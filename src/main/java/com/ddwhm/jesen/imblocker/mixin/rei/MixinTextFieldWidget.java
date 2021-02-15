package com.ddwhm.jesen.imblocker.mixin.rei;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
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
    @Inject(at = @At("HEAD"), method = "setFocused", remap = false)
    private void presetFocused(boolean selected, CallbackInfo info) {
        ImBlocker.LOGGER.debug("AbstractButtonWidget.setFocused");
        if (selected) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }

    @Inject(at = @At("RETURN"), method = "changeFocus")
    private void postChangeFocus(boolean lookForwards, CallbackInfoReturnable<Boolean> cir) {
        ImBlocker.LOGGER.debug("AbstractButtonWidget.changeFocus");
        if (cir.getReturnValue()) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }
}
