package com.pulsevisuals.hud;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HitDirectionIndicator {
    private static final HitDirectionIndicator INSTANCE = new HitDirectionIndicator();
    private final List<DamageIndicator> indicators = new ArrayList<>();

    public static HitDirectionIndicator get() { return INSTANCE; }

    public void onDamageReceived(Entity source) {
        if (!PulseConfig.get().hitDirectionEnabled || source == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        Vec3d dir = source.getPos().subtract(client.player.getPos()).normalize();
        indicators.add(new DamageIndicator(dir));
    }

    public void tick() {
        Iterator<DamageIndicator> it = indicators.iterator();
        while (it.hasNext()) {
            DamageIndicator ind = it.next();
            ind.age++;
            if (ind.age >= 40) it.remove();
        }
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().hitDirectionEnabled || client.player == null) return;
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        int radius = 60;
        int arcSize = 30;

        for (DamageIndicator ind : indicators) {
            float alpha = ind.age < 20 ? 1.0f : 1.0f - (float)(ind.age - 20) / 20f;
            int a = (int)(alpha * 200);

            float playerYaw = client.player.getYaw();
            float yaw = (float) Math.toDegrees(Math.atan2(ind.direction.x, ind.direction.z));
            float relative = yaw - playerYaw;

            double rad = Math.toRadians(relative - 90);
            int px = (int)(cx + radius * Math.cos(rad));
            int py = (int)(cy + radius * Math.sin(rad));

            int color = (a << 24) | (PulseConfig.get().hitDirectionColor & 0x00FFFFFF);
            int size = 6;
            context.fill(px - size/2, py - size/2, px + size/2, py + size/2, color);
        }
    }

    public static class DamageIndicator {
        public final Vec3d direction;
        public int age;

        public DamageIndicator(Vec3d direction) {
            this.direction = direction;
            this.age = 0;
        }
    }
}
