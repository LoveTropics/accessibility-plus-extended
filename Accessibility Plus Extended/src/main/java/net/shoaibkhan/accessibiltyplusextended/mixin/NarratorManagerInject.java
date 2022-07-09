package net.shoaibkhan.accessibiltyplusextended.mixin;

import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;

import java.util.UUID;

@Mixin(NarratorChatListener.class)
public class NarratorManagerInject {

  @Inject(at = @At("HEAD"), method = "narrate(Ljava/lang/String;)V", cancellable = true)
  public void sayWithNVDA(String message, CallbackInfo ci) {
    if (NarratorPlus.isNVDALoaded()) {
      NarratorPlus.narrate(message);
      ci.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
  public void onChatMessage(ChatType messageType, Component message, UUID sender, CallbackInfo ci) {
    String option = NarratorPlus.chatOptions[Config.getInt(ConfigKeys.CHAT_NARRATION.getKey())];

    switch (option){
      case "on": {
        if (NarratorPlus.isNVDALoaded()) {
          Object text2;
          if (message instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent) message).getKey())) {
            text2 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent) message).getArgs());
          } else {
            text2 = message;
          }

          String string = ((Component) text2).getString();
          NarratorPlus.narrate(string);
          ci.cancel();
        }
        break;
      }
      case "off": {
        ci.cancel();
        break;
      }
      case "default":{
        break;
      }
    }
  }
}
