package com.ddwhm.jesen.imblocker.mixin.libgui;

import com.ddwhm.jesen.imblocker.ImBlocker;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "io.github.cottonmc.cotton.gui.widget.WTextField", remap = false)
public class MixinWTextField {
    @Dynamic
    @Inject(method = "onFocusGained", at = @At("RETURN"))
    private void postOnFocusGained(CallbackInfo info) {
        ImBlocker.LOGGER.debug("MixinWTextField.onFocusGained");
        ImBlocker.imManager.makeOn();
    }
}
