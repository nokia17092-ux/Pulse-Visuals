package com.pulsevisuals.hud;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class KillFeedManager {
    private static final KillFeedManager INSTANCE = new KillFeedManager();
    private final Deque<KillEntry> entries = new ArrayDeque<>();
    private static final int MAX_ENTRIES = 5;
    private static final int DISPLAY_TICKS = 120;

    public static KillFeedManager get() { return INSTANCE; }

    public void addEntry(String killer, String victim, String weapon) {
        if (!PulseConfig.get().killFeedEnabled) return;
        if (entries.size() >= MAX_ENTRIES) entries.pollFirst();
        entries.addLast(new KillEntry(killer, victim, weapon));
    }

    public void tick() {
        entries.removeIf(e -> {
            e.age++;
            return e.age >= DISPLAY_TICKS;
        });
    }

    public void render(DrawContext context, MinecraftClient client) {
        if (!PulseConfig.get().killFeedEnabled) return;
        int x = client.getWindow().getScaledWidth() - 200;
        int y = 10;
        for (KillEntry entry : entries) {
            float alpha = entry.age < DISPLAY_TICKS - 20
                ? 1.0f
                : 1.0f - (float)(entry.age - (DISPLAY_TICKS - 20)) / 20f;
            int a = (int)(alpha * 180);
            int textA = (int)(alpha * 255);

            context.fill(x - 2, y - 1, x + 198, y + 10, (a << 24) | 0x000000);
            String text = entry.killer + " ⚔ " + entry.victim;
            context.drawText(client.textRenderer, Text.literal(text), x, y, (textA << 24) | 0xFFFFFF, true);
            y += 13;
        }
    }

    public static class KillEntry {
        public final String killer;
        public final String victim;
        public final String weapon;
        public int age;

        public KillEntry(String killer, String victim, String weapon) {
            this.killer = killer;
            this.victim = victim;
            this.weapon = weapon;
        }
    }
}
