package com.ddwhm.jesen.imblocker.mixin.ftbquests;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"dev.ftb.mods.ftblibrary.ui.TextBox"}, remap = false)
public abstract class MixinQuestsTextBox {
    @Dynamic
    @Inject(at = @At("HEAD"), method = "setFocused")
    private void presetFocused(boolean setFocused) {
        ImBlocker.LOGGER.debug("FTBQuests.TextBox.setFocused");
        WidgetManager.updateWidgetStatus("ftbquests.TextBox", setFocused);
    }
    @Dynamic
    @Inject(method = "isFocused", at = @At("RETURN"))
    private void postIsFocused(CallbackInfoReturnable<Boolean> cir) {
        // 更新 Widget 存活时间
        ImBlocker.LOGGER.debug("FTBQuests.TextBox.isFocused");
        WidgetManager.updateWidgetStatus("ftbquests.TextBox", cir.getReturnValue());
    }
}