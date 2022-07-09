package net.shoaibkhan.accessibiltyplusextended.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.AccessibilityPlusExt;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;

public class CrosshairTarget {
	private final Minecraft client;

	public CrosshairTarget(Minecraft client) {
		this.client = client;
		this.main();
	}

	private void main() {
		HitResult hit = client.hitResult;
		if (hit == null)
		    return;
		String text = "";
		switch (hit.getType()) {
			case MISS:
				break;
			case BLOCK:
				assert client.level != null;
				if (Config.get(ConfigKeys.READ_BLOCKS_KEY.getKey())
						|| Config.get(ConfigKeys.READ_SIGNS_CONTENTS.getKey())) {
					BlockHitResult blockHitResult = (BlockHitResult) hit;
					BlockState blockState = client.level.getBlockState(blockHitResult.getBlockPos());
					Block block = blockState.getBlock();

					String name = block.getName().getString();

					// Class name in production environment can be different
					String blockPos = blockHitResult.getBlockPos().immutable().toString();

					String searchQuery = name + blockPos;

					String blockEntries = blockState.getValues() + "" + blockState.getBlock() + "" + blockPos;
					boolean isSign = blockState.is(BlockTags.SIGNS);

					if (!isSign && Config.get(ConfigKeys.READ_BLOCKS_KEY.getKey())) {
						if (!AccessibilityPlusExt.mainThreadMap.containsKey(searchQuery) && !blockEntries.equalsIgnoreCase(LockingHandler.lockedOnBlockEntries)) {
							text += name;

							if (Config.get(ConfigKeys.NARRATE_BLOCK_SIDE_KEY.getKey())) {
	                            Direction side = blockHitResult.getDirection();
								text += " " + I18n.get("narrate.apextended." + side.getSerializedName());
							}

							NarratorPlus.narrate(text);
							AccessibilityPlusExt.mainThreadMap.put(searchQuery, 5000);
						}
					}
					if (isSign && Config.get(ConfigKeys.READ_SIGNS_CONTENTS.getKey())) {
						if (!AccessibilityPlusExt.mainThreadMap.containsKey(searchQuery)) {
							String output = "";
							try {
								SignBlockEntity signentity = (SignBlockEntity) client.level
										.getBlockEntity(blockHitResult.getBlockPos());

								 // 1.17
								 output += "1: " + signentity.getMessage(0, false).getString() + ", ";
								 output += "2: " + signentity.getMessage(1, false).getString() + ", ";
								 output += "3: " + signentity.getMessage(2, false).getString() + ", ";
								 output += "4: " + signentity.getMessage(3, false).getString();

								// 1.16
//								output += "1: " + signentity.getTextOnRow(0).getString() + ", ";
//								output += "2: " + signentity.getTextOnRow(1).getString() + ", ";
//								output += "3: " + signentity.getTextOnRow(2).getString() + ", ";
//								output += "4: " + signentity.getTextOnRow(3).getString();
								 
								 output = I18n.get("narrate.apextended.sign.says", output);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
                                if (!output.isEmpty())
                                    NarratorPlus.narrate(output);
								AccessibilityPlusExt.mainThreadMap.put(searchQuery, 10000);
							}
						}
					}
				}
				break;
			case ENTITY:

				if (Config.get(ConfigKeys.ENTITY_NARRATOR_KEY.getKey())) {
					try {
						EntityHitResult entityHitResult = (EntityHitResult) hit;

						if (((EntityHitResult) hit).getEntity() == LockingHandler.lockedOnEntity)
							break;

						if (!AccessibilityPlusExt.mainThreadMap.containsKey("entity_narrator_key")) {
							NarratorPlus.narrate(entityHitResult.getEntity().getName().getString());
							AccessibilityPlusExt.mainThreadMap.put("entity_narrator_key", 5000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
		}
	}
}
