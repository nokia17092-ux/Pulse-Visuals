package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class DynamicCrosshair {
    private static final DynamicCrosshair INSTANCE = new DynamicCrosshair();
    private float spread = 0f;
    private boolean onTarget = false;

    public static DynamicCrosshair get() { return INSTANCE; }

    public void tick() {
        if (!PulseConfig.get().dynamicCrosshairEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        float targetSpread = 0f;
        if (PulseConfig.get().crosshairExpandOnMove && client.player.isSprinting()) {
            targetSpread = 5f;
        } else if (client.player.isSneaking()) {
            targetSpread = -2f;
        }
        spread = spread + (targetSpread - spread) * 0.15f;

        onTarget = false;
        if (PulseConfig.get().crosshairHighlightOnTarget && client.crosshairTarget != null) {
            if (client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                if (client.crosshairTarget instanceof EntityHitResult ehr) {
                    onTarget = ehr.getEntity() instanceof LivingEntity;
                }
            }
        }
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().dynamicCrosshairEnabled) return;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;

        int baseSize = 4;
        int gap = (int)(baseSize + spread);
        int thickness = 1;
        int length = 4;
        int color = onTarget ? 0xFFFF4444 : 0xFFFFFFFF;
        int shadow = 0x88000000;

        // Shadow
        context.fill(cx - gap - length, cy,     cx - gap,        cy + thickness, shadow);
        context.fill(cx + gap,          cy,     cx + gap + length, cy + thickness, shadow);
        context.fill(cx,               cy - gap - length, cx + thickness, cy - gap,        shadow);
        context.fill(cx,               cy + gap,          cx + thickness, cy + gap + length, shadow);

        // Crosshair lines
        context.fill(cx - gap - length, cy - 1, cx - gap,         cy + 1, color);
        context.fill(cx + gap,          cy - 1, cx + gap + length, cy + 1, color);
        context.fill(cx - 1, cy - gap - length, cx + 1, cy - gap,          color);
        context.fill(cx - 1, cy + gap,          cx + 1, cy + gap + length,  color);

        // Center dot on target
        if (onTarget) {
            context.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFFFF4444);
        }
    }
}
