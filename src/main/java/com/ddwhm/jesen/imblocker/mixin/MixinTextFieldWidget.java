package com.ddwhm.jesen.imblocker.mixin;

import com.ddwhm.jesen.imblocker.ImBlocker;
import com.ddwhm.jesen.imblocker.util.CompatAbstractButtonWidget;
import com.ddwhm.jesen.imblocker.util.TextFieldWidgetInvoker;
import com.ddwhm.jesen.imblocker.util.WidgetManager;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget extends ClickableWidget implements Drawable, Element, TextFieldWidgetInvoker {
    @Shadow
    private boolean editable;

    public MixinTextFieldWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "setEditable", at = @At(value = "RETURN"))
    private void postSetEditable(boolean editable, CallbackInfo ci) {
        ImBlocker.LOGGER.debug("TextFieldWidget.setEditable");
        updateWidgetStatus();
    }

    @Override
    public void updateWidgetStatus() {
        // 铁砧之类会设置 editable
        if (this instanceof CompatAbstractButtonWidget) {
            WidgetManager.updateWidgetStatus(this, this.editable && ((CompatAbstractButtonWidget) this).oldIsFocused());
        } else {
            WidgetManager.updateWidgetStatus(this, this.editable && this.isFocused());
        }
    }
}
