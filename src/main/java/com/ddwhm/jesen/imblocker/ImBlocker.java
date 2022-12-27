package com.ddwhm.jesen.imblocker;


import com.ddwhm.jesen.imblocker.immanager.DummyImManager;
import com.ddwhm.jesen.imblocker.immanager.ImManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

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
        // 配置键盘绑定
        KeyBinding keyIMBlocker = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.imblocker.switchIMEState", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.imblockerfabric.name"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyIMBlocker.wasPressed()) {
                if(imManager.getStatus()){
                    imManager.makeOff();
                }else{
                    imManager.makeOn();
                }
            }
        });
    }
}
