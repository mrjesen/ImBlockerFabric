package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    boolean firstSetScreen = false;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void preOpenScreen(Screen screen, CallbackInfo info) {
        if (!firstSetScreen) {
            ImBlocker.imManager.makeOff();
            firstSetScreen = false;
        }
        if (screen == null) {
            // 关闭 GUI 时关闭输入法
            ImBlocker.LOGGER.debug("MixinMinecraftClient.openScreen");
            WidgetManager.clear();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        // 每 tick + 1，超过 1s 没动作的 widget 则移除
        WidgetManager.tick();
    }
}
