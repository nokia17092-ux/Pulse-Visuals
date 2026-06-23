package com.pulsevisuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class PulseConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pulse-visuals.json");
    private static PulseConfig INSTANCE = new PulseConfig();

    // Hit Particles
    public boolean hitParticlesEnabled = true;
    public int hitParticleColor = 0xFFD700;
    public int critParticleColor = 0xFF3030;
    public int hitParticleCount = 8;
    public float hitParticleSize = 1.0f;
    public int hitParticleLifetime = 15;

    // Target HUD
    public boolean targetHudEnabled = true;
    public boolean targetHudShowHealth = true;
    public boolean targetHudShowArmor = true;
    public boolean targetHudShowName = true;
    public boolean targetHudShowDistance = true;

    // Damage Numbers
    public boolean damageNumbersEnabled = true;
    public int normalDamageColor = 0xFFD700;
    public int critDamageColor = 0xFF3030;
    public int healColor = 0x55FF55;
    public float damageNumberScale = 1.0f;

    // Critical Hit Effects
    public boolean critEffectEnabled = true;
    public boolean critScreenFlash = true;

    // Trajectory Prediction
    public boolean trajectoryEnabled = true;
    public int trajectoryColor = 0xFFFFFF;
    public int trajectoryDots = 20;

    // Weapon Trails
    public boolean weaponTrailEnabled = true;
    public int weaponTrailColor = 0xFFD700;
    public int weaponTrailCritColor = 0xFF3030;
    public int weaponTrailLength = 10;

    // Combo Counter
    public boolean comboEnabled = true;
    public int comboTimeout = 60;

    // Dynamic Crosshair
    public boolean dynamicCrosshairEnabled = true;
    public boolean crosshairExpandOnMove = true;
    public boolean crosshairHighlightOnTarget = true;

    // Low HP Pulse
    public boolean lowHpPulseEnabled = true;
    public float lowHpThreshold = 0.3f;
    public int lowHpPulseColor = 0xFF0000;

    // Sprint Trail
    public boolean sprintTrailEnabled = true;
    public int sprintTrailColor = 0x88FFFFFF;

    // Hit Direction Indicator
    public boolean hitDirectionEnabled = true;
    public int hitDirectionColor = 0xFF3030;

    // Kill Feed
    public boolean killFeedEnabled = true;

    // FPS Optimizer
    public boolean fpsOptimizerEnabled = true;
    public boolean entityCullingEnabled = true;
    public boolean smartParticleLimiterEnabled = true;
    public boolean mobAiThrottleEnabled = true;
    public boolean dynamicRenderDistanceEnabled = false;
    public int targetFps = 60;

    public static PulseConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                INSTANCE = GSON.fromJson(reader, PulseConfig.class);
                if (INSTANCE == null) INSTANCE = new PulseConfig();
            } catch (Exception e) {
                INSTANCE = new PulseConfig();
            }
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            // silently ignore
        }
    }
}
