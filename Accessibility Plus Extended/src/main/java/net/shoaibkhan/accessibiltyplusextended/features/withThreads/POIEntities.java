package net.shoaibkhan.accessibiltyplusextended.features.withThreads;

import java.util.TreeMap;
import net.shoaibkhan.accessibiltyplusextended.modInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.phys.Vec3;
import net.shoaibkhan.accessibiltyplusextended.features.LockingHandler;
import net.shoaibkhan.accessibiltyplusextended.features.POIHandler;

public class POIEntities extends Thread {
	private Minecraft client;
	private TreeMap<Double, Entity> passiveEntity = new TreeMap<>();
	private TreeMap<Double, Entity> hostileEntity = new TreeMap<>();
	private TreeMap<Double, Entity> eyeOfEnderEntity = new TreeMap<>();
	public boolean running = false;

	public void run() {
		this.client = Minecraft.getInstance();
		running = true;
		this.main();
	}

	private void main() {
		try {
			for (Entity i : client.level.entitiesForRendering()) {

				// For curseforge
//				 if (!(i instanceof MobEntity || i instanceof ItemEntity || i instanceof EyeOfEnderEntity))
//				 continue;

				// For discord
				if (!(i instanceof Mob || i instanceof ItemEntity || i instanceof EyeOfEnder || (i instanceof Player && i != client.player)))
					continue;

				BlockPos blockPos = i.blockPosition();

				Vec3 entityVec3d = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				Vec3 playerVec3d = new Vec3(client.player.blockPosition().getX(), client.player.blockPosition().getY(),
						client.player.blockPosition().getZ());
				Double distance = entityVec3d.distanceTo(playerVec3d);

				int range = POIHandler.getRange();
				float volume = POIHandler.getVolume();
				int delay = POIHandler.getDelay();

				if (distance <= range) {
					String entityString = i + "";
					int z = entityString.indexOf("/");
					int y = entityString.indexOf(",", z);
					entityString = entityString.substring(z, y);

					if (i instanceof EyeOfEnder && distance <= 0.2) {
						eyeOfEnderEntity.put(distance, i);
						LockingHandler.lockedOnEntity = i;
						LockingHandler.lockedOnBlockEntries = "";

						LockingHandler.lockedOnBlock = null;
						LockingHandler.isLockedOntoLadder = false;

					} else if (i instanceof AgeableMob) {
						passiveEntity.put(distance, i);
						if (!modInit.mainThreadMap.containsKey("passiveentity+" + entityString) && volume>0) {
							client.level.playLocalSound(blockPos, SoundEvents.NOTE_BLOCK_BELL, SoundSource.BLOCKS,
									volume, 0f, true);
							modInit.mainThreadMap.put("passiveentity+" + entityString, delay);
						}
					} else if (i instanceof Monster) {
						hostileEntity.put(distance, i);
						if (!modInit.mainThreadMap.containsKey("hostileentity+" + entityString) && volume>0) {
							client.level.playLocalSound(blockPos, SoundEvents.NOTE_BLOCK_BELL, SoundSource.BLOCKS,
									volume, 2f, true);
							modInit.mainThreadMap.put("hostileentity+" + entityString, delay);
						}
					} else if (i instanceof ItemEntity) {
						if (i.isOnGround()) {
							if (!modInit.mainThreadMap.containsKey("itementity+" + i) && volume>0) {
								client.level.playLocalSound(blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
										SoundSource.BLOCKS, volume, 2f, true);
								modInit.mainThreadMap.put("itementity+" + i, delay);
							}
						}
					} else if (i instanceof Player) {
						passiveEntity.put(distance, i);
						if (!modInit.mainThreadMap.containsKey("passiveentity+" + entityString) && volume>0) {
							client.level.playLocalSound(blockPos, SoundEvents.NOTE_BLOCK_BELL, SoundSource.BLOCKS,
									volume, 0f, true);
							modInit.mainThreadMap.put("passiveentity+" + entityString, delay);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		POIHandler.passiveEntity = passiveEntity;
		POIHandler.hostileEntity = hostileEntity;
		POIHandler.eyeOfEnderEntity = eyeOfEnderEntity;
		running = false;
	}

}
