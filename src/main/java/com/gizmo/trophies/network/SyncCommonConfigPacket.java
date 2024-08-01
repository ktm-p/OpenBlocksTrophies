package com.gizmo.trophies.network;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCommonConfigPacket(TrophyConfig.TrophySourceDrop drop) implements CustomPacketPayload {

	public static final Type<SyncCommonConfigPacket> TYPE = new Type<>(OpenBlocksTrophies.prefix("sync_common_config"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncCommonConfigPacket> STREAM_CODEC = StreamCodec.composite(TrophyConfig.TrophySourceDrop.STREAM_CODEC, SyncCommonConfigPacket::drop, SyncCommonConfigPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncCommonConfigPacket message, IPayloadContext context) {
		context.enqueueWork(() -> {
			TrophyConfig.trophyDropSource = message.drop();
		});
	}
}
