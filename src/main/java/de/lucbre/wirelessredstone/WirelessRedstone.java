package de.lucbre.wirelessredstone;

import de.lucbre.wirelessredstone.block.WirelessReceiverBlock;
import de.lucbre.wirelessredstone.block.WirelessSenderBlock;
import de.lucbre.wirelessredstone.block.entity.WirelessNodeBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
                    net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder
                            .create(WirelessNodeBlockEntity::new, WIRELESS_SENDER, WIRELESS_RECEIVER).build());

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
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(tab -> {
            tab.accept(WIRELESS_SENDER.asItem());
            tab.accept(WIRELESS_RECEIVER.asItem());
        });
        LOGGER.info("Wireless Redstone loaded: sender and receiver blocks registered.");
    }
}
