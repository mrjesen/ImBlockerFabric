package com.ddwhm.jesen.imblocker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class IMManager {
    public static int flag = 0;
    /*
        0 初始化
        200 WINDOWS
        500 NOT WINDOWS
        2000 Off
        2001 On

    */


    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    static {
        Native.register("imm32");
    }

    private static User32 u = User32.INSTANCE;


    public static void makeOn() {
        if (IMManager.flag == 2001 || IMManager.flag == 500) {
            return;
        }
        IMManager.flag = 2001;
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    public static void makeOff() {
        if (IMManager.flag == 2000 || IMManager.flag == 500) {
            return;
        }
        System.out.println("Stop IM");
        IMManager.flag = 2000;
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc != null) {
            ImmDestroyContext(himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    public static boolean toggle() {
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmAssociateContext(hwnd, null);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
            ImmReleaseContext(hwnd, himc);
            return true;
        }
        ImmReleaseContext(hwnd, himc);
        return false;
    }
}

