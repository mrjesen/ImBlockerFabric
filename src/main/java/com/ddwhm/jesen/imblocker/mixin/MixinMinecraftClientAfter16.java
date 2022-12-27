package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClientAfter16 {

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void preOpenScreen(Screen screen, CallbackInfo info) {
        if(screen !=null && screen.getTitle().toString().contains("sign.edit")){
            ImBlocker.LOGGER.debug("MixinMinecraftClient.sign.edit");
            WidgetManager.updateWidgetStatus(this, true);
        }
    }

}
