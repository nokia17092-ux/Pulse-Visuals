package com.pulsevisuals.hud;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ComboManager {
    private static final ComboManager INSTANCE = new ComboManager();
    private int combo = 0;
    private int ticksSinceLastHit = 0;
    private boolean showFlash = false;
    private int flashTimer = 0;

    public static ComboManager get() { return INSTANCE; }

    public void onHit() {
        if (!PulseConfig.get().comboEnabled) return;
        combo++;
        ticksSinceLastHit = 0;
        showFlash = true;
        flashTimer = 8;
    }

    public void onMiss() {
        combo = 0;
    }

    public void tick() {
        if (!PulseConfig.get().comboEnabled) return;
        ticksSinceLastHit++;
        if (ticksSinceLastHit >= PulseConfig.get().comboTimeout) {
            combo = 0;
            ticksSinceLastHit = 0;
        }
        if (flashTimer > 0) flashTimer--;
        else showFlash = false;
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().comboEnabled || combo < 2) return;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;

        int y = cy + 30;
        String comboText = combo + "x COMBO";

        float pulse = showFlash ? 1.0f + (float) flashTimer / 8f * 0.3f : 1.0f;
        int color = getComboColor(combo);

        // Shadow
        context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(comboText), cx, y, color);
    }

    private int getComboColor(int c) {
        if (c >= 10) return 0xFF3030;
        if (c >= 5)  return 0xFFAA00;
        return 0xFFD700;
    }

    public int getCombo() { return combo; }
}
