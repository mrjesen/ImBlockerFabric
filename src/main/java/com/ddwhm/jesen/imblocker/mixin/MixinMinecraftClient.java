package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> implements SnooperListener, WindowEventHandler {

    public MixinMinecraftClient(String string) {
        super(string);
    }

    @Inject(at = @At("HEAD"), method = "openScreen")
    private void preOpenScreen(Screen screen, CallbackInfo info) {
        if (screen == null) {
            // 关闭 GUI 时关闭输入法
            ImBlocker.LOGGER.debug("MixinMinecraftClient.openScreen");
            ImManager.makeOff();
        }
    }
}
