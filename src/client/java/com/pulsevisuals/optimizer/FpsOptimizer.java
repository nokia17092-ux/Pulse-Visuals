dpackage com.pulsevisuals.optimizer;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FpsOptimizer {
    private static final FpsOptimizer INSTANCE = new FpsOptimizer();
    private final Set<Integer> culledEntities = ConcurrentHashMap.newKeySet();
    private int tickCounter = 0;
    private long lastFps = 60;
    private ParticlesMode originalParticlesMode = null;

    public static FpsOptimizer get() { return INSTANCE; }

    public void tick() {
        if (!PulseConfig.get().fpsOptimizerEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        tickCounter++;

        // Update FPS tracking
        lastFps = client.getCurrentFps();

        // Smart Particle Limiter
        if (PulseConfig.get().smartParticleLimiterEnabled) {
            updateParticleLimiter(client);
        }

        // Entity Culling — update every 4 ticks
        if (PulseConfig.get().entityCullingEnabled && tickCounter % 4 == 0) {
            updateEntityCulling(client);
        }
    }

    private void updateParticleLimiter(MinecraftClient client) {
        if (lastFps < PulseConfig.get().targetFps * 0.7) {
            if (client.options.getParticles().getValue() == ParticlesMode.ALL) {
                originalParticlesMode = ParticlesMode.ALL;
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
            }
        } else if (lastFps > PulseConfig.get().targetFps * 0.9) {
            if (originalParticlesMode != null) {
                client.options.getParticles().setValue(originalParticlesMode);
                originalParticlesMode = null;
            }
        }
    }

    private void updateEntityCulling(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        culledEntities.clear();

        Vec3d playerPos = client.player.getPos();
        Vec3d look = client.player.getRotationVec(1.0f);

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            if (!(entity instanceof LivingEntity)) continue;

            Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
            double dot = look.dotProduct(toEntity);
            double dist = entity.getPos().distanceTo(playerPos);

            // Cull if entity is far behind and far away
            if (dot < -0.5 && dist > 16) {
                culledEntities.add(entity.getId());
            }
        }
    }

    public boolean shouldCullEntity(Entity entity) {
        if (!PulseConfig.get().fpsOptimizerEnabled || !PulseConfig.get().entityCullingEnabled) return false;
        return culledEntities.contains(entity.getId());
    }

    public long getLastFps() { return lastFps; }

    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (originalParticlesMode != null) {
            client.options.getParticles().setValue(originalParticlesMode);
            originalParticlesMode = null;
        }
        culledEntities.clear();
    }
}
