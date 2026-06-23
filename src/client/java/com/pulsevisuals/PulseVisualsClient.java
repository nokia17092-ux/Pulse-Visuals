package com.pulsevisuals;

import com.pulsevisuals.config.PulseConfig;
import com.pulsevisuals.effects.*;
import com.pulsevisuals.gui.PulseVisualsScreen;
import com.pulsevisuals.hud.*;
import com.pulsevisuals.optimizer.FpsOptimizer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PulseVisualsClient implements ClientModInitializer {

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        PulseConfig.load();

        // Key binding: Right CTRL
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.pulse_visuals.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            "key.categories.pulse_visuals"
        ));

        // Client tick — key check + FPS optimizer
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new PulseVisualsScreen(null));
                }
            }
            FpsOptimizer.get().tick();
        });

        // HUD rendering
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            var client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            TargetHud.get().render(context, client);
            CriticalHitEffect.get().render(context, client);
            LowHpPulse.get().render(context, client);
            HitDirectionIndicator.get().render(context, client);
            ComboManager.get().render(context, client);
            KillFeedManager.get().render(context, client);
            DynamicCrosshair.get().render(context, client);
            renderDamageNumbersHud(context, client);
        });

        // World-space rendering (3D)
        WorldRenderEvents.AFTER_ENTITIES.register(this::onWorldRender);

        PulseVisuals.LOGGER.info("Pulse Visuals Client initialized.");
    }

    private void renderDamageNumbersHud(net.minecraft.client.gui.DrawContext context, net.minecraft.client.MinecraftClient client) {
        if (!PulseConfig.get().damageNumbersEnabled) return;
        if (client.world == null || client.player == null) return;

        var camera = client.gameRenderer.getCamera();
        var projMat = client.gameRenderer.getBasicProjectionMatrix(client.options.getFov().getValue());

        for (var num : DamageNumberManager.get().getNumbers()) {
            var pos = num.pos;
            var camPos = camera.getPos();

            // Simple billboard: project 3D -> 2D
            double dx = pos.x - camPos.x;
            double dy = pos.y - camPos.y;
            double dz = pos.z - camPos.z;

            var rot = camera.getRotation();
            // Transform to camera space using yaw/pitch
            float yaw = (float) Math.toRadians(camera.getYaw());
            float pitch = (float) Math.toRadians(camera.getPitch());

            double cx = dx * Math.cos(yaw) + dz * Math.sin(yaw);
            double cy = dy * Math.cos(pitch) - (dx * Math.sin(yaw) - dz * Math.cos(yaw)) * Math.sin(pitch);
            double cz = -(-dx * Math.sin(yaw) + dz * Math.cos(yaw)) * Math.cos(pitch) - dy * Math.sin(pitch);

            if (cz >= 0) continue; // Behind camera

            float fov = (float) Math.toRadians(client.options.getFov().getValue());
            float aspect = (float) client.getWindow().getScaledWidth() / client.getWindow().getScaledHeight();
            float projX = (float)(cx / (-cz * Math.tan(fov / 2f)));
            float projY = (float)(cy / (-cz * Math.tan(fov / 2f) / aspect));

            int screenX = (int)((projX + 1f) / 2f * client.getWindow().getScaledWidth());
            int screenY = (int)((1f - (projY + 1f) / 2f) * client.getWindow().getScaledHeight());

            float alpha = num.getAlpha();
            int a = (int)(alpha * 255);
            int color = (a << 24) | (num.color & 0x00FFFFFF);

            String text = num.getText();
            float scale = num.getScale();

            context.getMatrices().push();
            context.getMatrices().translate(screenX, screenY, 0);
            context.getMatrices().scale(scale, scale, 1f);
            context.drawCenteredTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                net.minecraft.text.Text.literal(text),
                0, 0, color
            );
            context.getMatrices().pop();
        }
    }

    private void onWorldRender(WorldRenderContext ctx) {
        var matrices = ctx.matrixStack();
        var vertexConsumers = ctx.consumers();
        var camera = ctx.camera();
        float tickDelta = ctx.tickCounter().getTickDelta(true);

        if (matrices == null || vertexConsumers == null) return;

        HitParticleManager.get().render(matrices, vertexConsumers, camera, tickDelta);
        WeaponTrailRenderer.get().render(matrices, vertexConsumers, camera, tickDelta);
        SprintTrailManager.get().render(matrices, vertexConsumers, camera, tickDelta);
        TrajectoryRenderer.get().render(matrices, vertexConsumers, camera, tickDelta);
    }
}
