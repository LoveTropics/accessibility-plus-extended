package net.shoaibkhan.accessibiltyplusextended.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;

import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.keyboard.KeyboardController;

@Mixin(Screen.class)
public class RenderTooltipInject {

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V")
    private void readOneLineTooltips(PoseStack matrices, Component text, int x, int y, CallbackInfo callback) {
        if (!Config.get(ConfigKeys.READ_TOOLTIPS_KEY.getKey()) || KeyboardController.hasControlOverMouse()) {
            return;
        }
        String nextText = text.getString();
        if (!NarratorPlus.getInstance().lastText.equals(nextText)) {
            NarratorPlus.getInstance().lastText = nextText;
            NarratorPlus.narrate(nextText);
        }
    }

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V")
    private void renderTooltip(PoseStack matrices, List<Component> lines, int x, int y, CallbackInfo callback) {
        if (!Config.get(ConfigKeys.READ_TOOLTIPS_KEY.getKey()) || KeyboardController.hasControlOverMouse()) {
            return;
        }
        if (lines.size() > 0) {
            String nextText = NarratorPlus.getInstance().prefixAmount;
            for (int i = 0; i < lines.size(); i++) {
                nextText += lines.get(i).getString() + ", ";
            }
            if (!NarratorPlus.getInstance().lastText.equals(nextText)) {
                NarratorPlus.getInstance().lastText = nextText;
                NarratorPlus.narrate(nextText);
            }
        }
    }
}