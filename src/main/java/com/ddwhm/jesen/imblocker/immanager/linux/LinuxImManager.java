package com.ddwhm.jesen.imblocker.immanager.linux;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.immanager.ImManager;
import com.sun.jna.Native;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

public class LinuxImManager implements ImManager {
    private boolean status = false;


    public LinuxImManager() {
        this.makeOff();
    }

    @SuppressWarnings({"unchecked"})
    private static void updateEnv(String name, String val) {
        Map<String, String> env = System.getenv();
        try {
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(name, val);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private native void disableIme();

    private native void enableIme();

    private static void loadJNI() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("native/linux/immanager.so");
        File file = null;
        file = File.createTempFile("lib", ".so");
        OutputStream os = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.close();
        System.load(file.getAbsolutePath());
        file.deleteOnExit();
    }

    static  {

        try {
            loadJNI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeOn() {
        ImBlocker.LOGGER.debug("status:{} makeOn", status);
        if (status) {
            return;
        }
        status = true;
        enableIme();
    }

    public void makeOff() {
        ImBlocker.LOGGER.debug("status:{} makeOff", status);
        if (!status) {
            return;
        }
        status = false;
        disableIme();
    }
}
