package net.shoaibkhan.accessibiltyplusextended.keyboard;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import blue.endless.jankson.annotation.Nullable;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks;
import me.shedaniel.cloth.api.client.events.v0.ScreenHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.TooltipFlag;
import net.shoaibkhan.accessibiltyplusextended.HudScreenHandler;
import net.shoaibkhan.accessibiltyplusextended.NarratorPlus;
import net.shoaibkhan.accessibiltyplusextended.config.Config;
import net.shoaibkhan.accessibiltyplusextended.config.ConfigKeys;
import net.shoaibkhan.accessibiltyplusextended.mixin.AccessorHandledScreen;
import net.shoaibkhan.accessibiltyplusextended.util.KeyBinds;

public class KeyboardController {
  private static Minecraft client;
  @Nullable
  private static List<SlotsGroup> groups;
  @Nullable
  private static SlotsGroup currentGroup;
  @Nullable
  private static Slot currentSlot;
  @Nullable
  private static AccessorHandledScreen screen;

  private static boolean narrateCursorStack = false;
  private static double lastMouseX = 0;
  private static double lastMouseY = 0;
  private static boolean hasControlOverMouse = false;

  private enum FocusDirection {
    UP, DOWN, LEFT, RIGHT
  }

  public KeyboardController() {
    if(Config.get(ConfigKeys.INV_KEYBOARD_CONTROL_KEY.getKey())) {
      ClothClientHooks.SCREEN_INIT_POST.register(KeyboardController::onScreenOpen);
      ClothClientHooks.SCREEN_KEY_PRESSED.register(KeyboardController::onKeyPress);
    }
  }

  public static boolean hasControlOverMouse() {
    if (screen == null) {
      return false;
    } else {
      return hasControlOverMouse;
    }
  }

  private static InteractionResult onScreenOpen(Minecraft mc, Screen currentScreen, ScreenHooks screenHooks) {
    client = mc;
    groups = null;
    screen = null;
    currentGroup = null;
    currentSlot = null;

    if (currentScreen != null && currentScreen instanceof AccessorHandledScreen) {
      screen = (AccessorHandledScreen) currentScreen;
      groups = SlotsGroup.generateGroupsFromSlots(screen.getHandler().slots);
      moveMouseToHome();
    }
    return InteractionResult.PASS;
  }

  private static InteractionResult onKeyPress(Minecraft mc, Screen currentScreen, int keyCode, int scanCode,
      int modifiers) {
    if (screen != null && Config.get(ConfigKeys.INV_KEYBOARD_CONTROL_KEY.getKey()) && !HudScreenHandler.isSearchingRecipies) {
      if (KeyBinds.LEFT_KEY.getKeyBind().matches(keyCode, scanCode)) {
        focusSlotAt(FocusDirection.LEFT);
      } else if (KeyBinds.RIGHT_KEY.getKeyBind().matches(keyCode, scanCode)) {
        focusSlotAt(FocusDirection.RIGHT);
      } else if (KeyBinds.UP_KEY.getKeyBind().matches(keyCode, scanCode)) {
        focusSlotAt(FocusDirection.UP);
      } else if (KeyBinds.DOWN_KEY.getKeyBind().matches(keyCode, scanCode)) {
        focusSlotAt(FocusDirection.DOWN);
      } else if (KeyBinds.GROUP_KEY.getKeyBind().matches(keyCode, scanCode)) {
        if (modifiers == GLFW.GLFW_MOD_SHIFT) {
          focusGroupVertically(false);
        } else {
          focusGroupVertically(true);
        }
        return InteractionResult.SUCCESS;
      } else if (KeyBinds.HOME_KEY.getKeyBind().matches(keyCode, scanCode)) {
        if (modifiers == GLFW.GLFW_MOD_SHIFT) {
          focusEdgeGroup(false);
        } else {
          focusEdgeSlot(false);
        }
      } else if (KeyBinds.END_KEY.getKeyBind().matches(keyCode, scanCode)) {
        if (modifiers == GLFW.GLFW_MOD_SHIFT) {
          focusEdgeGroup(true);
        } else {
          focusEdgeSlot(true);
        }
      } else if (KeyBinds.CLICK_KEY.getKeyBind().matches(keyCode, scanCode)) {
        click(false);
      } else if (KeyBinds.RIGHT_CLICK_KEY.getKeyBind().matches(keyCode, scanCode)) {
        click(true);
      }
    }
    return InteractionResult.PASS;
  }

