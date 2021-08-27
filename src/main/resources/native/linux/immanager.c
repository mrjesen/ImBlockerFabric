// gcc immanager.c -shared -I ~/.local/jdk1.8.0_202/include -I ~/.local/jdk1.8.0_202/include/linux/ -o immanager.so -fPIC -masm=intel
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdint.h>

#include <elf.h>
#include <unistd.h>
#include <sys/mman.h>
#include <inttypes.h>
#include <X11/Xlib.h>
#include "com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager.h"

static int (*oldXFilterEvent)(void *, void *);
static void **got_addr;
static bool enable = false;
static bool hooked = false;

// #define DEBUG

#ifdef DEBUG
#define my_fprintf fprintf
#else
int my_fprintf(FILE *stream, const char *fmt, ...)
{
    return 0;
}
#endif

static void *get_module_addr(pid_t pid, const char *module_name, bool end)
{
    FILE *fp;
    void *start_addr = NULL, *end_addr = NULL;
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
        //my_fprintf(stderr, "%s\n", line);
        if (NULL != strstr(line, module_name) &&
            sscanf(line, "%" PRIxPTR "-%" PRIxPTR " %*4s 00000000", &start_addr, &end_addr) == 2)
            break;
    }
    fclose(fp);
    return end ? end_addr : start_addr;
}

static bool myXFilterEvent(XEvent *event, void *w);

static void checkHook()
{
    // 若是 hook 发生在第一次解析 got 表前，则 oldXFilterEvent 会指向 XFilterEvent 的 plt 表
    // 这时调用 oldXFilterEvent 会导致之前的 hook 失效，因此需要检查并更新 oldXFilterEvent
    // 这么写可能存在条件竞争（但是我懒得改了）
    if (*got_addr != (void *)&myXFilterEvent)
    {
        oldXFilterEvent = *got_addr;
        *got_addr = (void *)&myXFilterEvent;
    }
}

static bool lastXFilterEventResult;
static int last_keycode;
static bool myXFilterEvent(XEvent *event, void *w)
{
    static uint64_t last_xkey_time = 0;
    // 核心思路为判断当前事件是否为键盘事件，如果是键盘事件并且锁定输入法时则会阻止事件传递
    // 否则传递所有事件
    bool ret = true;
    if (event->xkey.type == KeyPress || event->xkey.type == KeyRelease) // 键盘事件, 这段从 IDA 抄的
    {
        
        last_keycode = event->xkey.keycode;

        if (enable)
        {
            ret = oldXFilterEvent((void *)event, w);
            if (last_xkey_time == event->xkey.time) // event.xkey.time
            {
                // 修改 time 主要用于绕过 glfw 的 time 检查，让它能响应多次 XFilterEvent 
                event->xkey.time += 1;
            }
            last_xkey_time = event->xkey.time; 
        }
        lastXFilterEventResult = ret;
        my_fprintf(stderr, "oldXFilterEvent result: %d keycode: %d type: %d xkey_time: %d\n", ret, last_keycode, event->type, last_xkey_time);
    }
    else
    {
        ret = oldXFilterEvent((void *)event, w);
    }

    checkHook();
    return ret;
}
static const uint8_t MAGIC[] = "\xB9\x01\x00\x00\x00\x44\x89\xEA\x44\x89\xCE\xE8";

static uint8_t patch_shellcode[] = "\x48\xb8\x66\x55\x44\x33\x22\x11\x00\x00\xff\xd0\x90\x90\x90\x90";

static void *glfwInputKeyAddr = NULL;

static void myGlfwInputKey()
{
    if (!enable || (enable && !lastXFilterEventResult))
    {
        asm volatile(
            "leave;"
            "mov ecx, 1;"
            "mov edx, r13d;"
            "mov esi, r9d;"
            "jmp rax"
            :
            : "a"(glfwInputKeyAddr));
    }
    else
    {
        my_fprintf(stderr, "myGlfwInputKey result: %d keycode: %d\n", lastXFilterEventResult, last_keycode);
    }
    return;
}

static void *find_magic(void *base_addr, void *end_addr, const void *magic, size_t size)
{
    void *ret = NULL;
    for (uint8_t *addr = (uint8_t *)base_addr; addr + size < (uint8_t *)end_addr; ++addr)
    {
        if (memcmp(addr, magic, size) == 0)
        {
            ret = addr;
            break;
        }
    }
    return ret;
}

static void thread_safe_patch(void *addr, const void *code, size_t size)
{
    if (size == 1)
    {
        *(uint8_t *)addr = *(uint8_t *)code;
    }
    else if (size == 2)
    {
        *(uint16_t *)addr = *(uint16_t *)code;
    }
    else
    {
        *(uint16_t *)addr = 0xfeeb; // jump self
        memcpy((void *)((uint8_t *)addr + 2), (void *)((uint8_t *)code + 2), size - 2);
        *(uint16_t *)addr = *(uint16_t *)code;
    }
}

