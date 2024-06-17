package com.gizmo.trophies.config;

import com.gizmo.trophies.network.SyncCommonConfigPacket;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class TrophyConfig {

	// -- CLIENT CONFIG --
	public static boolean playersRenderNames = true;
	public static boolean renderNameColorsAndIcons = true;

	// -- COMMON CONFIG --
	public static boolean fakePlayersDropTrophies = false;
	public static boolean anySourceDropsTrophies = false;
	public static boolean rightClickEffectOverride = false;
	public static double dropChanceOverride = -1.0D;
	public static double playerChargedCreeperDropChance = 0.2D;

	static void rebakeClientOptions(TrophyClientConfig config) {
		playersRenderNames = config.playersRenderNames.get();
		renderNameColorsAndIcons = config.renderNameColorsAndIcons.get();
	}

	static void rebakeCommonOptions(TrophyCommonConfig config) {
		fakePlayersDropTrophies = config.fakePlayersDropTrophies.get();
		anySourceDropsTrophies = config.anySourceDropsTrophies.get();
		rightClickEffectOverride = config.rightClickEffectOverride.get();
		dropChanceOverride = config.dropChanceOverride.get();
		playerChargedCreeperDropChance = config.playerChargedCreeperDropChance.get();

		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null && server.isDedicatedServer()) {
			PacketDistributor.sendToAllPlayers(new SyncCommonConfigPacket(fakePlayersDropTrophies, anySourceDropsTrophies));
		}
	}
}
