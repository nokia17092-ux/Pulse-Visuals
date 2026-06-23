package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SprintTrailManager {
    private static final SprintTrailManager INSTANCE = new SprintTrailManager();
    private final List<TrailParticle> particles = new ArrayList<>();
    private final Random random = new Random();
    private int spawnCooldown = 0;

    public static SprintTrailManager get() { return INSTANCE; }

    public void tick() {
        if (!PulseConfig.get().sprintTrailEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.player.isSprinting()) {
            if (spawnCooldown <= 0) {
                Vec3d pos = client.player.getPos().add(
                    (random.nextDouble() - 0.5) * 0.3,
                    0.1,
                    (random.nextDouble() - 0.5) * 0.3
                );
                particles.add(new TrailParticle(pos));
                spawnCooldown = 2;
            } else {
                spawnCooldown--;
            }
        }

        Iterator<TrailParticle> it = particles.iterator();
        while (it.hasNext()) {
            TrailParticle p = it.next();
            p.age++;
            if (p.age >= 20) it.remove();
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {
        if (!PulseConfig.get().sprintTrailEnabled || particles.isEmpty()) return;
        Vec3d camPos = camera.getPos();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getTranslucentParticles());
        int baseColor = PulseConfig.get().sprintTrailColor;
        float r = ((baseColor >> 16) & 0xFF) / 255f;
        float g = ((baseColor >> 8) & 0xFF) / 255f;
        float b = (baseColor & 0xFF) / 255f;

        for (TrailParticle p : particles) {
            float alpha = (1.0f - (float) p.age / 20f) * 0.5f;
            float size = 0.08f * (1.0f - (float) p.age / 20f);

            double rx = p.pos.x - camPos.x;
            double ry = p.pos.y - camPos.y;
            double rz = p.pos.z - camPos.z;

            matrices.push();
            matrices.translate(rx, ry, rz);
            var matrix = matrices.peek().getPositionMatrix();
            vc.vertex(matrix, -size, 0, -size).color(r, g, b, alpha).next();
            vc.vertex(matrix, -size, 0,  size).color(r, g, b, alpha).next();
            vc.vertex(matrix,  size, 0,  size).color(r, g, b, alpha).next();
            vc.vertex(matrix,  size, 0, -size).color(r, g, b, alpha).next();
            matrices.pop();
        }
    }

    public static class TrailParticle {
        public final Vec3d pos;
        public int age;
        public TrailParticle(Vec3d pos) { this.pos = pos; }
    }
}