  private static void focusSlotAt(FocusDirection direction) {
    if (currentGroup == null) {
      focusGroupVertically(true);
      return;
    }
    if (currentSlot == null) {
      focusSlot(currentGroup.getFirstSlot());
      return;
    }
    int targetDeltaX = 0;
    int targetDeltaY = 0;
    switch (direction) {
      case UP:
        if (!currentGroup.hasSlotAbove(currentSlot)) {
          NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.noSlots.above"));
          return;
        }
        targetDeltaY = -18;
        break;
      case DOWN:
        if (!currentGroup.hasSlotBelow(currentSlot)) {
          NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.noSlots.below"));
          return;
        }
        targetDeltaY = 18;
        break;
      case LEFT:
        if (!currentGroup.hasSlotLeft(currentSlot)) {
          NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.noSlots.left"));
          return;
        }
        targetDeltaX = -18;
        break;
      case RIGHT:
        if (!currentGroup.hasSlotRight(currentSlot)) {
          NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.noSlots.right"));
          return;
        }
        targetDeltaX = 18;
        break;
    }
    int targetX = currentSlot.x + targetDeltaX;
    int targetY = currentSlot.y + targetDeltaY;
    for (Slot s : currentGroup.slots) {
      if (s.x == targetX && s.y == targetY) {
        focusSlot(s);
        break;
      }
    }
  }

  private static void focusSlot(Slot slot) {
    currentSlot = slot;
    moveToSlot(currentSlot);
    String message = "";
    if (currentGroup.getSlotName(currentSlot).length() > 0) {
      message += currentGroup.getSlotName(currentSlot) + ". ";
    }
    if (!currentSlot.hasItem()) {
      message += I18n.get("narrate.apextended.invcon.emptySlot");
    } else {
      List<Component> lines = currentSlot.getItem().getTooltipLines(client.player, TooltipFlag.Default.NORMAL);
      for (Component line : lines) {
        message += line.getString() + ", ";
      }
    }
    if (message != null && message.length() > 0) {
      NarratorPlus.narrate(message);
    }
  }

  private static void focusEdgeSlot(boolean end) {
    if (currentGroup == null) {
      focusGroupVertically(true);
      return;
    }
    if (currentGroup.slots.size() == 1 && currentSlot != null) {
      NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.onlyOneSlot"));
      return;
    }
    focusSlot(end ? currentGroup.getLastSlot() : currentGroup.getFirstSlot());
  }

  private static void focusEdgeGroup(boolean last) {
    focusGroup(groups.get(last ? groups.size() - 1 : 0));
  }

  private static void focusGroupVertically(boolean goBelow) {
    if (currentGroup == null) {
      focusGroup(groups.get(0));
    } else {
      int currentGroupIndex = groups.indexOf(currentGroup);
      int nextGroupIndex = currentGroupIndex + (goBelow ? 1 : -1);
      if (nextGroupIndex < 0) {
        NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.reachedTopGroup"));
        return;
      } else if (nextGroupIndex > groups.size() - 1) {
        NarratorPlus.narrate(I18n.get("narrate.apextended.invcon.reachedBottomGroup"));
        return;
      } else {
        focusGroup(groups.get(nextGroupIndex));
      }
    }
  }

  private static void focusGroup(SlotsGroup group) {
    currentGroup = group;
    currentSlot = null;
    moveMouseToHome();
    NarratorPlus.narrate(currentGroup.getName());
  }

  private static void moveMouseToHome() {
    SlotsGroup lastGroup = groups.get(groups.size() - 1);
    Slot lastSlot = lastGroup.getLastSlot();
    moveMouseToScreenCoords(lastSlot.x + 19, lastSlot.y + 19);
  }

  private static void click(boolean rightClick) {
    double scale = client.getWindow().getGuiScale();
    double x = client.mouseHandler.xpos() / scale;
    double y = client.mouseHandler.ypos() / scale;
    int button = rightClick ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : GLFW.GLFW_MOUSE_BUTTON_LEFT;
    client.screen.mouseClicked(x, y, button);
    client.screen.mouseReleased(x, y, button);
    narrateCursorStack = true;
  }

	private static void moveMouseTo(double x, double y) {
		InputConstants.grabOrReleaseMouse(client.getWindow().getWindow(), GLFW.GLFW_CURSOR_NORMAL, x, y);
		lastMouseX = (double) x;
		lastMouseY = (double) y;
	}

	private static void moveMouseToScreenCoords(int x, int y) {
		double targetX = (screen.getX() + x) * client.getWindow().getGuiScale();
		double targetY = (screen.getY() + y) * client.getWindow().getGuiScale();
		moveMouseTo(targetX, targetY);
	}

  private static void moveToSlot(Slot slot) {
    if (slot == null) {
      return;
    }
    moveMouseToScreenCoords(slot.x + 9, slot.y + 9);
  }
}
