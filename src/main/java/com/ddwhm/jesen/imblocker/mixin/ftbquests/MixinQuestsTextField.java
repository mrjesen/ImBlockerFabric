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
@Mixin(targets = {"dev.ftb.mods.ftblibrary.ui.TextField"}, remap = false)
public abstract class MixinQuestsTextField {

    @Dynamic
    @Inject(at = @At("RETURN"), method = "<init>")
    private void postInit(boolean setFocused, CallbackInfoReturnable<Object> info) {
        ImBlocker.LOGGER.debug("FTBQuests.TextField.<init>");
        WidgetManager.updateWidgetStatus(this, true);
    }

}