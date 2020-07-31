package com.ddwhm.jesen.imblocker;

import com.ddwhm.jesen.imblocker.IMManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding;

public class ImBlocker implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	private static KeyBinding kb;
	@Override
	public void onInitialize() {
        
	}
}
