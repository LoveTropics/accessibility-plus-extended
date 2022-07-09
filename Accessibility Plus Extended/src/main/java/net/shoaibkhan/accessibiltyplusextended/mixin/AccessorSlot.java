package net.shoaibkhan.accessibiltyplusextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.inventory.Slot;

@Environment(EnvType.CLIENT)
@Mixin(Slot.class)
public interface AccessorSlot {
    @Accessor("index")
    int getInventoryIndex();
}
