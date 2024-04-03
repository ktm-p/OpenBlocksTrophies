package com.gizmo.trophies.network;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SyncCommonConfigPacket(boolean fakePlayerDrops, boolean anySourceDrops) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(OpenBlocksTrophies.MODID, "sync_common_config");

	public SyncCommonConfigPacket(FriendlyByteBuf buf) {
		this(buf.readBoolean(), buf.readBoolean());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(this.fakePlayerDrops());
		buf.writeBoolean(this.anySourceDrops());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(SyncCommonConfigPacket message, PlayPayloadContext ctx) {
		ctx.workHandler().execute(() -> {
			TrophyConfig.fakePlayersDropTrophies = message.fakePlayerDrops();
			TrophyConfig.anySourceDropsTrophies = message.anySourceDrops();
		});
	}
}
