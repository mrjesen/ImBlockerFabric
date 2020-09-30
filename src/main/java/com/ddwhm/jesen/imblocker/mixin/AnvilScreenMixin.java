package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Inject(at = @At("RETURN"), method = "setup")
    private void onSetup(CallbackInfo info) {
        IMManager.makeOff();
        System.out.println("Anvil Setup Off");
    }

    @Inject(at = @At("RETURN"), method = "onSlotUpdate")
    private void onSlotUpdateMixin(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo info) {
        if (slotId == 0 && stack.isEmpty()) {
            IMManager.makeOff();
            //System.out.println("Anvil onStart Off");
        }
        if (slotId == 0 && !stack.isEmpty()) {
            IMManager.makeOn();
            //System.out.println("Anvil notEmpty On");
        }

    }
}
