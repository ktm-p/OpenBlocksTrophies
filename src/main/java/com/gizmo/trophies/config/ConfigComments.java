package com.gizmo.trophies.config;

public final class ConfigComments {
	//CLIENT
	public static final String RENDER_NAMES = "If true, player trophies will render their names over their head similar to how players do.";
	public static final String RENDER_NAME_DECO = """
		If true, some player trophies will render with special icons or name colors.
		If you find this to be too distracting for some reason, you can turn this off to keep the names plainly formatted.""";

	//COMMON
	public static final String RIGHT_CLICK_OVERRIDE = """
		If true, certain trophies will not do special things when right clicked.
		This can already be changed per trophy with a datapack, but this config option remains for those who want to disable all right click behaviors for every trophy.
		Note that this will not affect mobs playing sounds when right clicked, just behaviors like the squid spawning water or rabbits dropping carrots.""";
	public static final String DROP_OVERRIDE = """
		The chance a trophy will drop from its respective mob.
		All trophy drop chances are defined in their trophy json, but if you want to override that chance without going through and changing every json this is for you.
		This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
		Set this value to any negative number to disable the override.""";
	public static final String DROP_SOURCE = """
		Determines what damage source a mob needs to be killed by in order to have a chance to drop a trophy. The following values are allowed:
		ALL: allows trophies to drop whenever a mob dies. This can be to fall damage, another mob, etc.
		FAKE_PLAYER: allows trophies to drop from players and fake players. This can allow mob grinders from other mobs to drop trophies, such as the mob masher from MobGrindingUtils.
		PLAYER: only player kills will drop trophies.""";
	public static final String CHARGED_CREEPER = """
		The chance a player will drop a trophy when killed by a charged creeper.
		This config mostly exists for singleplayer worlds where getting a player kill on a player is rather difficult.
		This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
		Set this value to 0.0 to disable drops from charged creeper kills.""";
}
