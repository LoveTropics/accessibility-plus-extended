package net.shoaibkhan.accessibiltyplusextended.features.withThreads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.modInit;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;

public class DurabilityThread extends Thread {
	private final Minecraft client;
	private double threshold;
	public static Integer[] thresholdArray = { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60,
			65, 70, 75, 80, 85, 90, 95 };

	public DurabilityThread() {
		this.client = Minecraft.getInstance();
		threshold = 25;
	}

	public void run() {
		try {
			threshold = thresholdArray[Config.getInt(ConfigKeys.DURABILITY_THRESHOLD_KEY.getKey())];
		} catch (Exception e) {
			threshold = 25;
		}

		try {
			assert this.client.player != null;
			 Inventory playerInventory = this.client.player.getInventory(); // For 1.17
//			PlayerInventory playerInventory = this.client.player.inventory;      // For 1.16
			int size = playerInventory.getContainerSize();
			for (int i = 0; i <= size; i++) {
				ItemStack itemStack = playerInventory.getItem(i);
				String name = itemStack.getHoverName().getString();
				if (itemStack.isDamageableItem()) {
					String searchQuery = name + "\t" + itemStack;
					if (modInit.lowDurabilityItems.contains(searchQuery))
						break;
					double maxDamage = itemStack.getMaxDamage();
					double damage = itemStack.getDamageValue();
					double healthLeft = 100.00 - ((damage*100)/maxDamage);
					if (healthLeft <= threshold) {
//						this.client.player.sendMessage(new LiteralText(name + " durability is low"), true);
						NarratorPlus.narrate(I18n.get("narrate.apextended.durability.warn", name));
						modInit.lowDurabilityItems.add(searchQuery);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
