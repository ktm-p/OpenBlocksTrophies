package com.gizmo.trophies.item;

import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class TrophyItem extends BlockItem {

	public TrophyItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	public static Trophy getTrophy(@Nonnull ItemStack stack) {
		TrophyInfo info = stack.get(TrophyRegistries.TROPHY_INFO);
		if (info != null) {
			ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(info.type());
			if (Trophy.getTrophies().containsKey(entityKey)) {
				return Trophy.getTrophies().get(entityKey);
			}
		}

		return null;
	}

	public static boolean hasCycleOnTrophy(@Nonnull ItemStack stack) {
		TrophyInfo info = stack.get(TrophyRegistries.TROPHY_INFO);
		if (info != null) {
			return info.cycling().isPresent();
		}

		return false;
	}

	public static ItemStack loadEntityToTrophy(EntityType<?> type) {
		return loadVariantToTrophy(type, new CompoundTag());
	}

	public static ItemStack loadVariantToTrophy(EntityType<?> type, CompoundTag variant) {
		ItemStack stack = new ItemStack(TrophyRegistries.TROPHY_ITEM.get());
		stack.set(TrophyRegistries.TROPHY_INFO, new TrophyInfo(type, variant));
		stack.set(DataComponents.RARITY, getTrophyRarity(stack));
		return stack;
	}

	public static ItemStack createCyclingTrophy(EntityType<?> type) {
		ItemStack stack = new ItemStack(TrophyRegistries.TROPHY_ITEM.get());
		stack.set(TrophyRegistries.TROPHY_INFO, new TrophyInfo(type, !Trophy.getTrophies().isEmpty()));
		stack.set(DataComponents.RARITY, getTrophyRarity(stack));
		return stack;
	}

	public static CompoundTag getTrophyVariant(@Nonnull ItemStack stack) {
		TrophyInfo info = stack.get(TrophyRegistries.TROPHY_INFO);
		if (info != null && info.variant().isPresent()) {
			return info.variant().get();
		}

		return new CompoundTag();
	}

	public static Rarity getTrophyRarity(ItemStack stack) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null) {
			if (trophy.type() == EntityType.PLAYER) {
				return Rarity.EPIC;
			} else if (trophy.type().is(Tags.EntityTypes.BOSSES) || trophy.dropChance() >= Trophy.BOSS_DROP_CHANCE) {
				return Rarity.RARE;
			} else {
				return Rarity.UNCOMMON;
			}
		}
		return Rarity.COMMON;
	}

	@Override
	public Component getName(ItemStack stack) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null && !hasCycleOnTrophy(stack)) {
			return Component.translatable("block.obtrophies.trophy.entity", trophy.type().getDescription().plainCopy().getString());
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		Trophy trophy = getTrophy(stack);
		if (trophy != null && !hasCycleOnTrophy(stack)) {
			tooltip.add(Component.translatable("item.obtrophies.trophy.modid", this.getModIdForTooltip(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(trophy.type())).getNamespace())).withStyle(ChatFormatting.GRAY));
			if (flag.isAdvanced()) {
				CompoundTag variant = getTrophyVariant(stack);
				HolderLookup.Provider provider = context.registries();
				if (provider != null && !trophy.getVariants(provider).isEmpty() && !variant.isEmpty()) {
					variant.getAllKeys().forEach(s -> tooltip.add(Component.translatable("item.obtrophies.trophy.variant", s, Objects.requireNonNull(variant.get(s)).getAsString()).withStyle(ChatFormatting.GRAY)));
				}
			}
		}
	}

	private String getModIdForTooltip(String modId) {
		return ModList.get().getModContainerById(modId)
				.map(ModContainer::getModInfo)
				.map(IModInfo::getDisplayName)
				.orElseGet(() -> StringUtils.capitalize(modId));
	}

	@Override
	@Nullable
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}
}
