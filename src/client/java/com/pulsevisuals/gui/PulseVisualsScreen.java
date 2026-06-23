package com.pulsevisuals.gui;

import com.pulsevisuals.config.PulseConfig;
import com.pulsevisuals.optimizer.FpsOptimizer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PulseVisualsScreen extends Screen {
    private final Screen parent;
    private int selectedTab = 0;
    private static final String[] TABS = {
        "Hit FX", "HUD", "Damage", "Crits", "Trajectory", "Trails", "Crosshair", "Low HP", "FPS"
    };
    private final List<ButtonWidget> tabButtons = new ArrayList<>();
    private final List<ButtonWidget> optionButtons = new ArrayList<>();

    public PulseVisualsScreen(Screen parent) {
        super(Text.literal("✦ Pulse Visuals"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        tabButtons.clear();
        optionButtons.clear();

        // Tab bar
        int tabW = (width - 20) / TABS.length;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            ButtonWidget tab = ButtonWidget.builder(Text.literal(TABS[i]), b -> {
                selectedTab = idx;
                rebuildOptions();
            }).dimensions(10 + i * tabW, 30, tabW - 2, 18).build();
            addDrawableChild(tab);
            tabButtons.add(tab);
        }

        // Close button
        addDrawableChild(ButtonWidget.builder(Text.literal("✕ Close"), b -> {
            PulseConfig.save();
            if (client != null) client.setScreen(parent);
        }).dimensions(width - 60, 8, 50, 14).build());

        rebuildOptions();
    }

    private void rebuildOptions() {
        for (ButtonWidget btn : optionButtons) remove(btn);
        optionButtons.clear();

        int startY = 60;
        int col1 = 20;
        int col2 = width / 2 + 10;
        int bW = width / 2 - 30;

        PulseConfig cfg = PulseConfig.get();

        switch (selectedTab) {
            case 0 -> { // Hit Particles
                addToggle("Hit Particles", cfg.hitParticlesEnabled, col1, startY, bW, v -> cfg.hitParticlesEnabled = v);
                addToggle("Crit Color Diff", true, col2, startY, bW, v -> {});
                addInfo("Normal: Golden  |  Crit: Red", col1, startY + 30);
                addInfo("Count: " + cfg.hitParticleCount + "  |  Life: " + cfg.hitParticleLifetime + "t", col1, startY + 45);
            }
            case 1 -> { // Target HUD
                addToggle("Target HUD", cfg.targetHudEnabled, col1, startY, bW, v -> cfg.targetHudEnabled = v);
                addToggle("Show Health", cfg.targetHudShowHealth, col2, startY, bW, v -> cfg.targetHudShowHealth = v);
                addToggle("Show Armor", cfg.targetHudShowArmor, col1, startY + 28, bW, v -> cfg.targetHudShowArmor = v);
                addToggle("Show Name", cfg.targetHudShowName, col2, startY + 28, bW, v -> cfg.targetHudShowName = v);
                addToggle("Show Distance", cfg.targetHudShowDistance, col1, startY + 56, bW, v -> cfg.targetHudShowDistance = v);
            }
            case 2 -> { // Damage Numbers
                addToggle("Damage Numbers", cfg.damageNumbersEnabled, col1, startY, bW, v -> cfg.damageNumbersEnabled = v);
                addInfo("Normal: Golden  |  Crit: Red  |  Heal: Green", col1, startY + 30);
                addInfo("Float upward animation with fade-out", col1, startY + 45);
            }
            case 3 -> { // Crit Effects
                addToggle("Crit Effect", cfg.critEffectEnabled, col1, startY, bW, v -> cfg.critEffectEnabled = v);
                addToggle("Screen Flash", cfg.critScreenFlash, col2, startY, bW, v -> cfg.critScreenFlash = v);
            }
            case 4 -> { // Trajectory
                addToggle("Trajectory", cfg.trajectoryEnabled, col1, startY, bW, v -> cfg.trajectoryEnabled = v);
                addInfo("Shows for: Bow, Crossbow, Trident", col1, startY + 30);
                addInfo("Accounts for gravity and air drag", col1, startY + 45);
                addInfo("Dots: " + cfg.trajectoryDots, col1, startY + 60);
            }
            case 5 -> { // Trails
                addToggle("Weapon Trail", cfg.weaponTrailEnabled, col1, startY, bW, v -> cfg.weaponTrailEnabled = v);
                addToggle("Sprint Trail", cfg.sprintTrailEnabled, col2, startY, bW, v -> cfg.sprintTrailEnabled = v);
                addToggle("Combo Counter", cfg.comboEnabled, col1, startY + 28, bW, v -> cfg.comboEnabled = v);
                addToggle("Kill Feed", cfg.killFeedEnabled, col2, startY + 28, bW, v -> cfg.killFeedEnabled = v);
                addToggle("Hit Direction", cfg.hitDirectionEnabled, col1, startY + 56, bW, v -> cfg.hitDirectionEnabled = v);
            }
            case 6 -> { // Dynamic Crosshair
                addToggle("Dynamic Crosshair", cfg.dynamicCrosshairEnabled, col1, startY, bW, v -> cfg.dynamicCrosshairEnabled = v);
                addToggle("Expand on Move", cfg.crosshairExpandOnMove, col2, startY, bW, v -> cfg.crosshairExpandOnMove = v);
                addToggle("Highlight on Target", cfg.crosshairHighlightOnTarget, col1, startY + 28, bW, v -> cfg.crosshairHighlightOnTarget = v);
                addInfo("Turns red when aiming at an entity", col1, startY + 58);
            }
            case 7 -> { // Low HP Pulse
                addToggle("Low HP Pulse", cfg.lowHpPulseEnabled, col1, startY, bW, v -> cfg.lowHpPulseEnabled = v);
                addInfo("Threshold: " + (int)(cfg.lowHpThreshold * 100) + "% HP", col1, startY + 30);
                addInfo("Red vignette pulses at edge of screen", col1, startY + 45);
            }
            case 8 -> { // FPS Optimizer
                addToggle("FPS Optimizer", cfg.fpsOptimizerEnabled, col1, startY, bW, v -> {
                    cfg.fpsOptimizerEnabled = v;
                    if (!v) FpsOptimizer.get().onDisable();
                });
                addToggle("Entity Culling", cfg.entityCullingEnabled, col2, startY, bW, v -> cfg.entityCullingEnabled = v);
                addToggle("Smart Particle Limit", cfg.smartParticleLimiterEnabled, col1, startY + 28, bW, v -> cfg.smartParticleLimiterEnabled = v);
                addToggle("Mob AI Throttle", cfg.mobAiThrottleEnabled, col2, startY + 28, bW, v -> cfg.mobAiThrottleEnabled = v);
                addToggle("Dynamic Render Dist", cfg.dynamicRenderDistanceEnabled, col1, startY + 56, bW, v -> cfg.dynamicRenderDistanceEnabled = v);
                addInfo("Current FPS: " + FpsOptimizer.get().getLastFps(), col1, startY + 86);
                addInfo("Target FPS: " + cfg.targetFps + " (particle limiter threshold)", col1, startY + 100);
            }
        }
    }

    private void addToggle(String label, boolean value, int x, int y, int w, java.util.function.Consumer<Boolean> setter) {
        String state = value ? "§a■ ON" : "§c■ OFF";
        ButtonWidget btn = ButtonWidget.builder(Text.literal(label + " " + state), b -> {
            boolean current = b.getMessage().getString().contains("ON");
            setter.accept(!current);
            String newState = !current ? "§a■ ON" : "§c■ OFF";
            b.setMessage(Text.literal(label + " " + newState));
        }).dimensions(x, y, w, 20).build();
        addDrawableChild(btn);
        optionButtons.add(btn);
    }

    private void addInfo(String text, int x, int y) {
        // Stored for rendering
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Header background
        context.fill(0, 0, width, 28, 0xFF111111);
        context.fill(0, 28, width, 30, 0xFFFF4444);

        // Tab bar background
        context.fill(0, 30, width, 52, 0xFF1A1A1A);

        // Content area
        context.fill(0, 52, width, height, 0xFF0D0D0D);

        // Header text
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("✦ PULSE VISUALS"), width / 2, 8, 0xFFFF4444);

        // Selected tab highlight
        if (selectedTab >= 0 && selectedTab < tabButtons.size()) {
            ButtonWidget sel = tabButtons.get(selectedTab);
            context.fill(sel.getX(), sel.getY(), sel.getX() + sel.getWidth(), sel.getY() + sel.getHeight(), 0x55FF4444);
        }

        // Info texts per tab
        int startY = 60;
        int col1 = 20;
        PulseConfig cfg = PulseConfig.get();
        switch (selectedTab) {
            case 0 -> {
                context.drawText(textRenderer, Text.literal("§7Normal color: §eGolden  §7Crit color: §cRed"), col1, startY + 30, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Particle count: §f" + cfg.hitParticleCount + "  §7Lifetime: §f" + cfg.hitParticleLifetime + " ticks"), col1, startY + 42, 0xFFFFFF, false);
            }
            case 4 -> {
                context.drawText(textRenderer, Text.literal("§7Works with: §fBow, Crossbow, Trident"), col1, startY + 30, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Simulates §fgravity §7and §fair drag"), col1, startY + 42, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Dots: §f" + cfg.trajectoryDots), col1, startY + 54, 0xFFFFFF, false);
            }
            case 7 -> {
                context.drawText(textRenderer, Text.literal("§7Triggers below §f" + (int)(cfg.lowHpThreshold * 100) + "% §7HP"), col1, startY + 30, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Pulsing red vignette at screen edges"), col1, startY + 42, 0xFFFFFF, false);
            }
            case 8 -> {
                context.drawText(textRenderer, Text.literal("§7Current FPS: §a" + FpsOptimizer.get().getLastFps()), col1, startY + 88, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Target FPS: §f" + cfg.targetFps + " §7(particle limit trigger)"), col1, startY + 100, 0xFFFFFF, false);
                context.drawText(textRenderer, Text.literal("§7Entity Culling skips rendering of entities behind you"), col1, startY + 114, 0xFFFFFF, false);
            }
        }

        // Footer
        context.fill(0, height - 20, width, height, 0xFF111111);
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§7Config auto-saves on close  |  Open with §fRight CTRL"), width / 2, height - 14, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 347) { // Right CTRL
            PulseConfig.save();
            if (client != null) client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void close() {
        PulseConfig.save();
        if (client != null) client.setScreen(parent);
    }
}
