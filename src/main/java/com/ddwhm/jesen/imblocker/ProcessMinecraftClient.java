package com.ddwhm.jesen.imblocker;

import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.gui.screen.Screen;

public class ProcessMinecraftClient {

    public static void ProcessScreen(Screen screen) {
        if (screen == null) {
            // 关闭 GUI 时关闭输入法
            ImBlocker.LOGGER.info("MixinProcessScreen.setScreen null");
            WidgetManager.clear();
        }else{
            if(screen.getTitle().toString().contains("sign.edit")){
                ImBlocker.LOGGER.info("MixinProcessScreen.preOpenScreen: screen is SignEditScreen");
                WidgetManager.updateWidgetStatus("SignEditScreen", true);
            }else if(screen.toString().contains("class_473") || screen.toString().contains("BookEditScreen")){
                ImBlocker.LOGGER.info("MixinProcessScreen.preOpenScreen: screen is BookEditScreen");
                WidgetManager.updateWidgetStatus("BookEditScreen", true);
            }else if(screen.getTitle().toString().contains("container.repair")){
                ImBlocker.LOGGER.info("MixinProcessScreen.preOpenScreen: screen is AnvilScreen");
                WidgetManager.updateWidgetStatus("AnvilScreen", true);
            }else if(screen.getTitle().toString().contains("chat_screen.title") || screen.toString().contains("class_408") || screen.toString().contains("GuiChatOF") || screen.toString().contains("ChatScreen")){
                ImBlocker.LOGGER.info("MixinProcessScreen.preOpenScreen: screen is ChatScreen");
                WidgetManager.updateWidgetStatus("ChatScreen", true);
            }else{
                ImBlocker.LOGGER.info("MixinProcessScreen.setScreen: " + screen);
                ImBlocker.LOGGER.info("MixinProcessScreen.setScreenTitle: " + screen.getTitle().toString());
            }
        }
    }
}
