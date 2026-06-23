package com.pulsevisuals.mixin;

import com.pulsevisuals.effects.*;
import com.pulsevisuals.hud.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        DamageNumberManager.get().tick();
        HitParticleManager.get().tick();
        WeaponTrailRenderer.get().tick();
        SprintTrailManager.get().tick();
        CriticalHitEffect.get().tick();
        DynamicCrosshair.get().tick();
        LowHpPulse.get().tick();
        ComboManager.get().tick();
        KillFeedManager.get().tick();
        HitDirectionIndicator.get().tick();
    }
}
