package com.gizmo.trophies.network;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import com.google.common.collect.Maps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record SyncTrophyConfigsPacket(Map<ResourceLocation, Trophy> trophies) implements CustomPacketPayload {

	public static final Type<SyncTrophyConfigsPacket> TYPE = new Type<>(OpenBlocksTrophies.prefix("sync_trophy_configs"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncTrophyConfigsPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(Maps::newHashMapWithExpectedSize, ResourceLocation.STREAM_CODEC, ByteBufCodecs.fromCodecTrusted(Trophy.BASE_CODEC)),
		SyncTrophyConfigsPacket::trophies, SyncTrophyConfigsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncTrophyConfigsPacket message, IPayloadContext context) {
		context.enqueueWork(() -> {
			TrophyReloadListener.getValidTrophies().putAll(message.trophies());
			OpenBlocksTrophies.LOGGER.debug("Received {} trophy configs from server.", message.trophies().size());
		});
	}
}
