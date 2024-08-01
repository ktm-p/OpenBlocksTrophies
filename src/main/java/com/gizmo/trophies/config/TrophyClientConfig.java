package com.gizmo.trophies.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TrophyClientConfig {
	final ModConfigSpec.BooleanValue playersRenderNames;
	final ModConfigSpec.BooleanValue renderNameColorsAndIcons;

	public TrophyClientConfig(ModConfigSpec.Builder builder) {
		this.playersRenderNames = builder
				.translation("config.obtrophies.render_names")
				.comment(ConfigComments.RENDER_NAMES)
				.define("renderPlayerTrophyNames", true);

		this.renderNameColorsAndIcons = builder
				.translation("config.obtrophies.render_name_decorators")
				.comment(ConfigComments.RENDER_NAME_DECO)
				.define("renderPlayerTrophyNameDecorators", true);
	}
}
