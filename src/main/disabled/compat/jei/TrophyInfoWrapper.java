package com.gizmo.trophies.compat.jei;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record TrophyInfoWrapper(Trophy trophy, int variant) implements IRecipeCategoryExtension<TrophyInfoWrapper> {

	public EntityType<?> getTrophyEntity() {
		return this.trophy().type();
	}

	public ItemStack getTrophyItem() {
		return TrophyItem.loadEntityToTrophy(this.trophy().type(), this.variant(), false);
	}

	public Optional<CompoundTag> getDefaultTrophyVariant() {
		return this.trophy().defaultData();
	}
}
