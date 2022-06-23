package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntityLegacy {

    @Inject(at = @At("RETURN"), method = "setEditable")
    private void postSetEditable(boolean editable, CallbackInfo info) {
        ImBlocker.LOGGER.debug("SignBlockEntity.setEditable");
        if(editable){
            WidgetManager.updateWidgetStatus(this, true);
        }else{
            WidgetManager.updateWidgetStatus(this, false);
        }
    }

}
