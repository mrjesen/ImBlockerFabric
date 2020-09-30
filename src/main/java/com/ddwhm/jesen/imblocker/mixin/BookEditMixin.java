package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public class BookEditMixin {

    @Inject(at = @At("RETURN"), method = "<init>*")
    private void onConstructed(CallbackInfo info) {
        IMManager.makeOn();
//        System.out.println("Opened IM!");
    }

    @Inject(at = @At("RETURN"), method = "finalizeBook")
    private void onFinalBook(CallbackInfo info) {
        IMManager.makeOff();
    }
}
