#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <elf.h>
#include <unistd.h>
#include <sys/mman.h>
#include <inttypes.h>
#include "com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager.h"

int (*oldXFilterEvent)(void *, void *);
void **got_addr;
static bool enable = false;
static bool hooked = false;

typedef union {
    int type;
    long pad[24];
} XEvent;

void *get_module_base(pid_t pid, const char *module_name)
{
    FILE *fp;
    long addr = 0;
    char filename[32] = "\n";
    char line[1024] = "\n";
    if (pid < 0)
    {
        snprintf(filename, sizeof(filename), "/proc/self/maps");
    }
    else
    {
        snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    }
    // 获取指定pid进程加载的内存模块信息
    fp = fopen(filename, "r");
    while (fgets(line, sizeof(line), fp))
    {
        fprintf(stderr, "%s\n", line);
        if (NULL != strstr(line, module_name) &&
            sscanf(line, "%" PRIxPTR "-%*lx %*4s 00000000", &addr) == 1)
            break;
    }
    fclose(fp);
    return (void *)addr;
}

bool myXFilterEvent(XEvent *event, void *w);

void checkHook() {
    // 若是 hook 发生在第一次解析 got 表前，则 oldXFilterEvent 会指向 XFilterEvent 的 plt 表
    // 这时调用 oldXFilterEvent 会导致之前的 hook 失效，因此需要检查并更新 oldXFilterEvent
    // 这么写可能存在条件竞争（但是我懒得改了）
    if (*got_addr != (void*)&myXFilterEvent) {
        oldXFilterEvent = *got_addr;
        *got_addr = (void*)&myXFilterEvent;
    }
}

bool myXFilterEvent(XEvent *event, void *w)
{
    // 核心思路为判断当前事件是否为键盘事件，如果是键盘事件并且锁定输入法时则会阻止事件传递
    // 否则传递所有事件
    bool ret = true;
    if (enable)
    {
        ret = oldXFilterEvent((void*)event, w);
    }
    else
    {
        if (event->type - 2 > 1) // 非键盘事件则传递，键盘事件屏蔽，我也不知道对应的常量是多少，这段从 IDA 抄的
        {
            ret = oldXFilterEvent((void*)event, w);
        }
    }
    checkHook();
    return ret;
}

void hook()
{
    size_t base_addr = (size_t)get_module_base(-1, "libglfw.so");
    fprintf(stderr, "base_addr address : %p\n", base_addr);

    Elf64_Ehdr *header = (Elf64_Ehdr *)(base_addr);
    if (memcmp(header->e_ident, "\177ELF", 4) != 0)
    {
        return;
    }
    //基址 + Elf32_Off e_phoff_PROGRAM_HEADER_OFFSET_IN_FILE(e_phoff)的值 = program_header_table的地址
    Elf64_Phdr *phdr_table = (Elf64_Phdr *)(base_addr + header->e_phoff);
    if (phdr_table == 0)
    {
        fprintf(stderr, "phdr_table address : 0\n");
        return;
    }
    size_t phdr_count = header->e_phnum;
    fprintf(stderr, "phdr_count : %p\n", phdr_count);
    //遍历program header table p_type等于PT_DYNAMIC即为dynameic,获取到p_offset
    size_t p_vaddr = 0;
    size_t p_memsz = 0;
    int j = 0;
    for (j = 0; j < phdr_count; j++)
    {
        if (phdr_table[j].p_type == PT_DYNAMIC)
        {
            fprintf(stderr, "phdr_table[j].p_vaddr : %p\n", phdr_table[j].p_vaddr);
            p_vaddr = phdr_table[j].p_vaddr + base_addr;
            p_memsz = phdr_table[j].p_memsz;
            break;
        }
    }
    fprintf(stderr, "p_vaddr : %p\n", p_vaddr);
    fprintf(stderr, "p_memsz : %p\n", p_memsz);
    Elf64_Dyn *dynamic_table = (Elf64_Dyn *)(p_vaddr);
    size_t jmpRelOff = 0;
    size_t strTabOff = 0;
    size_t pltRelSz = 0;
    size_t symTabOff = 0;
    size_t dynCount = p_memsz / sizeof(Elf64_Dyn);
    for (size_t i = 0; i < dynCount; i++)
    {
        size_t val = dynamic_table[i].d_un.d_val;
        switch (dynamic_table[i].d_tag)
        {
        case DT_JMPREL:
            jmpRelOff = val;
            break;
        case DT_STRTAB:
            strTabOff = val;
            break;
        case DT_PLTRELSZ:
            pltRelSz = val / sizeof(Elf64_Rel);
            break;
        case DT_SYMTAB:
            symTabOff = val;
            break;
        }
    }
    Elf64_Rel *rel_table = (Elf64_Rel *)jmpRelOff;
    fprintf(stderr, "jmpRelOff : %p\n", jmpRelOff);
    fprintf(stderr, "strTabOff : %p\n", strTabOff);
    fprintf(stderr, "symTabOff : %p\n", symTabOff);

    //遍历查找要hook的导入函数
    for (size_t i = 0; i < pltRelSz; i++)
    {
        fprintf(stderr, "i=%p rel_table=%p\n", i, rel_table);
        size_t ndx = ELF64_R_SYM(rel_table[i].r_info);
        fprintf(stderr, "i=%p ndx=%p\n", i, ndx);
        Elf64_Sym *symTableIndex = (Elf64_Sym *)(ndx * sizeof(Elf64_Sym) + symTabOff);
        fprintf(stderr, "i=%p symTableIndex=%p\n", i, symTableIndex);
        char *funcName = (char *)(symTableIndex->st_name + strTabOff);
        fprintf(stderr, "i=%p funcName=%p\n", i, funcName);
        if (memcmp(funcName, "XFilterEvent", sizeof("XFilterEvent")) == 0)
        {
            size_t page_size = getpagesize();
            got_addr = (void **)(rel_table[i].r_offset + base_addr);
            oldXFilterEvent = *got_addr;
            fprintf(stderr, "old_function=%p new_function=%p\n", oldXFilterEvent, myXFilterEvent);
            fprintf(stderr, "got_addr=%p, page size=%p\n", got_addr, page_size);
            mprotect((void *)((size_t)got_addr & 0xfffffffffffff000), page_size, PROT_READ | PROT_WRITE);
            *got_addr = myXFilterEvent;
            break;
        }
        if (i == pltRelSz - 1) {
            fprintf(stderr, "can't found XFilterEvent addr.\n");
        }
    }
    return;
}

JNIEXPORT void JNICALL Java_com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager_disableIme(JNIEnv *jniEnv, jobject obj)
{
    // fputs("disableIme", stderr);
    if (!hooked) {
        hooked = true;
        hook();
    }
    enable = false;
}

JNIEXPORT void JNICALL Java_com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager_enableIme(JNIEnv *jniEnv, jobject obj)
{
    // fputs("enableIme", stderr);
    if (!hooked) {
        hooked = true;
        hook();
    }
    enable = true;
}
