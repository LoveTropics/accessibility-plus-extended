package net.shoaibkhan.accessibiltyplusextended.features.withThreads;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.shoaibkhan.accessibiltyplusextended.HudRenderCallBackClass;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;

public class FluidDetectorThread extends Thread{
  private boolean lava, water; 
  public static Float[] volume = { 0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f, 0.55f, 0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 1f };
  public static Float[] pitch = { 0f, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f, 5f, -0.5f, -1f, -1.5f, -2f, -2.5f, -3f, -3.5f, -4f, -4.5f, -5f };
  public static Integer[] range = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

  public FluidDetectorThread(boolean lava, boolean water){
    this.lava = lava;
    this.water = water;
  }
  public void run() {
    Minecraft client = Minecraft.getInstance();
    BlockPos pos = client.player.blockPosition();
    int posX = pos.getX();
    int posY = pos.getY();
    int posZ = pos.getZ();

    int rangeVal = 10;
    try {
      rangeVal = range[Config.getInt(ConfigKeys.FIND_FLUID_RANGE.getKey())];
    } catch (Exception e) {
      rangeVal = 10;
    }

    BlockPos newBlockPos = new BlockPos(new Vec3(posX, posY, posZ));
    BlockPos fluidPos = findFluid(client, newBlockPos, rangeVal, this.lava, this.water);
    if(fluidPos!=null){
      if(!Config.get(ConfigKeys.FIND_FLUID_TEXT_KEY.getKey())){
        try {
          Float vol, pit;
          try {
            vol = volume[Config.getInt(ConfigKeys.FIND_FLUID_VOLUME.getKey())];
          } catch (Exception e) {
            vol = 0.2f;
          }
          try {
            pit = pitch[Config.getInt(ConfigKeys.FIND_FLUID_PITCH.getKey())];
          } catch (Exception e) {
            pit = 1f;
          }

          client.level.playLocalSound(fluidPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, vol, pit, true);

        } catch (Exception e) {
        }
      }else{
        String posDifference = HudRenderCallBackClass.get_position_difference(fluidPos, client);
        MutableComponent blockMutableText = client.level.getBlockState(fluidPos).getBlock().getName();
        String name = blockMutableText.getString();
    
//        client.player.sendMessage(new LiteralText(""+name+", "+posDifference), true);
        NarratorPlus.narrate(name+", "+posDifference);
      }
    }
  }

  private static BlockPos findFluid(Minecraft client, BlockPos blockPos, int range, boolean lava, boolean water){
    BlockState blockState = client.level.getBlockState(blockPos);
    if (blockState.is(Blocks.VOID_AIR))
      return null;

    FluidState fluidState = client.level.getFluidState(blockPos);
    if ((fluidState.is(FluidTags.LAVA) && lava) || (fluidState.is(FluidTags.WATER) && water) && fluidState.getAmount()==8) {
      return blockPos;
    } else if(range-1 >= 0 && blockState.isAir()){
      int posX = blockPos.getX();
      int posY = blockPos.getY();
      int posZ = blockPos.getZ();
      int rangeVal = range-1;
      BlockPos bp1 = findFluid(client, new BlockPos(new Vec3(posX, posY, posZ - 1)), rangeVal, lava, water);
      BlockPos bp2 = findFluid(client, new BlockPos(new Vec3(posX, posY, posZ + 1)), rangeVal, lava, water);
      BlockPos bp3 = findFluid(client, new BlockPos(new Vec3(posX - 1, posY, posZ)), rangeVal, lava, water);
      BlockPos bp4 = findFluid(client, new BlockPos(new Vec3(posX + 1, posY, posZ)), rangeVal, lava, water);
      BlockPos bp5 = findFluid(client, new BlockPos(new Vec3(posX, posY - 1, posZ)), rangeVal, lava, water);
      BlockPos bp6 = findFluid(client, new BlockPos(new Vec3(posX, posY + 1, posZ)), rangeVal, lava, water);

      if(bp1 != null ) return bp1;
      if(bp2 != null ) return bp2;
      if(bp3 != null ) return bp3;
      if(bp4 != null ) return bp4;
      if(bp5 != null ) return bp5;
      if(bp6 != null ) return bp6;
    }

    return null;
  }
}
