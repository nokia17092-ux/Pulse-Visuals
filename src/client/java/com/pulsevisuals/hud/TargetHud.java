package com.pulsevisuals.hud;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TargetHud {
    private static final TargetHud INSTANCE = new TargetHud();

    public static TargetHud get() { return INSTANCE; }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().targetHudEnabled) return;
        if (client.player == null || client.world == null) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;
        if (!(hit instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int panelW = 180;
        int panelH = 60;
        int x = (screenWidth - panelW) / 2;
        int y = screenHeight - panelH - 60;

        // Background
        context.fill(x - 2, y - 2, x + panelW + 2, y + panelH + 2, 0x88000000);
        context.fill(x - 2, y - 2, x + panelW + 2, y - 1, 0xFFFF4444);

        int textY = y + 4;

        // Name
        if (PulseConfig.get().targetHudShowName) {
            String name = target.getName().getString();
            int nameColor = (target instanceof PlayerEntity) ? 0xFFFFFF : 0xAAFFAA;
            context.drawText(client.textRenderer, Text.literal(name), x + 4, textY, nameColor, true);
            textY += 12;
        }

        // HP bar
        if (PulseConfig.get().targetHudShowHealth) {
            float maxHp = (float) target.getAttributeValue(EntityAttributes.MAX_HEALTH);
            float currentHp = target.getHealth();
            float ratio = currentHp / maxHp;

            int barW = panelW - 8;
            int barH = 6;
            int barX = x + 4;
            int barY = textY;

            context.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);
            int hpColor = ratio > 0.6f ? 0xFF55FF55 : ratio > 0.3f ? 0xFFFFAA00 : 0xFFFF3333;
            context.fill(barX, barY, barX + (int)(barW * ratio), barY + barH, hpColor);

            String hpText = String.format("%.1f / %.1f", currentHp, maxHp);
            context.drawText(client.textRenderer, Text.literal(hpText), barX + 2, barY - 1, 0xFFFFFF, true);
            textY += 14;
        }

        // Armor
        if (PulseConfig.get().targetHudShowArmor && target instanceof PlayerEntity player) {
            int armor = player.getArmor();
            context.drawText(client.textRenderer, Text.literal("🛡 Armor: " + armor), x + 4, textY, 0xAAAAFF, true);
            textY += 12;
        }

        // Distance
        if (PulseConfig.get().targetHudShowDistance && client.player != null) {
            double dist = client.player.distanceTo(target);
            context.drawText(client.textRenderer, Text.literal(String.format("📏 %.1fm", dist)), x + 4, textY, 0xDDDDDD, true);
        }
    }
}
