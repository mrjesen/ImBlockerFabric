package com.ddwhm.jesen.imblocker;


import com.ddwhm.jesen.imblocker.immanager.DummyImManager;
import com.ddwhm.jesen.imblocker.immanager.ImManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class ImBlocker implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("IMBlocker");
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
        // 配置键盘绑定
        KeyBinding keyIMBlocker = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.imblocker.switchIMEState", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.imblockerfabric.name"));
        ScreenEvents.AFTER_INIT.register((MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) -> {
            if (screen != null) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
                    if (keyIMBlocker.matchesKey(key, scancode)){
                        if(imManager.getStatus()){
                            imManager.makeOff();
                        }else{
                            imManager.makeOn();
                        }
                    }
                });
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyIMBlocker.wasPressed()) {
                if(imManager.getStatus()){
                    imManager.makeOff();
                }else{
                    imManager.makeOn();
                }
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight)->{
//            ScreenEvents.beforeRender(screen).register((s,m,x,y,t)->{
//
//            });

        });
    }

}
