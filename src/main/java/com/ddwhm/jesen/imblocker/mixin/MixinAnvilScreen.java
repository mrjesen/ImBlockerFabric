package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen {
    @Inject(at = @At("RETURN"), method = "setup")
    private void postSetup(CallbackInfo info) {
        // fix #4
        ImBlocker.LOGGER.debug("AnvilScreen.setup");
        ImManager.makeOff();
    }

    @Inject(at = @At("RETURN"), method = "onSlotUpdate")
    private void postOnSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo info) {
        if (slotId != 0) {
            return;
        }
        ImBlocker.LOGGER.debug("AnvilScreen.onSlotUpdate");
        if (stack.isEmpty()) {
            ImManager.makeOff();
        } else {
            ImManager.makeOn();
        }
    }
}
