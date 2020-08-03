package com.ddwhm.jesen.imblocker.mixin;
import com.ddwhm.jesen.imblocker.IMManager;
import com.ddwhm.jesen.imblocker.ImBlocker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ScreenMixin {

    @Inject(at = @At("HEAD"), method = "openScreen")
    private void openScreenMixin(Screen screen,CallbackInfo info) {
        if (screen == null) {
            IMManager.makeOff();
        }
//        System.out.println("GUI close -> Off InputMethod.");
    }
}
