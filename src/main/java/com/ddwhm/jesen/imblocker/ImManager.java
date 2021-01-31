package com.ddwhm.jesen.imblocker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class ImManager {
    public enum Status {
        INIT, ON, OFF
    }

    public static Status status = Status.INIT;

    public enum OS {
        WINDOWS, OTHERS
    }

    public static OS os = OS.OTHERS;

    private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

    private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

    private static native WinNT.HANDLE ImmCreateContext();

    private static native boolean ImmDestroyContext(WinNT.HANDLE himc);

    static {
        Native.register("imm32");
    }

    private static final User32 u = User32.INSTANCE;


    public static void makeOn() {
        ImBlocker.LOGGER.debug("status:{} makeOn", ImManager.status);
        if (ImManager.os != OS.WINDOWS || ImManager.status == Status.ON) {
            return;
        }
        ImManager.status = Status.ON;
        WinDef.HWND hwnd = u.GetForegroundWindow();
        WinNT.HANDLE himc = ImmGetContext(hwnd);
        if (himc == null) {
            himc = ImmCreateContext();
            ImmAssociateContext(hwnd, himc);
        }
        ImmReleaseContext(hwnd, himc);
    }

    public static void makeOff() {
        ImBlocker.LOGGER.debug("status:{} makeOff", ImManager.status);
        if (ImManager.os != OS.WINDOWS || ImManager.status == Status.OFF) {
            return;
        }
        ImManager.status = Status.OFF;
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

