package net.shoaibkhan.accessibiltyplusextended;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import net.shoaibkhan.accessibiltyplusextended.features.FeaturesWithThreadHandler;
import net.shoaibkhan.accessibiltyplusextended.features.LockingHandler;
import net.shoaibkhan.accessibiltyplusextended.gui.AccessibilityPlusConfigGui;
import net.shoaibkhan.accessibiltyplusextended.gui.ConfigGui;
import net.shoaibkhan.accessibiltyplusextended.gui.ConfigScreen;
import net.shoaibkhan.accessibiltyplusextended.util.KeyBinds;

public class HudRenderCallBackClass {
	private Minecraft client;
	public static int entityNarratorFlag = 0, oreDetectorFlag = 0;
	public static boolean isTradeScreenOpen = false;
	public static boolean isAltPressed, isControlPressed, isDPressed, isAPressed, isWPressed, isSPressed, isRPressed,
			isFPressed, isCPressed, isVPressed, isTPressed, isEnterPressed, isShiftPressed;
	public static int currentColumn = 0;
	public static int currentRow = 0;
	private final HudScreenHandler hudScreenHandler;

	public HudRenderCallBackClass() {
		hudScreenHandler = new HudScreenHandler();
		HudRenderCallback.EVENT.register(this::hudRenderCallbackEventMethod);
	}

	private void hudRenderCallbackEventMethod(PoseStack matixStack, float f) {
		this.client = Minecraft.getInstance();
		if (client.player == null)
			return;

		try {

			keyPresses();

			if (Config.get(ConfigKeys.POI_KEY.getKey()))
				new LockingHandler();

			new FeaturesWithThreadHandler(client);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Config.get(ConfigKeys.INV_KEYBOARD_CONTROL_KEY.getKey())) {

			isDPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.d").getValue()));
			isAPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.a").getValue()));
			isWPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.w").getValue()));
			isSPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.s").getValue()));
			isRPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.r").getValue()));
			isFPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.f").getValue()));
			isCPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.c").getValue()));
			isVPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.v").getValue()));
			isTPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.t").getValue()));
			isEnterPressed = (InputConstants.isKeyDown(client.getWindow().getWindow(), InputConstants.getKey("key.keyboard.enter").getValue()));

			if (client.screen == null) {
				currentColumn = 0;
				currentRow = 0;
				HudScreenHandler.isSearchingRecipies = false;
				HudScreenHandler.bookPageIndex = 0;
			} else {
				Screen screen = client.screen;
				hudScreenHandler.screenHandler(screen);

				// Reset lockOnBlock
				LockingHandler.lockedOnBlockEntries = "";
				LockingHandler.lockedOnBlock = null;
				LockingHandler.isLockedOntoLadder = false;
			}
		}
	}

	private void keyPresses() {
		isAltPressed = (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.left.alt").getValue()) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.right.alt").getValue()));
		isControlPressed = (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.left.control").getValue()) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.right.control").getValue()));
		isShiftPressed = (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.left.shift").getValue()) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.getKey("key.keyboard.right.shift").getValue()));

		while (KeyBinds.CONFIG_KEY.getKeyBind().consumeClick()) {
			if (!isControlPressed) {
				Screen screen = new ConfigScreen(new ConfigGui(client.player, client), "ext.title");
				client.setScreen(screen);
				return;
			}
		}

		while (KeyBinds.AP_CONFIG_KEY.getKeyBind().consumeClick()) {
			client.setScreen(new ConfigScreen(new AccessibilityPlusConfigGui(client.player), "title"));
			return;
		}

	}

	public static String get_position_difference(BlockPos blockPos, Minecraft client) {
		LocalPlayer player = client.player;
		Direction dir = client.player.getDirection();

		Vec3 diff = player.getEyePosition().subtract(Vec3.atCenterOf(blockPos));
		BlockPos diffBlockPos = new BlockPos(Math.round(diff.x), Math.round(diff.y), Math.round(diff.z));

		String diffXBlockPos = "";
		String diffYBlockPos = "";
		String diffZBlockPos = "";

		if (diffBlockPos.getX() != 0) {
			if (dir == Direction.NORTH) {
				diffXBlockPos = diff(diffBlockPos.getX(), "right", "left");
			} else if (dir == Direction.SOUTH) {
				diffXBlockPos = diff(diffBlockPos.getX(), "left", "right");
			} else if (dir == Direction.EAST) {
				diffXBlockPos = diff(diffBlockPos.getX(), "away", "behind");
			} else if (dir == Direction.WEST) {
				diffXBlockPos = diff(diffBlockPos.getX(), "behind", "away");
			}
		}

		if (diffBlockPos.getY() != 0) {
			diffYBlockPos = diff(diffBlockPos.getY(), "up", "down");
		}

		if (diffBlockPos.getZ() != 0) {
			if (dir == Direction.SOUTH) {
				diffZBlockPos = diff(diffBlockPos.getZ(), "away", "behind");
			} else if (dir == Direction.NORTH) {
				diffZBlockPos = diff(diffBlockPos.getZ(), "behind", "away");
			} else if (dir == Direction.EAST) {
				diffZBlockPos = diff(diffBlockPos.getZ(), "right", "left");
			} else if (dir == Direction.WEST) {
				diffZBlockPos = diff(diffBlockPos.getZ(), "left", "right");
			}
		}

		String text = "";
		if (dir == Direction.NORTH || dir == Direction.SOUTH)
			text = String.format("%s  %s  %s", diffZBlockPos, diffYBlockPos, diffXBlockPos);
		else
			text = String.format("%s  %s  %s", diffXBlockPos, diffYBlockPos, diffZBlockPos);
		return text;
	}

	private static String diff(int blocks, String key1, String key2) {
		return I18n.get("narrate.apextended.posDiff." + (blocks < 0 ? key1 : key2), Math.abs(blocks));
	}
}
