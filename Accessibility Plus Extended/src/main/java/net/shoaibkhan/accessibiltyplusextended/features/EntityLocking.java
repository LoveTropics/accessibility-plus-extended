package net.shoaibkhan.accessibiltyplusextended.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoaibkhan.accessibiltyplusextended.HudRenderCallBackClass;

public class EntityLocking {
	private MinecraftClient client;
	private KeyBinding LockEntityKey;
	public static Entity lockedOnEntity = null;

	public EntityLocking(MinecraftClient client, KeyBinding LockEntityKey) {
		this.client = client;
		this.LockEntityKey = LockEntityKey;
		this.main();
	}

	private void main() {
		if (lockedOnEntity != null) {
			if (!lockedOnEntity.isAlive())
				lockedOnEntity = null;
			Vec3d vec3d = new Vec3d(lockedOnEntity.getX(), lockedOnEntity.getY() + lockedOnEntity.getHeight() - 0.25,
					lockedOnEntity.getZ());
			client.player.lookAt(EntityAnchor.EYES, vec3d);

		}

		while (LockEntityKey.wasPressed()) {
			Entity toBeLocked = entityLocking();
			if (toBeLocked != null) {
				MutableText mutableText = (new LiteralText("")).append(toBeLocked.getName());
				client.player
						.sendMessage(
								new LiteralText(
										mutableText.getString() + " "
												+ HudRenderCallBackClass
														.get_position_difference(toBeLocked.getBlockPos(), client)),
								true);
				lockedOnEntity = toBeLocked;
			}
		}

		if (HudRenderCallBackClass.isAltPressed) {
			while (LockEntityKey.wasPressed()) {
				lockedOnEntity = null;
			}
		}
	}

	private Entity entityLocking() {
		double closestDouble = -99999;
		Entity closestEntity = null;
		try {
			for (Entity i : client.world.getEntities()) {
				if (!(i instanceof MobEntity))
					continue;
				BlockPos blockPos = i.getBlockPos();

				Vec3d entityVec3d = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				Vec3d playerVec3d = new Vec3d(client.player.getBlockPos().getX(), client.player.getBlockPos().getY(),
						client.player.getBlockPos().getZ());
				if (closestDouble == -99999 || closestDouble > entityVec3d.distanceTo(playerVec3d)) {
					closestDouble = entityVec3d.distanceTo(playerVec3d);
					closestEntity = i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (closestDouble > 10.0)
			closestEntity = null;
		return closestEntity;
	}

}
