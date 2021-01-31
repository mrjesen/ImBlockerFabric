package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public class MixinBookEditScreen {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void postInit(CallbackInfo info) {
        ImBlocker.LOGGER.debug("BookEditScreen.<init>");
        ImManager.makeOn();
    }

    @Inject(at = @At("RETURN"), method = "finalizeBook")
    private void preFinalizeBook(CallbackInfo info) {
        ImBlocker.LOGGER.debug("BookEditScreen.finalizeBook");
        ImManager.makeOff();
    }
}
