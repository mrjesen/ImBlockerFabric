package com.ddwhm.jesen.imblocker;


import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;


public class ImBlocker implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("ImBlocker");

    @Override
    public void onInitializeClient() {
        // Configurator.setLevel(LOGGER.getName(), Level.toLevel("DEBUG"));
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            ImManager.os = ImManager.OS.WINDOWS;
        } else {
            ImManager.os = ImManager.OS.OTHERS;
        }
    }
}
