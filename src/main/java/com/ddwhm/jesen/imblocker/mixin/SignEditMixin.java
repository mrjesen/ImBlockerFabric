package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class SignEditMixin {

    @Inject(at = @At("RETURN"), method = "<init>*")
    private void onConstructed(CallbackInfo info) {
        IMManager.makeOn();
//        System.out.println("Opened IM!");
    }

    @Inject(at = @At("RETURN"), method = "finishEditing")
    private void onFinishEditing(CallbackInfo info) {
        IMManager.makeOff();
    }
}
