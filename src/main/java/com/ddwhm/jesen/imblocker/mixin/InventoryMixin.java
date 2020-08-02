package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CreativeInventoryScreen.class)
public class InventoryMixin {
    @Inject(at = @At("RETURN"), method = "<init>*")
    private void CreativeInventoryScreenMixin(CallbackInfo info) {
        //生存模式物品栏
        System.out.println("survival off");
        IMManager.makeOff();
    }
}
