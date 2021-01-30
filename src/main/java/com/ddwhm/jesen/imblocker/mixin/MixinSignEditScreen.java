package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void postInit(CallbackInfo info) {
        ImBlocker.LOGGER.debug("SignEditScreen.<init>");
        ImManager.makeOn();
    }

    @Inject(at = @At("RETURN"), method = "finishEditing")
    private void postFinishEditing(CallbackInfo info) {
        ImBlocker.LOGGER.debug("SignEditScreen.finishEditing");
        ImManager.makeOff();
    }
}
