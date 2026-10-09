package de.lucbre.wirelessredstone.network;

import de.lucbre.wirelessredstone.WirelessRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class WirelessRedstonePayloads {
    private WirelessRedstonePayloads() {}

    public record RequestFrequency(BlockPos pos) implements CustomPacketPayload {
        public static final Type<RequestFrequency> TYPE = new Type<>(WirelessRedstone.id("request_frequency"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestFrequency> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, RequestFrequency::pos, RequestFrequency::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SetFrequency(BlockPos pos, int frequency) implements CustomPacketPayload {
        public static final Type<SetFrequency> TYPE = new Type<>(WirelessRedstone.id("set_frequency"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SetFrequency> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, SetFrequency::pos, ByteBufCodecs.VAR_INT, SetFrequency::frequency, SetFrequency::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record OpenFrequency(BlockPos pos, int frequency, int range) implements CustomPacketPayload {
        public static final Type<OpenFrequency> TYPE = new Type<>(WirelessRedstone.id("open_frequency"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenFrequency> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, OpenFrequency::pos, ByteBufCodecs.VAR_INT, OpenFrequency::frequency, ByteBufCodecs.VAR_INT, OpenFrequency::range, OpenFrequency::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
