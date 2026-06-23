package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class HitParticleManager {
    private static final HitParticleManager INSTANCE = new HitParticleManager();
    private final List<HitParticle> particles = new ArrayList<>();
    private final Random random = new Random();

    public static HitParticleManager get() { return INSTANCE; }

    public void spawnHitParticles(Vec3d pos, boolean isCrit) {
        if (!PulseConfig.get().hitParticlesEnabled) return;
        PulseConfig cfg = PulseConfig.get();
        int count = cfg.hitParticleCount + (isCrit ? 4 : 0);
        int color = isCrit ? cfg.critParticleColor : cfg.hitParticleColor;
        for (int i = 0; i < count; i++) {
            Vec3d vel = new Vec3d(
                (random.nextDouble() - 0.5) * 0.3,
                random.nextDouble() * 0.25,
                (random.nextDouble() - 0.5) * 0.3
            );
            particles.add(new HitParticle(pos, vel, color, cfg.hitParticleSize, cfg.hitParticleLifetime));
        }
    }

    public void tick() {
        Iterator<HitParticle> it = particles.iterator();
        while (it.hasNext()) {
            HitParticle p = it.next();
            p.tick();
            if (p.isDead()) it.remove();
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {
        if (!PulseConfig.get().hitParticlesEnabled || particles.isEmpty()) return;
        Vec3d camPos = camera.getPos();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getTranslucentParticles());

        for (HitParticle p : particles) {
            float alpha = p.getAlpha();
            if (alpha <= 0) continue;
            float r = ((p.color >> 16) & 0xFF) / 255f;
            float g = ((p.color >> 8) & 0xFF) / 255f;
            float b = (p.color & 0xFF) / 255f;
            float size = p.size * 0.05f;

            double rx = p.pos.x - camPos.x;
            double ry = p.pos.y - camPos.y;
            double rz = p.pos.z - camPos.z;

            matrices.push();
            matrices.translate(rx, ry, rz);
            var matrix = matrices.peek().getPositionMatrix();

            vc.vertex(matrix, -size, -size, 0).color(r, g, b, alpha).next();
            vc.vertex(matrix, -size,  size, 0).color(r, g, b, alpha).next();
            vc.vertex(matrix,  size,  size, 0).color(r, g, b, alpha).next();
            vc.vertex(matrix,  size, -size, 0).color(r, g, b, alpha).next();
            matrices.pop();
        }
    }

    public static class HitParticle {
        public Vec3d pos;
        public Vec3d velocity;
        public final int color;
        public final float size;
        public final int maxAge;
        public int age;
        private static final float GRAVITY = 0.02f;

        public HitParticle(Vec3d pos, Vec3d velocity, int color, float size, int maxAge) {
            this.pos = pos;
            this.velocity = velocity;
            this.color = color;
            this.size = size;
            this.maxAge = maxAge;
        }

        public void tick() {
            age++;
            velocity = velocity.subtract(0, GRAVITY, 0).multiply(0.88);
            pos = pos.add(velocity);
        }

        public boolean isDead() { return age >= maxAge; }

        public float getAlpha() {
            if (age < maxAge / 2) return 1.0f;
            return 1.0f - (float)(age - maxAge / 2) / (maxAge / 2f);
        }
    }
}
