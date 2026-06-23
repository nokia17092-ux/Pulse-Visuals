package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CriticalHitEffect {
    private static final CriticalHitEffect INSTANCE = new CriticalHitEffect();
    private int flashTimer = 0;
    private static final int FLASH_DURATION = 12;

    public static CriticalHitEffect get() { return INSTANCE; }

    public void trigger() {
        if (!PulseConfig.get().critEffectEnabled) return;
        flashTimer = FLASH_DURATION;
    }

    public void tick() {
        if (flashTimer > 0) flashTimer--;
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().critEffectEnabled || !PulseConfig.get().critScreenFlash) return;
        if (flashTimer <= 0) return;

        float alpha = (float) flashTimer / FLASH_DURATION * 0.35f;
        int a = (int)(alpha * 255);
        int color = (a << 24) | 0xFF2200;

        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();

        int thickness = 20;
        context.fill(0, 0, w, thickness, color);
        context.fill(0, h - thickness, w, h, color);
        context.fill(0, 0, thickness, h, color);
        context.fill(w - thickness, 0, w, h, color);
    }
}
