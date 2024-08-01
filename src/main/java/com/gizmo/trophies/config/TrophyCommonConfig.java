package com.gizmo.trophies.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TrophyCommonConfig {
	final ModConfigSpec.EnumValue<TrophyConfig.TrophySourceDrop> trophyDropSource;
	final ModConfigSpec.BooleanValue rightClickEffectOverride;
	final ModConfigSpec.DoubleValue dropChanceOverride;
	final ModConfigSpec.DoubleValue playerChargedCreeperDropChance;

	public TrophyCommonConfig(ModConfigSpec.Builder builder) {
		this.trophyDropSource = builder
				.translation("config.obtrophies.trophy_source_drop")
				.comment(ConfigComments.DROP_SOURCE)
				.defineEnum("trophySourceDrop", TrophyConfig.TrophySourceDrop.PLAYER);

		this.rightClickEffectOverride = builder
				.translation("config.obtrophies.right_click_override")
				.comment(ConfigComments.RIGHT_CLICK_OVERRIDE)
				.define("globalRightClickOverride", false);

		this.dropChanceOverride = builder
				.translation("config.obtrophies.drop_chance")
				.comment(ConfigComments.DROP_OVERRIDE)
				.defineInRange("globalTrophyDropOverride", -1.0D, -1.0D, 1.0D);

		this.playerChargedCreeperDropChance = builder
				.translation("config.obtrophies.player_creeper_chance")
				.comment(ConfigComments.CHARGED_CREEPER)
				.defineInRange("chargedCreeperPlayerTrophyChance", 0.2D, 0.0D, 1.0D);
	}
}
