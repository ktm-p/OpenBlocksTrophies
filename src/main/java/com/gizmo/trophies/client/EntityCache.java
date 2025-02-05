package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityCache {

	private static final Map<EntityType<?>, Entity> ENTITY_MAP = new WeakHashMap<>();
	private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

	@Nullable
	public static LivingEntity fetchEntity(EntityType<?> type, @Nullable Level level, CompoundTag variant, Optional<CompoundTag> defaultVariant) {
		if (level != null && !IGNORED_ENTITIES.contains(type)) {
			Entity entity = null;
			if (type == EntityType.PLAYER) {
				entity = Minecraft.getInstance().player;
			} else {
				try {
					entity = ENTITY_MAP.computeIfAbsent(type, t -> {
						Entity created = t.create(level);
						if (created != null) {
							created.setYRot(0.0F);
							created.setYHeadRot(0.0F);
							created.setYBodyRot(0.0F);
							created.hasImpulse = false;
							if (created instanceof Mob mob) {
								mob.setNoAi(true);
							}
						}
						return created;
					});
				} catch (Exception e) {
					OpenBlocksTrophies.LOGGER.error("Failed to cache a render for entity {}", type.getDescriptionId(), e);
				}
			}
			if (entity instanceof LivingEntity living) {
				CompoundTag tag = new CompoundTag();
				defaultVariant.ifPresent(tag1 -> tag1.getAllKeys().forEach(s -> tag.put(s, Objects.requireNonNull(tag1.get(s)))));
				if (!variant.isEmpty()) {
					if (entity instanceof VillagerDataHolder villager) {
						variant.getAllKeys().forEach(s -> villager.setVillagerData(new VillagerData(VillagerType.PLAINS, Objects.requireNonNull(Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).get(ResourceLocation.tryParse(variant.getString(s)))), 1)));
					} else {
						variant.getAllKeys().forEach(s -> tag.put(s, Objects.requireNonNull(variant.get(s))));
					}
				}
				if (!tag.isEmpty()) {
					living.readAdditionalSaveData(tag);
					if (entity instanceof Mob mob) {
						mob.setNoAi(true);
					}
				}
				return living;
			} else {
				IGNORED_ENTITIES.add(type);
				ENTITY_MAP.remove(type);
			}
		}
		return null;
	}

	public static void addEntityToBlacklist(EntityType<?> type) {
		IGNORED_ENTITIES.add(type);
		ENTITY_MAP.remove(type);
	}
}
