package de.lucbre.wirelessredstone.block.entity;

import de.lucbre.wirelessredstone.WirelessRedstone;
import de.lucbre.wirelessredstone.block.WirelessReceiverBlock;
import de.lucbre.wirelessredstone.block.WirelessSenderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class WirelessNodeBlockEntity extends BlockEntity {
    private static final Set<WirelessNodeBlockEntity> LOADED_SENDERS = new HashSet<>();

    private int frequency = 1;
    // 0 means unlimited within currently loaded chunks.
    private int range = 2048;
    private int tickCounter;

    public WirelessNodeBlockEntity(BlockPos pos, BlockState state) {
        super(WirelessRedstone.NODE_ENTITY, pos, state);
        // Block entities are constructed as their chunks are loaded or when placed.
        // Register senders here because this Minecraft version has no BlockEntity.onLoad().
        if (state.getBlock() instanceof WirelessSenderBlock) {
            LOADED_SENDERS.add(this);
        }
    }

    public int frequency() { return frequency; }
    public int range() { return range; }

    @Override
    public void setRemoved() {
        LOADED_SENDERS.remove(this);
        super.setRemoved();
    }

    public void setFrequency(int frequency) {
        if (level == null || level.isClientSide()) return;
        this.frequency = Math.max(0, Math.min(256, frequency));
        setChanged();
    }

    public void cycleRange() {
        if (level == null || level.isClientSide()) return;
        range = range == 1024 ? 2048 : range == 2048 ? 0 : 1024;
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
        output.putInt("frequency", frequency);
        output.putInt("range", range);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String oldChannel = input.getStringOr("channel", "default");
        int legacyFrequency = 1;
        if (oldChannel.startsWith("channel-")) {
            try {
                legacyFrequency = Integer.parseInt(oldChannel.substring("channel-".length()));
            } catch (NumberFormatException ignored) {
                legacyFrequency = 1;
            }
        }
        frequency = Math.max(0, Math.min(256, input.getIntOr("frequency", legacyFrequency)));
        int savedRange = input.getIntOr("range", 2048);
        // Old prototype ranges (4/8/16) are upgraded to the new default.
        range = savedRange == 1024 || savedRange == 2048 || savedRange == 0 ? savedRange : 2048;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WirelessNodeBlockEntity entity) {
        if (level.isClientSide() || !(state.getBlock() instanceof WirelessReceiverBlock)) return;
        if (++entity.tickCounter < 10) return;
        entity.tickCounter = 0;

        boolean powered = false;
        long maxDistanceSqr = entity.range == 0 ? Long.MAX_VALUE : (long) entity.range * entity.range;
        Iterator<WirelessNodeBlockEntity> iterator = LOADED_SENDERS.iterator();
        while (iterator.hasNext()) {
            WirelessNodeBlockEntity sender = iterator.next();
            if (sender.isRemoved() || sender.level == null) {
                iterator.remove();
                continue;
            }
            if (sender.level != level || sender.frequency != entity.frequency) continue;
            if (sender.worldPosition.distSqr(pos) > maxDistanceSqr) continue;
            if (level.hasNeighborSignal(sender.worldPosition)) {
                powered = true;
                break;
            }
        }

        if (state.getBlock() instanceof WirelessReceiverBlock receiver) {
            receiver.setPowered(level, pos, state, powered);
        }
    }
}
