package com.ddwhm.jesen.imblocker.mixin.replay;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiTextField"}, remap = false)
public abstract class MixinAbstractGuiTextField {
    @Dynamic
    @Inject(at = @At("HEAD"), method = "setFocused")
    private void presetFocused(boolean setFocused, CallbackInfoReturnable<Object> info) {
        ImBlocker.LOGGER.debug("jGuiAbstractGuiTextField.setFocused");
        WidgetManager.updateWidgetStatus("replay.AbstractGuiTextField", setFocused);
    }
    @Dynamic
    @Inject(method = "isFocused", at = @At("RETURN"))
    private void postIsFocused(CallbackInfoReturnable<Boolean> cir) {
        // 更新 Widget 存活时间
        ImBlocker.LOGGER.debug("jGuiAbstractGuiTextField.isFocused");
        WidgetManager.updateWidgetStatus("replay.AbstractGuiTextField", cir.getReturnValue());
    }
}