package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatMixin {
    @Inject(at = @At("RETURN"), method = "<init>*")
    private void onConstructed(CallbackInfo info) {
        IMManager.makeOn();
        // System.out.println("Opened IM!");
    }
}
