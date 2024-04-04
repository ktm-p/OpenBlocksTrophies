package com.gizmo.trophies.compat.emi;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
	public static final EmiRecipeCategory TROPHY = new EmiRecipeCategory(new ResourceLocation(OpenBlocksTrophies.MODID, "trophy"), EmiStack.of(TrophyItem.loadEntityToTrophy(EntityType.CHICKEN, 0, !Trophy.getTrophies().isEmpty())));

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(TROPHY);

		if (!Trophy.getTrophies().isEmpty()) {
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : Trophy.getTrophies().entrySet()) {
				if (trophyEntry.getValue().type() == EntityType.PLAYER || OpenBlocksTrophies.getTrophyDropChance(trophyEntry.getValue()) <= 0.0D) continue;
				if (!trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty()) {
					for (int i = 0; i < trophyEntry.getValue().getVariants(Minecraft.getInstance().level.registryAccess()).size(); i++) {
						registry.addRecipe(new EmiTrophyRecipe(trophyEntry.getValue(), i));
					}
				} else {
					registry.addRecipe(new EmiTrophyRecipe(trophyEntry.getValue(), 0));
				}
			}
		}
	}
}
