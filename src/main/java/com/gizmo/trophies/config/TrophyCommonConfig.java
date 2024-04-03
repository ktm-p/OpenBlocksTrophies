package com.gizmo.trophies.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class TrophyCommonConfig {
	final ModConfigSpec.BooleanValue fakePlayersDropTrophies;
	final ModConfigSpec.BooleanValue anySourceDropsTrophies;
	final ModConfigSpec.BooleanValue rightClickEffectOverride;
	final ModConfigSpec.DoubleValue dropChanceOverride;
	final ModConfigSpec.DoubleValue playerChargedCreeperDropChance;

	public TrophyCommonConfig(ModConfigSpec.Builder builder) {
		this.fakePlayersDropTrophies = builder
				.translation("obtrophies.config.fake_player_drops")
				.comment("""
						If true, fake players will allow mobs to drop trophies.
						This can allow mob grinders from other mobs to drop trophies, such as the mob masher from MobGrindingUtils.""")
				.define("fake_player_drops", false);

		this.anySourceDropsTrophies = builder
				.translation("obtrophies.config.any_kill_drops")
				.comment("""
						If true, allows trophies to drop whenever a mob dies. This can be to fall damage, another mob, etc.
						Basically, a kill doesnt have to count as a player kill for a trophy to drop.""")
				.define("any_kill_drops", false);

		this.rightClickEffectOverride = builder
				.translation("obtrophies.config.right_click_override")
				.comment("""
						If true, certain trophies will not do special things when right clicked.
						This can already be changed per trophy with a datapack, but this config option remains for those who want to disable all right click behaviors for every trophy.
						Note that this will not affect mobs playing sounds when right clicked, just behaviors like the squid spawning water or rabbits dropping carrots.""")
				.define("right_click_override", false);

		this.dropChanceOverride = builder
				.translation("obtrophies.config.drop_chance")
				.comment("""
						The chance a trophy will drop from its respective mob.
						All trophy drop chances are defined in their trophy json, but if you want to override that chance without going through and changing every json this is for you.
						This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
						Set this value to any negative number to disable the override.""")
				.defineInRange("trophy_drop_override", -1.0D, -1.0D, 1.0D);

		this.playerChargedCreeperDropChance = builder
				.translation("obtrophies.config.player_creeper_chance")
				.comment("""
						The chance a player will drop a trophy when killed by a charged creeper.
						This config mostly exists for singleplayer worlds where getting a player kill on a player is rather difficult.
						This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
						Set this value to 0.0 to disable drops from charged creeper kills.""")
				.defineInRange("charged_creeper_player_trophy_chance", 0.2D, 0.0D, 1.0D);
	}
}
