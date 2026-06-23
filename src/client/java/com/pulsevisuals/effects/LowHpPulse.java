package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;

public class LowHpPulse {
    private static final LowHpPulse INSTANCE = new LowHpPulse();
    private float pulsePhase = 0f;

    public static LowHpPulse get() { return INSTANCE; }

    public void tick() {
        pulsePhase += 0.08f;
        if (pulsePhase > Math.PI * 2) pulsePhase -= (float)(Math.PI * 2);
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().lowHpPulseEnabled || client.player == null) return;
        float maxHp = (float) client.player.getAttributeValue(EntityAttributes.MAX_HEALTH);
        float hp = client.player.getHealth();
        float ratio = hp / maxHp;
        if (ratio > PulseConfig.get().lowHpThreshold) return;

        float intensity = (1.0f - ratio / PulseConfig.get().lowHpThreshold);
        float pulse = (float)(Math.sin(pulsePhase) * 0.5f + 0.5f);
        float alpha = intensity * pulse * 0.45f;

        int a = (int)(alpha * 255);
        int c = PulseConfig.get().lowHpPulseColor;
        int color = (a << 24) | (c & 0x00FFFFFF);

        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        int thickness = (int)(40 * intensity);

        context.fill(0, 0, w, thickness, color);
        context.fill(0, h - thickness, w, h, color);
        context.fill(0, thickness, thickness, h - thickness, color);
        context.fill(w - thickness, thickness, w, h - thickness, color);
    }
}
