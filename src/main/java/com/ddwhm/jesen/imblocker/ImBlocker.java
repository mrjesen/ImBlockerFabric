package com.ddwhm.jesen.imblocker;


import com.ddwhm.jesen.imblocker.immanager.DummyImManager;
import com.ddwhm.jesen.imblocker.immanager.ImManager;
import net.fabricmc.api.ClientModInitializer;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ImBlocker implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("ImBlocker");
    public static ImManager imManager;

    static {
        String className;
        if (SystemUtils.IS_OS_LINUX) {
            className = "com.ddwhm.jesen.imblocker.immanager.linux.LinuxImManager";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            className = "com.ddwhm.jesen.imblocker.immanager.windows.WindowsImManager";
        } else {
            className = "com.ddwhm.jesen.imblocker.immanager.DummyImManager";
        }

        try {
            imManager = (ImManager) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            imManager = new DummyImManager();
        }
    }

    @Override
    public void onInitializeClient() {
        // Configurator.setLevel(LOGGER.getName(), Level.toLevel("DEBUG"));
    }
}
