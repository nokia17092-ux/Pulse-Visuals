package com.pulsevisuals.effects;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.Vec3d;

public class TrajectoryRenderer {
    private static final TrajectoryRenderer INSTANCE = new TrajectoryRenderer();
    private static final double GRAVITY = 0.05;
    private static final double DRAG = 0.99;

    public static TrajectoryRenderer get() { return INSTANCE; }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {
        if (!PulseConfig.get().trajectoryEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        PlayerEntity player = client.player;
        Item held = player.getMainHandStack().getItem();
        boolean isProjectile = held instanceof BowItem || held instanceof CrossbowItem || held instanceof TridentItem;

        if (!isProjectile && !player.isUsingItem()) return;

        float power = 1.0f;
        if (held instanceof BowItem) {
            int useTicks = player.getItemUseTime();
            power = Math.min(useTicks / 20.0f, 1.0f);
        }

        Vec3d eyePos = player.getCameraPosVec(tickDelta);
        Vec3d look = player.getRotationVec(tickDelta);
        Vec3d vel = look.multiply(power * 3.0);

        Vec3d camPos = camera.getPos();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.LINES);

        int color = PulseConfig.get().trajectoryColor;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        Vec3d prev = eyePos;
        int dots = PulseConfig.get().trajectoryDots;

        for (int i = 0; i < dots; i++) {
            Vec3d next = prev.add(vel);
            vel = new Vec3d(vel.x * DRAG, vel.y * DRAG - GRAVITY, vel.z * DRAG);

            float alpha = 1.0f - (float) i / dots;

            Vec3d p1 = prev.subtract(camPos);
            Vec3d p2 = next.subtract(camPos);

            matrices.push();
            var matrix = matrices.peek().getPositionMatrix();
            var normal = matrices.peek().getNormalMatrix();
            vc.vertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z)
              .color(r, g, b, alpha)
              .normal(normal, 0, 1, 0)
              .next();
            vc.vertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z)
              .color(r, g, b, alpha)
              .normal(normal, 0, 1, 0)
              .next();
            matrices.pop();

            prev = next;

            if (client.world != null) {
                if (!client.world.getBlockState(net.minecraft.util.math.BlockPos.ofFloored(next)).isAir()) break;
            }
        }
    }
}
