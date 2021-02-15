package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.ImManager;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractButtonWidget.class)
public abstract class MixinAbstractButtonWidget extends DrawableHelper implements Drawable, Element {

//    @Inject(at = @At("RETURN"), method = "<init>")
//    private void postInit(int x, int y, int width, int height, Text message, CallbackInfo ci) {
//        ImBlocker.LOGGER.debug("AbstractButtonWidget.<init> {}", this.getClass().getName());
//        if (!this.getClass().getName().toLowerCase().contains("text")) {
//            return;
//        }
//        ImBlocker.LOGGER.debug("AbstractButtonWidget.<init>");
//        ImManager.makeOn();
//    }

    @Inject(at = @At("HEAD"), method = "setFocused")
    private void presetFocused(boolean selected, CallbackInfo info) {
        // 采用文本判断考虑到一般别的 mod 的 textWidget 名字里一般带有 text
        if (!(((AbstractButtonWidget) (Object) this) instanceof TextFieldWidget) && !this.getClass().getName().toLowerCase().contains("text")) {
            return;
        }
        ImBlocker.LOGGER.debug("AbstractButtonWidget.setFocused");
        if (selected) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }

    @Inject(at = @At("RETURN"), method = "changeFocus")
    private void postChangeFocus(boolean lookForwards, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getClass().getName().toLowerCase().contains("text")) {
            return;
        }
        ImBlocker.LOGGER.debug("AbstractButtonWidget.changeFocus");
        if (cir.getReturnValue()) {
            ImManager.makeOn();
        } else {
            ImManager.makeOff();
        }
    }
}
