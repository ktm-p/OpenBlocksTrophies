package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.network.SyncTrophyConfigsPacket;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class TrophyEvents {

	public static final RandomSource TROPHY_RANDOM = RandomSource.create();

	public static void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			PacketDistributor.PLAYER.with(event.getPlayer()).send(new SyncTrophyConfigsPacket(Trophy.getTrophies()));
			OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), event.getPlayer().getDisplayName().getString());
		} else {
			event.getPlayerList().getPlayers().forEach(player -> {
				PacketDistributor.PLAYER.with(player).send(new SyncTrophyConfigsPacket(Trophy.getTrophies()));
				OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), player.getDisplayName().getString());
			});
		}
	}

	public static void grantAdvancementBasedTrophies(AdvancementEvent.AdvancementEarnEvent event) {
		if (ModList.get().isLoaded("the_bumblezone")) {
			if (event.getAdvancement().id().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("the_bumblezone", "bee_queen"))), 0, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
			if (event.getAdvancement().id().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("the_bumblezone", "beehemoth"))), 1, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
		}
	}

	public static void maybeDropTrophy(LivingDropsEvent event) {
		//follow gamerules and mob drop requirements
		if (!event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) || !event.getEntity().shouldDropLoot())
			return;

		//players are a bit special.
		//charged creepers can make players drop trophies, and player trophies come loaded with the dead player's name
		if (event.getEntity() instanceof Player player) {
			double dropChance;
			if (event.getSource().getEntity() instanceof Creeper creeper && creeper.isPowered() && TrophyConfig.playerChargedCreeperDropChance > 0.0D) {
				dropChance = TrophyConfig.playerChargedCreeperDropChance - TROPHY_RANDOM.nextDouble();
			} else {
				//don't drop trophies if the config doesn't allow this source to
				if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.anySourceDropsTrophies)
					return;
				if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.fakePlayersDropTrophies)
					return;
				Trophy trophy = Trophy.getTrophies().getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER), new Trophy.Builder(EntityType.PLAYER).build());
				dropChance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * OpenBlocksTrophies.getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
			}
			if (dropChance > 0.0D) {
				ItemStack stack = TrophyItem.loadEntityToTrophy(EntityType.PLAYER, 0, false);
				stack.setHoverName(Component.literal(player.getDisplayName().getString()));
				event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
			}
		} else {
			//don't drop trophies if the config doesn't allow this source to
			if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.anySourceDropsTrophies)
				return;
			if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.fakePlayersDropTrophies)
				return;

			if (Trophy.getTrophies().containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()))) {
				Trophy trophy = Trophy.getTrophies().get(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()));
				if (trophy != null) {
					double chance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * OpenBlocksTrophies.getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
					if (chance > 0.0D) {
						event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyItem.loadEntityToTrophy(trophy.type(), fetchVariantIfAny(event.getEntity(), trophy), false)));
					}
				}
			}
		}
	}

	private static int fetchVariantIfAny(LivingEntity entity, Trophy trophy) {
		if (!trophy.getVariants(entity.level().registryAccess()).isEmpty()) {
			CompoundTag tag = new CompoundTag();
			entity.addAdditionalSaveData(tag);
			for (int i = 0; i < trophy.getVariants(entity.level().registryAccess()).size(); i++) {
				CompoundTag variantKeys = trophy.getVariants(entity.level().registryAccess()).get(i);
				for (String s : variantKeys.getAllKeys()) {
					if (entity instanceof VillagerDataHolder villager) {
						if (BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString().equals(variantKeys.getString(s))) {
							return i;
						}
					} else {
						Tag tagVer = tag.get(s);
						Tag variantVer = variantKeys.get(s);
						if (variantVer instanceof NumericTag num) {
							//most values save as bytes in the json, but sometimes they also be things like shorts.
							//we'll compare both numbers to long as they should always match this way.
							//comparing to int is going to cause issues for doubles
							if (tagVer instanceof NumericTag number && number.getAsLong() == num.getAsLong()) {
								return i;
							}
						} else if (Objects.equals(tagVer, variantVer)) {
							return i;
						}
					}
				}
			}
		}
		return 0;
	}
}
