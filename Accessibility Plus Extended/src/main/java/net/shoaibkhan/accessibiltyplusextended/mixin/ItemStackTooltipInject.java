package net.shoaibkhan.accessibiltyplusextended.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.keyboard.KeyboardController;

@Mixin(value = ItemStack.class, priority = 0)
public class ItemStackTooltipInject {
  @Inject(at = @At("RETURN"), method = "getTooltip")
  private void getTooltipMixin(Player player, TooltipFlag context, CallbackInfoReturnable<List<Component>> info)
      throws Exception {
    if (Minecraft.getInstance().level == null)
      return;
    ItemStack itemStack = (ItemStack) ((Object) this);
    List<Component> list = info.getReturnValue();

    addCount: {
      if (!itemStack.isStackable() || itemStack.getItem().canBeDepleted())
        break addCount;

      MutableComponent mutableText = new TextComponent("").append(list.get(0));
      list.set(0, new TextComponent(itemStack.getCount() + " " + mutableText.getString()));
    }

    narrateToolTip: {
      if (KeyboardController.hasControlOverMouse())
        break narrateToolTip;
      String message = "";

      for (Component text : list) {
        message += text.getString() + ", ";
      }

      if (!NarratorPlus.previousToolTip.equals(message)) {
        NarratorPlus.narrate(message);
        NarratorPlus.previousToolTip = message;
      }
    }

  }
}
