package com.ddwhm.jesen.imblocker.mixin.rei;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import me.shedaniel.rei.gui.widget.TextFieldWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget extends WidgetWithBounds implements Tickable {

    @Inject(method = "setFocused", at = @At("HEAD"), remap = false)
    private void presetFocused(boolean selected, CallbackInfo info) {
        ImBlocker.LOGGER.debug("rei TextFieldWidget.setFocused");
        WidgetManager.updateWidgetStatus(this, selected);
    }

    @Inject(method = "isFocused", at = @At("RETURN"), remap = false)
    private void postIsFocused(CallbackInfoReturnable<Boolean> cir) {
        ImBlocker.LOGGER.debug("rei TextFieldWidget.isFocused");
        WidgetManager.updateLifeTime(this);
    }

    @Inject(method = "changeFocus", at = @At("RETURN"))
    private void postChangeFocus(boolean lookForwards, CallbackInfoReturnable<Boolean> cir) {
        ImBlocker.LOGGER.debug("rei TextFieldWidget.changeFocus");
        WidgetManager.updateWidgetStatus(this, cir.getReturnValue());
    }
}
