package net.shoaibkhan.accessibiltyplusextended.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;

@Mixin(value=ItemStack.class,priority = 0)
public class DurabilityMixin {
	@Inject(at = @At("RETURN"), method = "getTooltip")
	private void getTooltipMixin(Player player, TooltipFlag context,CallbackInfoReturnable<List<Component>> info) throws Exception {
		if(Minecraft.getInstance().level == null) return;
		List<Component> list = info.getReturnValue();
		ItemStack itemStack = (ItemStack) ((Object) this);
		
//		if(HudRenderCallBackClass.isTradeScreenOpen) {
//			MutableText mutableText = new LiteralText("").append(list.get(0));
//			list.set(0, new LiteralText(itemStack.getCount() + " " + mutableText.getString()) );
//		}
		
		if (Config.get(ConfigKeys.DURABILITY_TOOL_TIP_KEY.getKey()) && Config.get(ConfigKeys.DURABILITY_CHECK_KEY.getKey())) {
			if (itemStack.getItem().canBeDepleted()) {
				int totalDurability = itemStack.getItem().getMaxDamage();
				int currrRemainingDurability = totalDurability - itemStack.getDamageValue();
                list.add(1, (new TranslatableComponent("narrate.apextended.durability", currrRemainingDurability, totalDurability).withStyle(ChatFormatting.GREEN)));
			}
		}
	}
}
