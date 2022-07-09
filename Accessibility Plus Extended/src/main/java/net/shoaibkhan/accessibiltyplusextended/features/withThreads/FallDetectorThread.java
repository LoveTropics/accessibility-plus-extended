package net.shoaibkhan.accessibiltyplusextended.features.withThreads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.modInit;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;

public class FallDetectorThread extends Thread {

	public boolean alive = false, finished = false;
	private Minecraft client;
	public static Integer[] range = { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	public static Integer[] depthArray = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	public void run() {
		alive = true;
		client = Minecraft.getInstance();
		BlockPos pos = client.player.blockPosition();
		int posX = pos.getX();
		int posY = pos.getY() - 1;
		int posZ = pos.getZ();

		int rangeVal = 10;
		try {
			rangeVal = range[Config.getInt(ConfigKeys.FALL_DETECTOR_RANGE_KEY.getKey())];
			rangeVal = rangeVal - 1;
		} catch (Exception e) {
			rangeVal = 10;
		}

		Direction dir = client.player.getDirection();
		if (dir == Direction.NORTH) {
			checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ - 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX, posY, posZ - 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ - 1)), dir, rangeVal);
		} else if (dir == Direction.SOUTH) {
			checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ + 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX, posY, posZ + 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ + 1)), dir, rangeVal);
		} else if (dir == Direction.EAST) {
			checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ - 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ + 1)), dir, rangeVal);
		} else if (dir == Direction.WEST) {
			checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ - 1)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ)), dir, rangeVal);
			checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ + 1)), dir, rangeVal);
		}
		finished = true;
	}

	private void checkBlock(BlockPos blockPos, Direction direction, int limit) {
		if (client.player.isFallFlying())
			return;
		if (!modInit.mainThreadMap.containsKey("fall_detector_key")) {
			BlockState block = client.level.getBlockState(blockPos);
			if (block.is(Blocks.VOID_AIR))
				return;
			int posX = blockPos.getX();
			int posY = blockPos.getY();
			int posZ = blockPos.getZ();

			if (block.isAir() && !modInit.mainThreadMap.containsKey("fluid_detector_key")
					&& !modInit.mainThreadMap.containsKey("fall_detector_key")) {
				BlockState topBlock = client.level.getBlockState(new BlockPos(new Vec3(posX, posY + 1, posZ)));
				if (topBlock.is(Blocks.VOID_AIR))
					return;
				if (!topBlock.isAir())
					return;

				int depth = getDepth((new BlockPos(new Vec3(posX, posY, posZ))), 15);

				int depthVal;
				try {
					depthVal = depthArray[Config.getInt(ConfigKeys.FALL_DETECTOR_DEPTH.getKey())];
				} catch (Exception e) {
					depthVal = 5;
				}

				if (depth >= depthVal && !modInit.mainThreadMap.containsKey("fall_detector_key")) {
					modInit.mainThreadMap.put("fall_detector_key", 5000);
//					client.player.sendMessage(new LiteralText("warning Fall Detected"), true);
					NarratorPlus.narrate(I18n.get("narrate.apextended.falldetector"));
				}
			}

			if (direction == Direction.NORTH && limit > 0) {
				checkBlock(new BlockPos(new Vec3(posX, posY, posZ - 1)), direction, limit - 1);
			} else if (direction == Direction.SOUTH && limit > 0) {
				checkBlock(new BlockPos(new Vec3(posX, posY, posZ + 1)), direction, limit - 1);
			} else if (direction == Direction.EAST && limit > 0) {
				checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ)), direction, limit - 1);
			} else if (direction == Direction.WEST && limit > 0) {
				checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ)), direction, limit - 1);
			}
		}
	}

	private int getDepth(BlockPos blockPos, int limit) {
		if (limit <= 0) return 0;

		BlockState block = client.level.getBlockState(blockPos);
		int posX = blockPos.getX();
		int posY = blockPos.getY();
		int posZ = blockPos.getZ();

		if (block.isAir() && !block.is(Blocks.VOID_AIR)) {
			return 1 + getDepth((new BlockPos(new Vec3(posX, posY - 1, posZ))), limit - 1);
		}
		return 0;
	}

}
