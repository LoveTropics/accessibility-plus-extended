package net.shoaibkhan.accessibiltyplusextended.features.withThreads;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.modInit;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import net.shoaibkhan.accessibiltyplusextended.features.POIHandler;

public class POIBlocks extends Thread {
    private Minecraft client;
    private TreeMap<Double, Vec3> oreBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> doorBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> buttonBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> ladderBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> leverBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> trapDoorBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3> blocks = new TreeMap<>();
    private float volume;
    private int delay;

    private static final List<Predicate<BlockState>> blockList = Lists.newArrayList();
    public boolean running = false;

	static {
		blockList.add(state -> state.is(Blocks.PISTON));
		blockList.add(state -> state.is(Blocks.STICKY_PISTON));
		blockList.add(state -> state.is(Blocks.RESPAWN_ANCHOR));
		blockList.add(state -> state.is(Blocks.BELL));
		blockList.add(state -> state.is(Blocks.OBSERVER));
		blockList.add(state -> state.is(Blocks.DAYLIGHT_DETECTOR));
		blockList.add(state -> state.is(Blocks.JUKEBOX));
		blockList.add(state -> state.is(Blocks.LODESTONE));
		blockList.add(state -> state.getBlock() instanceof BeehiveBlock);
		blockList.add(state -> state.getBlock() instanceof ComposterBlock);
		blockList.add(state -> state.is(Blocks.OBSERVER));
		blockList.add(state -> state.is(BlockTags.FENCE_GATES));
	}

    public void run() {
        client = Minecraft.getInstance();
        running = true;
        volume = POIHandler.getVolume();
        delay = POIHandler.getDelay();
        main();
    }

    private void main() {
        client = Minecraft.getInstance();
        assert client.player != null;

        BlockPos pos = client.player.blockPosition();

        int posX = pos.getX();
        int posY = pos.getY() - 1;
        int posZ = pos.getZ();
        int rangeVal = POIHandler.getRange();

        checkBlock(new BlockPos(new Vec3(posX, posY, posZ)), 0);
        checkBlock(new BlockPos(new Vec3(posX, posY + 3, posZ)), 0);
        checkBlock(new BlockPos(new Vec3(posX, posY + 1, posZ)), rangeVal);
        checkBlock(new BlockPos(new Vec3(posX, posY + 2, posZ)), rangeVal);

        POIHandler.oreBlocks = this.oreBlocks;
        POIHandler.doorBlocks = this.doorBlocks;
        POIHandler.buttonBlocks = this.buttonBlocks;
        POIHandler.ladderBlocks = this.ladderBlocks;
        POIHandler.leverBlocks = this.leverBlocks;
        POIHandler.trapDoorBlocks = this.trapDoorBlocks;
        POIHandler.blocks = this.blocks;

        running = false;
    }

    private void checkBlock(BlockPos blockPos, int val) {
        BlockState blockState = client.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Vec3 playerVec3dPos = client.player.getEyePosition();
        double posX = blockPos.getX();
        double posY = blockPos.getY();
        double posZ = blockPos.getZ();
        Vec3 blockVec3dPos = Vec3.atCenterOf(blockPos);

        double diff = playerVec3dPos.distanceTo(blockVec3dPos);
        boolean playSound = false;
        String soundType = "";

        FluidState fluidState = client.level.getFluidState(blockPos);

        if (block instanceof LiquidBlock && Config.get(ConfigKeys.POI_FLUID_DETECTOR_Key.getKey())) {
            if (fluidState.getAmount() == 8) {
                blocks.put(diff, blockVec3dPos);
                playSound = true;
                soundType = "blocks";
            }

            if (Config.get(ConfigKeys.POI_FLUID_DETECTOR_Key.getKey())
                    && !modInit.mainThreadMap.containsKey("fluid_detector_key")) {
                int delay = POIHandler.getFluidDetectorDelay();
                NarratorPlus.narrate(I18n.get("narrate.apextended.poiblock.warn"));
                modInit.mainThreadMap.put("fluid_detector_key", delay);
            }
        } else if (block instanceof OreBlock) {
            oreBlocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "ore";
        } else if (block instanceof ButtonBlock) {
            buttonBlocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocks";
        } else if (block instanceof LadderBlock) {
            ladderBlocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocks";
        } else if (block instanceof TrapDoorBlock) {
            trapDoorBlocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocks";
        } else if (block instanceof LeverBlock) {
            leverBlocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocks";
        } else if (block instanceof DoorBlock) {
            ImmutableSet<Entry<Property<?>, Comparable<?>>> entries = blockState.getValues().entrySet();
            for (Entry<Property<?>, Comparable<?>> i : entries) {

                if (i.getKey().getName().equals("half")) {
                    if (i.getValue().toString().equals("upper")) {
                        doorBlocks.put(diff, blockVec3dPos);
                        playSound = true;
                        soundType = "blocks";
                    }
                    break;
                }

            }
        } else if (blockList.stream().anyMatch($ -> $.test(blockState))) {
            blocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocks";
        } else if (blockState.getMenuProvider(client.level, blockPos) != null) {
            blocks.put(diff, blockVec3dPos);
            playSound = true;
            soundType = "blocksWithInterface";
        } else if (blockState.isAir() && val - 1 >= 0) {
            checkBlock(new BlockPos(new Vec3(posX, posY, posZ - 1)), val - 1); // North Block
            checkBlock(new BlockPos(new Vec3(posX, posY, posZ + 1)), val - 1); // South Block
            checkBlock(new BlockPos(new Vec3(posX - 1, posY, posZ)), val - 1); // West Block
            checkBlock(new BlockPos(new Vec3(posX + 1, posY, posZ)), val - 1); // East Block
            checkBlock(new BlockPos(new Vec3(posX, posY + 1, posZ)), val - 1); // Top Block
            checkBlock(new BlockPos(new Vec3(posX, posY - 1, posZ)), val - 1); // Bottom Block
        }

        if (playSound && !modInit.mainThreadMap.containsKey("sound+" + blockPos) && volume>0) {

            if (soundType.equalsIgnoreCase("ore"))
                client.level.playLocalSound(new BlockPos(blockVec3dPos), SoundEvents.ITEM_PICKUP,
                        SoundSource.BLOCKS, volume, -5f, true);
            else if (soundType.equalsIgnoreCase("blocks"))
                client.level.playLocalSound(new BlockPos(blockVec3dPos), SoundEvents.NOTE_BLOCK_BIT,
                        SoundSource.BLOCKS, volume, 2f, true);
            else if (soundType.equalsIgnoreCase("blocksWithInterface"))
                client.level.playLocalSound(new BlockPos(blockVec3dPos), SoundEvents.NOTE_BLOCK_BANJO,
                        SoundSource.BLOCKS, volume, 0f, true);

            modInit.mainThreadMap.put("sound+" + blockPos, delay);
        }
    }
}
