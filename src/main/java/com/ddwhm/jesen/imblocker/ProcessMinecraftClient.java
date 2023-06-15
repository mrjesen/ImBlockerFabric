package com.ddwhm.jesen.imblocker;

import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.gui.screen.Screen;

public class ProcessMinecraftClient {
    public static void ProcessScreen(Screen screen) {
        if (screen == null) {
            // 关闭 GUI 时关闭输入法
            ImBlocker.LOGGER.info("MixinMinecraftClientAfter16.setScreen null");
            WidgetManager.clear();
        }else{
            if(screen.toString().contains("SignEditScreen")){
                ImBlocker.LOGGER.info("MixinMinecraftClientBefore16.preOpenScreen: screen is SignEditScreen");
                WidgetManager.updateWidgetStatus("SignEditScreen", true);
            }else if(screen.toString().contains("BookEditScreen")){
                ImBlocker.LOGGER.info("MixinMinecraftClientBefore16.preOpenScreen: screen is BookEditScreen");
                WidgetManager.updateWidgetStatus("BookEditScreen", true);
            }else if(screen.toString().contains("AnvilScreen")){
                ImBlocker.LOGGER.info("MixinMinecraftClientBefore16.preOpenScreen: screen is AnvilScreen");
                WidgetManager.updateWidgetStatus("AnvilScreen", true);
            }else if(screen.toString().contains("screen.ChatScreen")){
                ImBlocker.LOGGER.info("MixinMinecraftClientBefore16.preOpenScreen: screen is ChatScreen");
                WidgetManager.updateWidgetStatus("ChatScreen", true);
            }else{
                ImBlocker.LOGGER.info("MixinMinecraftClientBefore16.setScreen: " + screen);
            }
        }
    }
}
