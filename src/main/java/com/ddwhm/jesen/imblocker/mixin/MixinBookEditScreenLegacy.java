package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public class MixinBookEditScreenLegacy {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void postInit(CallbackInfo info) {
        ImBlocker.LOGGER.debug("BookEditScreen.<init>");
        WidgetManager.updateWidgetStatus(this, true);
    }

    @Inject(at = @At("RETURN"), method = "finalizeBook")
    private void preFinalizeBook(CallbackInfo info) {
        ImBlocker.LOGGER.debug("BookEditScreen.finalizeBook");
        WidgetManager.updateWidgetStatus(this, false);
    }

    @Inject(at = @At("RETURN"), method = "tick", remap = false)
    private void postTick(CallbackInfo ci) {
        WidgetManager.updateLifeTime(this);
    }
}