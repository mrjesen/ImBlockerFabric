package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InventoryScreen.class)
public class InventoryMixin {
    @Inject(at = @At("RETURN"), method = "<init>*")
    private void inventoryScreenMixin(CallbackInfo info) {
        //生存模式物品栏
        System.out.println("survival off");
        IMManager.makeOff();
    }
}
