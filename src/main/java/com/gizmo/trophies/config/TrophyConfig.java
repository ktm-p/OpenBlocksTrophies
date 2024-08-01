package com.gizmo.trophies.config;

import com.gizmo.trophies.network.SyncCommonConfigPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.common.TranslatableEnum;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Locale;
import java.util.function.IntFunction;

public class TrophyConfig {

	// -- CLIENT CONFIG --
	public static boolean playersRenderNames = true;
	public static boolean renderNameColorsAndIcons = true;

	// -- COMMON CONFIG --
	public static TrophySourceDrop trophyDropSource = TrophySourceDrop.PLAYER;
	public static boolean rightClickEffectOverride = false;
	public static double dropChanceOverride = -1.0D;
	public static double playerChargedCreeperDropChance = 0.2D;

	static void rebakeClientOptions(TrophyClientConfig config) {
		playersRenderNames = config.playersRenderNames.get();
		renderNameColorsAndIcons = config.renderNameColorsAndIcons.get();
	}

	static void rebakeCommonOptions(TrophyCommonConfig config) {
		trophyDropSource = config.trophyDropSource.get();
		rightClickEffectOverride = config.rightClickEffectOverride.get();
		dropChanceOverride = config.dropChanceOverride.get();
		playerChargedCreeperDropChance = config.playerChargedCreeperDropChance.get();

		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null && server.isDedicatedServer()) {
			PacketDistributor.sendToAllPlayers(new SyncCommonConfigPacket(trophyDropSource));
		}
	}

	public enum TrophySourceDrop implements TranslatableEnum {
		ALL,
		FAKE_PLAYER,
		PLAYER;

		public static final IntFunction<TrophySourceDrop> BY_ID = ByIdMap.continuous(TrophySourceDrop::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, TrophySourceDrop> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, TrophySourceDrop::ordinal);

		@Override
		public Component getTranslatedName() {
			return Component.translatable("config.obtrophies.source_drop." + this.name().toLowerCase(Locale.ROOT));
		}
	}
}