static void patch()
{
    void *base_addr = get_module_addr(-1, "libglfw.so", false);
    void *end_addr = get_module_addr(-1, "libglfw.so", true);
    void *magic_addr = find_magic(base_addr, end_addr, MAGIC, sizeof(MAGIC) - 1);
    my_fprintf(stderr, "find magic from %p to %p result: %p\n", base_addr, end_addr, magic_addr);
    my_fprintf(stderr, "addr %p info:\n", (uint8_t *)base_addr + 0x18B70);
    for (int i = 0; i < sizeof(MAGIC) - 1; ++i)
    {
        my_fprintf(stderr, "%02X ", *((uint8_t *)base_addr + 0x18B70 + i));
    }
    my_fprintf(stderr, "\n");

    if (magic_addr != NULL)
    {
        size_t page_size = getpagesize();
        void *addr_to_write = magic_addr;
        glfwInputKeyAddr = (void *)(*(int32_t *)((uint8_t *)magic_addr + sizeof(MAGIC) - 1) + (uint8_t *)magic_addr + sizeof(MAGIC) - 2 + 5);
        my_fprintf(stderr, "glfwInputKeyAddr: %p\n", glfwInputKeyAddr);
        void *myGlfwInputKeyAddr = (void *)&myGlfwInputKey;
        memcpy(&patch_shellcode[2], &myGlfwInputKeyAddr, 6);
        mprotect((void *)((size_t)addr_to_write & 0xfffffffffffff000), page_size, PROT_READ | PROT_WRITE | PROT_EXEC);
        thread_safe_patch(addr_to_write, patch_shellcode, sizeof(patch_shellcode) - 1);
        mprotect((void *)((size_t)addr_to_write & 0xfffffffffffff000), page_size, PROT_READ | PROT_EXEC);
    }
}

static void hook()
{
    size_t base_addr = (size_t)get_module_addr(-1, "libglfw.so", false);
    my_fprintf(stderr, "base_addr address : %p\n", base_addr);

    Elf64_Ehdr *header = (Elf64_Ehdr *)(base_addr);
    if (memcmp(header->e_ident, "\177ELF", 4) != 0)
    {
        return;
    }
    //基址 + Elf32_Off e_phoff_PROGRAM_HEADER_OFFSET_IN_FILE(e_phoff)的值 = program_header_table的地址
    Elf64_Phdr *phdr_table = (Elf64_Phdr *)(base_addr + header->e_phoff);
    if (phdr_table == 0)
    {
        my_fprintf(stderr, "phdr_table address : 0\n");
        return;
    }
    size_t phdr_count = header->e_phnum;
    my_fprintf(stderr, "phdr_count : %p\n", phdr_count);
    //遍历program header table p_type等于PT_DYNAMIC即为dynameic,获取到p_offset
    size_t p_vaddr = 0;
    size_t p_memsz = 0;
    int j = 0;
    for (j = 0; j < phdr_count; j++)
    {
        if (phdr_table[j].p_type == PT_DYNAMIC)
        {
            my_fprintf(stderr, "phdr_table[j].p_vaddr : %p\n", phdr_table[j].p_vaddr);
            p_vaddr = phdr_table[j].p_vaddr + base_addr;
            p_memsz = phdr_table[j].p_memsz;
            break;
        }
    }
    my_fprintf(stderr, "p_vaddr : %p\n", p_vaddr);
    my_fprintf(stderr, "p_memsz : %p\n", p_memsz);
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
    my_fprintf(stderr, "jmpRelOff : %p\n", jmpRelOff);
    my_fprintf(stderr, "strTabOff : %p\n", strTabOff);
    my_fprintf(stderr, "symTabOff : %p\n", symTabOff);

    //遍历查找要hook的导入函数
    for (size_t i = 0; i < pltRelSz; i++)
    {
        my_fprintf(stderr, "i=%p rel_table=%p\n", i, rel_table);
        size_t ndx = ELF64_R_SYM(rel_table[i].r_info);
        my_fprintf(stderr, "i=%p ndx=%p\n", i, ndx);
        Elf64_Sym *symTableIndex = (Elf64_Sym *)(ndx * sizeof(Elf64_Sym) + symTabOff);
        my_fprintf(stderr, "i=%p symTableIndex=%p\n", i, symTableIndex);
        char *funcName = (char *)(symTableIndex->st_name + strTabOff);
        my_fprintf(stderr, "i=%p funcName=%p\n", i, funcName);
        if (memcmp(funcName, "XFilterEvent", sizeof("XFilterEvent")) == 0)
        {
            size_t page_size = getpagesize();
            got_addr = (void **)(rel_table[i].r_offset + base_addr);
            oldXFilterEvent = *got_addr;
            my_fprintf(stderr, "old_function=%p new_function=%p\n", oldXFilterEvent, myXFilterEvent);
            my_fprintf(stderr, "got_addr=%p, page size=%p\n", got_addr, page_size);
            mprotect((void *)((size_t)got_addr & 0xfffffffffffff000), page_size, PROT_READ | PROT_WRITE);
            *got_addr = myXFilterEvent;
            break;
        }
        if (i == pltRelSz - 1)
        {
            my_fprintf(stderr, "can't found XFilterEvent addr.\n");
        }
    }
    return;
}

JNIEXPORT void JNICALL Java_com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager_disableIme(JNIEnv *jniEnv, jobject obj)
{
    // fputs("disableIme", stderr);
    if (!hooked)
    {
        hooked = true;
        hook();
        patch();
    }
    enable = false;
}

JNIEXPORT void JNICALL Java_com_ddwhm_jesen_imblocker_immanager_linux_LinuxImManager_enableIme(JNIEnv *jniEnv, jobject obj)
{
    // fputs("enableIme", stderr);
    if (!hooked)
    {
        hooked = true;
        hook();
        patch();
    }
    enable = true;
}
