package com.gizmo.trophies.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TrophyClientConfig {
	final ModConfigSpec.BooleanValue playersRenderNames;
	final ModConfigSpec.BooleanValue renderNameColorsAndIcons;

	public TrophyClientConfig(ModConfigSpec.Builder builder) {
		this.playersRenderNames = builder
				.translation("obtrophies.config.players_render_names")
				.comment("If true, player trophies will render their names over their head similar to how players do.")
				.define("player_trophies_render_names", true);

		this.renderNameColorsAndIcons = builder
				.translation("obtrophies.config.render_name_decorators")
				.comment("""
						If true, some player trophies will render with special icons or name colors.
						If you find this to be too distracting for some reason, you can turn this off to keep the names plainly formatted.""")
				.define("render_player_trophy_name_decorators", true);
	}
}
