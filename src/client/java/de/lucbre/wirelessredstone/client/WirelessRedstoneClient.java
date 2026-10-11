package de.lucbre.wirelessredstone.client;

import de.lucbre.wirelessredstone.WirelessRedstone;
import de.lucbre.wirelessredstone.network.WirelessRedstonePayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

public final class WirelessRedstoneClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WirelessRedstonePayloads.OpenFrequency.TYPE, (payload, context) ->
                context.client().execute(() -> context.client().gui.setScreen(new FrequencyScreen(payload.pos(), payload.frequency(), payload.range()))));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!level.isClientSide() || hand != InteractionHand.MAIN_HAND || !player.isShiftKeyDown()) return InteractionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.is(WirelessRedstone.WIRELESS_SWITCH) || state.is(WirelessRedstone.WIRELESS_LAMP)) {
                ClientPlayNetworking.send(new WirelessRedstonePayloads.RequestFrequency(pos));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }
}