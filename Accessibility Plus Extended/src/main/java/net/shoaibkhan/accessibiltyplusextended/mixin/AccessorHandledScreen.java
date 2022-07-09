package net.shoaibkhan.accessibiltyplusextended.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AccessorHandledScreen {
    //
    @Accessor("playerInventoryTitleX")
    int getPlayerInventoryTitleX();

    @Accessor("playerInventoryTitleY")
    int getPlayerInventoryTitleY();

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Accessor("handler")
    AbstractContainerMenu getHandler();

    @Accessor("focusedSlot")
    Slot getFocusedSlot();

    @Accessor("focusedSlot")
    public void setFocusedSlot(Slot slot);
}
