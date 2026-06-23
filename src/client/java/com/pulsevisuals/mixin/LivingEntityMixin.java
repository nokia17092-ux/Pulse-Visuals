package com.pulsevisuals.mixin;

import com.pulsevisuals.effects.CriticalHitEffect;
import com.pulsevisuals.effects.HitParticleManager;
import com.pulsevisuals.hud.DamageNumberManager;
import com.pulsevisuals.hud.HitDirectionIndicator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        boolean isCrit = source.getAttacker() instanceof PlayerEntity attacker
            && attacker == client.player
            && attacker.fallDistance > 0
            && !attacker.isOnGround()
            && !attacker.isClimbing()
            && !attacker.isInWater();

        // Damage numbers on target entities
        if (source.getAttacker() == client.player) {
            DamageNumberManager.get().addDamageNumber(self, amount, isCrit, false);
            HitParticleManager.get().spawnHitParticles(self.getPos().add(0, self.getHeight() / 2.0, 0), isCrit);
            if (isCrit) CriticalHitEffect.get().trigger();
        }

        // Hit direction on player
        if (self == client.player && source.getAttacker() != null) {
            HitDirectionIndicator.get().onDamageReceived(source.getAttacker());
        }

        // Healing detection
        if (amount < 0 && self == client.player) {
            DamageNumberManager.get().addDamageNumber(self, -amount, false, true);
        }
    }
}
