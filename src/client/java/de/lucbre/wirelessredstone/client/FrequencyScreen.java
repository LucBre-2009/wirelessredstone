package de.lucbre.wirelessredstone.client;

import de.lucbre.wirelessredstone.network.WirelessRedstonePayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public final class FrequencyScreen extends Screen {
    private final BlockPos pos;
    private final int currentFrequency;
    private final int range;
    private EditBox frequencyField;

    public FrequencyScreen(BlockPos pos, int currentFrequency, int range) {
        super(Component.literal("Wireless Redstone"));
        this.pos = pos;
        this.currentFrequency = currentFrequency;
        this.range = range;
    }

    @Override
    protected void init() {
        int left = (this.width - 240) / 2;
        int top = (this.height - 112) / 2;
        this.frequencyField = new EditBox(this.font, left + 20, top + 42, 200, 20, Component.literal("Frequency"));
        this.frequencyField.setMaxLength(3);
        this.frequencyField.setValue(Integer.toString(this.currentFrequency));
        this.addRenderableWidget(this.frequencyField);
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveFrequency()).bounds(left + 20, top + 76, 94, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> onClose()).bounds(left + 126, top + 76, 94, 20).build());
        this.setInitialFocus(this.frequencyField);
    }

    private void saveFrequency() {
        try {
            int frequency = Integer.parseInt(this.frequencyField.getValue().trim());
            if (frequency < 0 || frequency > 256) return;
            ClientPlayNetworking.send(new WirelessRedstonePayloads.SetFrequency(this.pos, frequency));
            this.minecraft.gui.setScreen(null);
        } catch (NumberFormatException ignored) {
            this.frequencyField.setFocused(true);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int left = (this.width - 240) / 2;
        int top = (this.height - 112) / 2;
        graphics.text(this.font, "Frequency (0-256)", left + 20, top + 22, 0xFFFFFFFF, true);
        graphics.text(this.font, "Range: " + (this.range == 0 ? "Unlimited (loaded chunks)" : this.range + " blocks"), left + 20, top + 104, 0xFFB8B8B8, true);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(null);
    }
}
