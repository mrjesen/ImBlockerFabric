package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.TextFieldWidgetInvoker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickableWidget.class)
public abstract class MixinAbstractButtonWidget{

    @Shadow
    private boolean focused;
    // Fuck remap
    @Inject(method = {"setFocused", "method_25365"}, at = @At("RETURN"))
    private void postSetFocused(boolean selected, CallbackInfo info) {
        // 采用文本判断考虑到一般别的 mod 的 textWidget 名字里一般带有 text
        if (this instanceof TextFieldWidgetInvoker) {
            ImBlocker.LOGGER.debug("AbstractButtonWidget.setFocused");
            ((TextFieldWidgetInvoker) this).updateWidgetStatus();
        } else if (this.getClass().getName().toLowerCase().contains("text")) {
            ImBlocker.LOGGER.debug("AbstractButtonWidget.setFocused");
            WidgetManager.updateWidgetStatus("TextFieldWidget", selected);
        }
    }

    @Inject(method = "isFocused", at = @At("RETURN"))
    private void postIsFocused(CallbackInfoReturnable<Boolean> cir) {
        // 更新 Widget 存活时间
        // 至于其它的两个 inject 则是为了切换的更加迅速
        if (!(this instanceof TextFieldWidgetInvoker) && !this.getClass().getName().toLowerCase().contains("text")) {
            return;
        }
        ImBlocker.LOGGER.debug("AbstractButtonWidget.isFocused");
        WidgetManager.updateLifeTime("TextFieldWidget");
    }

    @Inject(method = "getNavigationPath", at = @At("RETURN"))
    private void postChangeFocus(GuiNavigation navigation, CallbackInfoReturnable<GuiNavigationPath> cir) {
        if (this instanceof TextFieldWidgetInvoker) {
            ImBlocker.LOGGER.debug("AbstractButtonWidget.changeFocus");
            ((TextFieldWidgetInvoker) this).updateWidgetStatus();
        } else if (this.getClass().getName().toLowerCase().contains("text")) {
            ImBlocker.LOGGER.debug("AbstractButtonWidget.changeFocus");
            WidgetManager.updateWidgetStatus("TextFieldWidget", this.focused);
        }
    }
}
