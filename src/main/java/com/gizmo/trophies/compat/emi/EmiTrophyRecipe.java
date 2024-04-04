package com.gizmo.trophies.compat.emi;

import com.gizmo.trophies.compat.TrophyRecipeViewerConstants;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record EmiTrophyRecipe(Trophy trophy, int variant) implements EmiRecipe {

	public static final EmiTexture BACKGROUND = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 0, 0, TrophyRecipeViewerConstants.WIDTH, TrophyRecipeViewerConstants.HEIGHT);
	public static final EmiTexture ANY_SOURCE_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 32, 23, 15);
	public static final EmiTexture FAKE_PLAYER_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 0, 16, 16);
	public static final EmiTexture PLAYER_INDICATOR = new EmiTexture(TrophyRecipeViewerConstants.BACKGROUND, 116, 16, 16, 16);

	@Override
	public EmiRecipeCategory getCategory() {
		return EmiCompat.TROPHY;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return null;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		SpawnEggItem egg = DeferredSpawnEggItem.byId(this.trophy().type());
		if (egg != null) {
			return List.of(EmiStack.of(egg));
		}
		return List.of();
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(TrophyItem.loadEntityToTrophy(this.trophy().type(), this.variant(), false)));
	}

	@Override
	public int getDisplayWidth() {
		return TrophyRecipeViewerConstants.WIDTH;
	}

	@Override
	public int getDisplayHeight() {
		return TrophyRecipeViewerConstants.HEIGHT;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(BACKGROUND, 0, 0);
		widgets.add(new EmiEntityWidget(this.trophy().type(), 10, 11, 32, Optional.ofNullable(TrophyRecipeViewerConstants.getTrophyVariant(this.trophy(), this.variant())), this.trophy().defaultData()));

		if (!TrophyConfig.anySourceDropsTrophies) {
			if (TrophyConfig.fakePlayersDropTrophies) {
				widgets.addTexture(FAKE_PLAYER_INDICATOR, 54, 19).tooltipText(List.of(TrophyRecipeViewerConstants.PLAYER_DROP_ONLY.copy().append(TrophyRecipeViewerConstants.FAKE_PLAYER_DROPS)));
			} else {
				widgets.addTexture(PLAYER_INDICATOR, 54, 19).tooltipText(List.of(TrophyRecipeViewerConstants.PLAYER_DROP_ONLY));
			}
		} else {
			widgets.addTexture(ANY_SOURCE_INDICATOR, 50, 19);
		}
		widgets.addText(Component.translatable("gui.obtrophies.jei.drop_chance", TrophyRecipeViewerConstants.getTrophyDropPercentage(this.trophy())), 46, 45, 0xFF808080, false);
		widgets.addSlot(this.getOutputs().get(0), 81, 14).large(true).drawBack(false);
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}
}
