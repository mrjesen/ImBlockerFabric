package com.ddwhm.jesen.imblocker.mixin.rei;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"me.shedaniel.rei.gui.widget.TextFieldWidget", "me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget"}, remap = false)
public abstract class MixinTextFieldWidget {

    @Dynamic
    // Fuck remap
    @Inject(at = @At("HEAD"), method = {"setFocused", "method_25365"})
    private void presetFocused(boolean selected, CallbackInfo info) {
        ImBlocker.LOGGER.debug("ReiTextFieldWidget.setFocused");
        WidgetManager.updateWidgetStatus("rei.TextFieldWidget", selected);
    }
    @Dynamic
    // Fuck remap
    @Inject(method = {"isFocused", "method_25370"}, at = @At("RETURN"))
    private void postIsFocused(CallbackInfoReturnable<Boolean> cir) {
        // 更新 Widget 存活时间
        ImBlocker.LOGGER.debug("ReiTextFieldWidget.isFocused");
        WidgetManager.updateWidgetStatus("rei.TextFieldWidget", cir.getReturnValue());
    }
}
