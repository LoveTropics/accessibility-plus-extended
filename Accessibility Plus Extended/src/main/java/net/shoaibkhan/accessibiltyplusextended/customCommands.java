package net.shoaibkhan.accessibiltyplusextended;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.Minecraft;
import net.shoaibkhan.accessibiltyplusextended.features.withThreads.FluidDetectorThread;

public class customCommands {
  Minecraft client;

  public customCommands() {
      ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("getxp").executes(source -> {
      client = Minecraft.getInstance();
      NarratorPlus.narrate(""+client.player.experienceLevel);
      return 1;
    }));

      ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("isfullscreen").executes(source -> {
      client = Minecraft.getInstance();
      NarratorPlus.narrate(client.options.fullscreen);
      return 1;
    }));

      ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("findlava").executes(source -> {
      try {
        FluidDetectorThread fluidDetectorThread = new FluidDetectorThread(true, false);
        fluidDetectorThread.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return 1;
    }));

      ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("findwater").executes(source -> {
      try {
        FluidDetectorThread fluidDetectorThread = new FluidDetectorThread(false, true);
        fluidDetectorThread.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return 1;
    }));
  }

}
