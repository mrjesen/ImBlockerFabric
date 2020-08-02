package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.IMManager;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButtonWidget.class)
public class AbsButtonMixin {
    @Inject(at = @At("RETURN"), method = "setFocused")
	private void setFocusedMixin(boolean selected, CallbackInfo info) {
		if(selected){
			IMManager.makeOn();
		}else{
			IMManager.makeOff();
		}

	}
}
