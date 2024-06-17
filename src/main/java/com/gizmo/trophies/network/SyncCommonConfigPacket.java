package com.gizmo.trophies.network;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCommonConfigPacket(boolean fakePlayerDrops, boolean anySourceDrops) implements CustomPacketPayload {

	public static final Type<SyncCommonConfigPacket> TYPE = new Type<>(OpenBlocksTrophies.prefix("sync_common_config"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncCommonConfigPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, SyncCommonConfigPacket::fakePlayerDrops,
		ByteBufCodecs.BOOL, SyncCommonConfigPacket::anySourceDrops,
		SyncCommonConfigPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncCommonConfigPacket message, IPayloadContext context) {
		context.enqueueWork(() -> {
			TrophyConfig.fakePlayersDropTrophies = message.fakePlayerDrops();
			TrophyConfig.anySourceDropsTrophies = message.anySourceDrops();
		});
	}
}
