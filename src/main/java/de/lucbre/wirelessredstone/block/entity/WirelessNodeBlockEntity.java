package de.lucbre.wirelessredstone.block.entity;

import de.lucbre.wirelessredstone.WirelessRedstone;
import de.lucbre.wirelessredstone.block.WirelessReceiverBlock;
import de.lucbre.wirelessredstone.block.WirelessSenderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class WirelessNodeBlockEntity extends BlockEntity {
    private String channel = "default";
    private int range = 16;
    private int tickCounter;

    public WirelessNodeBlockEntity(BlockPos pos, BlockState state) {
        super(WirelessRedstone.NODE_ENTITY, pos, state);
    }

    public String channel() { return channel; }
    public int range() { return range; }

    public void cycleChannel() {
        if (level == null || level.isClientSide()) return;
        if ("default".equals(channel)) channel = "channel-2";
        else {
            try {
                int number = Integer.parseInt(channel.substring("channel-".length()));
                channel = number >= 16 ? "default" : "channel-" + (number + 1);
            } catch (RuntimeException ignored) {
                channel = "default";
            }
        }
        setChanged();
    }

    public void cycleRange() {
        if (level == null || level.isClientSide()) return;
        range = range == 4 ? 8 : range == 8 ? 16 : 4;
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
            level.updateNeighborsAt(worldPosition, state.getBlock());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("channel", channel);
        output.putInt("range", range);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        channel = input.getStringOr("channel", "default");
        int savedRange = input.getIntOr("range", 16);
        range = savedRange == 4 || savedRange == 8 || savedRange == 16 ? savedRange : 16;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WirelessNodeBlockEntity entity) {
        if (level.isClientSide() || !(state.getBlock() instanceof WirelessReceiverBlock)) return;
        if (++entity.tickCounter < 10) return;
        entity.tickCounter = 0;
        boolean powered = false;
        int r = entity.range;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        search:
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + y * y + z * z > r * r) continue;
                    cursor.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (level.getBlockEntity(cursor) instanceof WirelessNodeBlockEntity sender
                            && level.getBlockState(cursor).getBlock() instanceof WirelessSenderBlock
                            && sender.channel.equals(entity.channel)
                            && level.hasNeighborSignal(cursor)) {
                        powered = true;
                        break search;
                    }
                }
            }
        }
        if (state.getBlock() instanceof WirelessReceiverBlock receiver) {
            receiver.setPowered(level, pos, state, powered);
        }
    }
}
