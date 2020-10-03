package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractButtonWidget.class)
public class AbsButtonMixin {
    @Inject(at = @At("RETURN"), method = "setFocused")
    private void setFocusedMixin(boolean selected, CallbackInfo info) {
        if (selected) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }

    @Inject(at = @At("RETURN"), method = "isFocused")
    private void onIsFocused(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }


    }
}
