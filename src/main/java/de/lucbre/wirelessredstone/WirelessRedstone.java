package de.lucbre.wirelessredstone;

import de.lucbre.wirelessredstone.block.WirelessReceiverBlock;
import de.lucbre.wirelessredstone.block.WirelessSenderBlock;
import de.lucbre.wirelessredstone.block.entity.WirelessNodeBlockEntity;
import de.lucbre.wirelessredstone.network.WirelessRedstonePayloads;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WirelessRedstone implements ModInitializer {
    public static final String MOD_ID = "wirelessredstone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Block WIRELESS_SENDER = registerBlock("wireless_sender", WirelessSenderBlock::new);
    public static final Block WIRELESS_RECEIVER = registerBlock("wireless_receiver", WirelessReceiverBlock::new);
    public static final net.minecraft.world.level.block.entity.BlockEntityType<WirelessNodeBlockEntity> NODE_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("wireless_node"),
                    FabricBlockEntityTypeBuilder.create(WirelessNodeBlockEntity::new, WIRELESS_SENDER, WIRELESS_RECEIVER).build());

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static Block registerBlock(String name, java.util.function.Function<BlockBehaviour.Properties, Block> factory) {
        Identifier identifier = id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, identifier);
        Block block = factory.apply(BlockBehaviour.Properties.of().setId(key).strength(2.0F));
        Registry.register(BuiltInRegistries.BLOCK, key, block);
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
        Item item = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return block;
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(WirelessRedstonePayloads.RequestFrequency.TYPE, WirelessRedstonePayloads.RequestFrequency.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(WirelessRedstonePayloads.SetFrequency.TYPE, WirelessRedstonePayloads.SetFrequency.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(WirelessRedstonePayloads.OpenFrequency.TYPE, WirelessRedstonePayloads.OpenFrequency.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(WirelessRedstonePayloads.RequestFrequency.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            Level level = player.level();
            if (player.blockPosition().distSqr(payload.pos()) > 64) return;
            if (!(level.getBlockEntity(payload.pos()) instanceof WirelessNodeBlockEntity node)) return;
            Block block = level.getBlockState(payload.pos()).getBlock();
            if (!(block instanceof WirelessSenderBlock) && !(block instanceof WirelessReceiverBlock)) return;
            ServerPlayNetworking.send(player, new WirelessRedstonePayloads.OpenFrequency(payload.pos(), node.frequency(), node.range()));
        });

        ServerPlayNetworking.registerGlobalReceiver(WirelessRedstonePayloads.SetFrequency.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            Level level = player.level();
            if (payload.frequency() < 0 || payload.frequency() > 256) return;
            if (player.blockPosition().distSqr(payload.pos()) > 64) return;
            if (!(level.getBlockEntity(payload.pos()) instanceof WirelessNodeBlockEntity node)) return;
            Block block = level.getBlockState(payload.pos()).getBlock();
            if (!(block instanceof WirelessSenderBlock) && !(block instanceof WirelessReceiverBlock)) return;
            node.setFrequency(payload.frequency());
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(tab -> {
            tab.accept(WIRELESS_SENDER.asItem());
            tab.accept(WIRELESS_RECEIVER.asItem());
        });
        LOGGER.info("Wireless Redstone loaded: numeric frequency menu and compact blocks registered.");
    }
}
