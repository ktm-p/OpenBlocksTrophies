package com.gizmo.trophies.behavior;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record CustomBehaviorType(MapCodec<? extends CustomBehavior> codec) {
	public static final Codec<CustomBehavior> DISPATCH_CODEC = Codec.lazyInitialized(OpenBlocksTrophies.CUSTOM_BEHAVIORS::byNameCodec).dispatch("type", CustomBehavior::getType, CustomBehaviorType::codec);
}
