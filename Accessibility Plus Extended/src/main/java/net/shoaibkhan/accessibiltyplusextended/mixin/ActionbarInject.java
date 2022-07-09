package net.shoaibkhan.accessibiltyplusextended.mixin;

import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.config.Config;

@Mixin(Gui.class)
public class ActionbarInject {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    public void speakActionbar(Component message, boolean tinted, CallbackInfo ci) {
        if(Config.get(ConfigKeys.ATION_BAR_KEY.getKey())){
            NarratorPlus.narrate(message.getString());
        }
    }
}
