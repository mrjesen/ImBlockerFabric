package com.ddwhm.jesen.imblocker;


import net.fabricmc.api.ClientModInitializer;


public class ImBlocker implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            ImManager.flag = 200;
        } else {
            ImManager.flag = 500;
        }
    }

}
