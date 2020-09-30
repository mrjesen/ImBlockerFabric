package com.ddwhm.jesen.imblocker;


import net.fabricmc.api.ClientModInitializer;


public class ImBlocker implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // System.out.println("1");
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            IMManager.flag = 200;
        } else {
            IMManager.flag = 500;
        }
    }

}
