package de.lucbre.wirelessredstone;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Initial project scaffold. Wireless signal handling and the configuration UI
 * are intentionally not represented as finished features yet.
 */
public final class WirelessRedstone implements ModInitializer {
    public static final String MOD_ID = "wirelessredstone";

    public static final Block WIRELESS_SENDER = registerBlock("wireless_sender");
    public static final Block WIRELESS_LINK = registerBlock("wireless_link");

    private static Block registerBlock(String path) {
        Identifier id = Identifier.of(MOD_ID, path);
        Block block = new Block(AbstractBlock.Settings.create().strength(2.0F));
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    @Override
    public void onInitialize() {
        // TODO: implement server-side frequency storage and wireless redstone.
    }
}
