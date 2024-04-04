package com.gizmo.trophies.client;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class ClientEvents {

	//if you see this and have to ask, no, you aren't being added to this list. Good day.
	private static final Map<String, Function<Component, Component>> SPECIAL_CASES = ImmutableMap.<String, Function<Component, Component>>builder()
			.put("celintro", name -> name.plainCopy().withStyle(ChatFormatting.GREEN).append(Component.literal(" ☠").withStyle(ChatFormatting.WHITE)))
			.put("fastcheeta", name -> name.plainCopy().withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal(" \uD83C\uDF3C").withStyle(ChatFormatting.LIGHT_PURPLE)))
			.put("derpderpling", name -> name.plainCopy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x641ACF))))
			.put("bigdious", name -> name.plainCopy().withStyle(ChatFormatting.DARK_RED).append(Component.literal(" ☺")))
			.put("melodioustwin", name -> name.plainCopy().withStyle(ChatFormatting.DARK_AQUA).append(Component.literal(" ♫")))
			.put("badneighbour", name -> name.plainCopy().withStyle(ChatFormatting.YELLOW).append(Component.literal(" \uD83D\uDE97")))
			.put("jodlodi", name -> name.plainCopy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x992D22))))
			.put("benimatic", name -> name.plainCopy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x11806A))))
			.put("killer_demon", name -> name.plainCopy().withStyle(ChatFormatting.RED))
			.put("drullkus", name -> name.plainCopy().withStyle(ChatFormatting.GOLD))
			.put("tamaized", name -> name.plainCopy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA4EA))))
			.put("alphaleaf", name -> name.plainCopy().withStyle(ChatFormatting.GREEN))
			.build();

	public static final ModelLayerLocation PLAYER_TROPHY = new ModelLayerLocation(new ResourceLocation(OpenBlocksTrophies.MODID, "player_trophy"), "main");
	public static final ModelLayerLocation SLIM_PLAYER_TROPHY = new ModelLayerLocation(new ResourceLocation(OpenBlocksTrophies.MODID, "slim_player_trophy"), "main");

	public static void init(IEventBus bus) {
		bus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerBlockEntityRenderer(TrophyRegistries.TROPHY_BE.get(), TrophyRenderer::new));
		bus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			event.registerLayerDefinition(PLAYER_TROPHY, () -> LayerDefinition.create(PlayerTrophyModel.createMesh(false), 64, 64));
			event.registerLayerDefinition(SLIM_PLAYER_TROPHY, () -> LayerDefinition.create(PlayerTrophyModel.createMesh(true), 64, 64));
		});
		NeoForge.EVENT_BUS.addListener(ClientEvents::dontRenderTrophyHitbox);
	}

	//disables rendering the trophy hitbox if there's no visible pedestal.
	//why? because I hate when hitboxes don't fit the block, and making it fit depending on entity is impossible.
	//so, we'll just make the hitbox rather large (almost a full block) but invisible.
	//this event also handles rendering name tags of player trophies when hovering over them
	public static void dontRenderTrophyHitbox(RenderHighlightEvent.Block event) {
		BlockState state = event.getCamera().getEntity().level().getBlockState(event.getTarget().getBlockPos());
		if (state.is(TrophyRegistries.TROPHY)) {
			if (TrophyConfig.playersRenderNames) {
				if (event.getCamera().getEntity().level().getBlockEntity(event.getTarget().getBlockPos()) instanceof TrophyBlockEntity trophy) {
					if (trophy.getTrophy() != null && trophy.getTrophy().type() == EntityType.PLAYER) {
						if (!trophy.getTrophyName().isBlank()) {
							renderNameTag(Component.literal(trophy.getTrophyName()), event.getCamera(), event.getTarget().getBlockPos(), event.getPoseStack(), event.getMultiBufferSource(), state.getValue(TrophyBlock.PEDESTAL));
						}
						event.setCanceled(true);
					}
				}
			}
			if (!state.getValue(TrophyBlock.PEDESTAL)) {
				event.setCanceled(true);
			}
		}
	}

	private static void renderNameTag(Component name, Camera camera, BlockPos pos, PoseStack stack, MultiBufferSource source, boolean pedestal) {
		Vec3 vec = camera.getPosition();
		float f = (float) (pos.getX() - vec.x());
		float f1 = (float) (pos.getY() - vec.y());
		float f2 = (float) (pos.getZ() - vec.z());
		int i = name.getString().equalsIgnoreCase("deadmau5") ? -10 : 0;
		float offset = pedestal ? 1.25F : 1.0F;
		name = handleSpecialNames(name);
		stack.pushPose();
		stack.translate(f + 0.5F, f1 + offset, f2 + 0.5F);
		stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		stack.scale(-0.015F, -0.015F, 0.015F);
		Matrix4f matrix4f = stack.last().pose();
		float opacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int j = (int) (opacity * 255.0F) << 24;
		Font font = Minecraft.getInstance().font;
		float width = (float) (-font.width(name) / 2);
		font.drawInBatch(name, width, (float) i, 553648127, false, matrix4f, source, Font.DisplayMode.NORMAL, j, LightTexture.FULL_BRIGHT);
		font.drawInBatch(name, width, (float) i, -1, false, matrix4f, source, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);

		stack.popPose();
	}

	private static Component handleSpecialNames(Component name) {
		Component ogName = name;
		if (ogName.getString().equalsIgnoreCase("gizmothemoonpig")) {
			name = name.copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0XFF3314))).append(Component.literal(" \uD83D\uDC51").withStyle(ChatFormatting.YELLOW));
		}
		if (ogName.getString().equalsIgnoreCase("tomatenjaeger")) {
			name = name.plainCopy().append(Component.literal(" ❤").withStyle(ChatFormatting.RED));
		}
		if (TrophyConfig.renderNameColorsAndIcons) {
			if (SPECIAL_CASES.containsKey(ogName.getString().toLowerCase())) {
				name = SPECIAL_CASES.get(ogName.getString().toLowerCase()).apply(name);
			}

			if (PlayerInfoHolder.TF_DEVS.contains(ogName.getString().toLowerCase(Locale.ROOT))) {
				name = name.copy().append(Component.literal("\uE115").withStyle(ChatFormatting.WHITE));
			}

			if (PlayerInfoHolder.MOJANGSTAS.contains(ogName.getString().toLowerCase(Locale.ROOT))) {
				name = name.copy().append(Component.literal("\uF56E").withStyle(ChatFormatting.DARK_RED));
			}
		}

		return name;
	}
}
