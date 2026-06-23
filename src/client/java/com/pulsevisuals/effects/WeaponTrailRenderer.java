package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;

public class WeaponTrailRenderer {
    private static final WeaponTrailRenderer INSTANCE = new WeaponTrailRenderer();
    private final Deque<TrailPoint> trail = new ArrayDeque<>();
    private boolean isCrit = false;
    private boolean isSwinging = false;
    private int swingCooldown = 0;

    public static WeaponTrailRenderer get() { return INSTANCE; }

    public void onSwing(boolean crit) {
        if (!PulseConfig.get().weaponTrailEnabled) return;
        isSwinging = true;
        isCrit = crit;
        swingCooldown = 10;
    }

    public void tick() {
        if (!PulseConfig.get().weaponTrailEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (swingCooldown > 0) {
            swingCooldown--;
            Vec3d handPos = client.player.getPos().add(0, client.player.getStandingEyeHeight() * 0.8, 0);
            Vec3d look = client.player.getRotationVec(1.0f);
            Vec3d right = look.crossProduct(new Vec3d(0, 1, 0)).normalize();
            trail.addLast(new TrailPoint(handPos.add(right.multiply(0.5))));
        } else {
            isSwinging = false;
        }

        if (trail.size() > PulseConfig.get().weaponTrailLength) {
            trail.pollFirst();
        }

        for (TrailPoint point : trail) {
            point.age++;
        }
        trail.removeIf(p -> p.age > PulseConfig.get().weaponTrailLength);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {
        if (!PulseConfig.get().weaponTrailEnabled || trail.size() < 2) return;
        Vec3d camPos = camera.getPos();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getTranslucentParticles());
        int baseColor = isCrit ? PulseConfig.get().weaponTrailCritColor : PulseConfig.get().weaponTrailColor;
        float r = ((baseColor >> 16) & 0xFF) / 255f;
        float g = ((baseColor >> 8) & 0xFF) / 255f;
        float b = (baseColor & 0xFF) / 255f;

        TrailPoint[] points = trail.toArray(new TrailPoint[0]);
        for (int i = 0; i < points.length - 1; i++) {
            float alpha = (float) i / points.length * 0.7f;
            Vec3d p1 = points[i].pos.subtract(camPos);
            Vec3d p2 = points[i + 1].pos.subtract(camPos);
            float width = 0.05f * ((float) i / points.length);

            matrices.push();
            var matrix = matrices.peek().getPositionMatrix();
            vc.vertex(matrix, (float)p1.x - width, (float)p1.y, (float)p1.z).color(r, g, b, alpha).next();
            vc.vertex(matrix, (float)p1.x + width, (float)p1.y, (float)p1.z).color(r, g, b, alpha).next();
            vc.vertex(matrix, (float)p2.x + width, (float)p2.y, (float)p2.z).color(r, g, b, alpha).next();
            vc.vertex(matrix, (float)p2.x - width, (float)p2.y, (float)p2.z).color(r, g, b, alpha).next();
            matrices.pop();
        }
    }

    public static class TrailPoint {
        public final Vec3d pos;
        public int age;
        public TrailPoint(Vec3d pos) { this.pos = pos; }
    }
}
