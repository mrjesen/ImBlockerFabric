package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public class ImBlockerMixin {
    @Inject(at = @At("RETURN"), method = "<init>*")
    private void onConstructed(CallbackInfo info) {
        IMManager.makeOn();
//		System.out.println("Opened IM!");
    }


}

