package com.pulsevisuals.hud;

import com.pulsevisuals.config.PulseConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DamageNumberManager {
    private static final DamageNumberManager INSTANCE = new DamageNumberManager();
    private final List<DamageNumber> numbers = new ArrayList<>();
    private final Random random = new Random();

    public static DamageNumberManager get() {
        return INSTANCE;
    }

    public void addDamageNumber(Entity entity, float amount, boolean isCrit, boolean isHeal) {
        if (!PulseConfig.get().damageNumbersEnabled) return;
        Vec3d pos = entity.getPos().add(
            (random.nextDouble() - 0.5) * 0.6,
            entity.getHeight() + 0.3 + (random.nextDouble() * 0.4),
            (random.nextDouble() - 0.5) * 0.6
        );
        int color;
        if (isHeal) color = PulseConfig.get().healColor;
        else if (isCrit) color = PulseConfig.get().critDamageColor;
        else color = PulseConfig.get().normalDamageColor;
        numbers.add(new DamageNumber(pos, amount, isCrit, isHeal, color));
    }

    public void tick() {
        Iterator<DamageNumber> it = numbers.iterator();
        while (it.hasNext()) {
            DamageNumber n = it.next();
            n.tick();
            if (n.isDead()) it.remove();
        }
    }

    public List<DamageNumber> getNumbers() {
        return numbers;
    }

    public static class DamageNumber {
        public Vec3d pos;
        public final float amount;
        public final boolean isCrit;
        public final boolean isHeal;
        public final int color;
        public int age;
        public static final int MAX_AGE = 40;
        private static final float RISE_SPEED = 0.04f;

        public DamageNumber(Vec3d pos, float amount, boolean isCrit, boolean isHeal, int color) {
            this.pos = pos;
            this.amount = amount;
            this.isCrit = isCrit;
            this.isHeal = isHeal;
            this.color = color;
            this.age = 0;
        }

        public void tick() {
            age++;
            pos = pos.add(0, RISE_SPEED, 0);
        }

        public boolean isDead() {
            return age >= MAX_AGE;
        }

        public float getAlpha() {
            if (age < 20) return 1.0f;
            return 1.0f - (float)(age - 20) / 20f;
        }

        public float getScale() {
            if (isCrit && age < 5) return 1.5f - age * 0.1f;
            return PulseConfig.get().damageNumberScale;
        }

        public String getText() {
            if (isHeal) return "+" + String.format("%.1f", amount);
            return "-" + String.format("%.1f", amount);
        }
    }
}
