package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.DrawableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawableHelper.class)
public class DrawTextMixin {
    @Inject(at = @At("RETURN"), method = "drawTextWithShadow")
    private void retdrawTextWithShadow(CallbackInfo info) {
        IMManager.makeOff();
    }
}
