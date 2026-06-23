package com.pulsevisuals.mixin;

import com.pulsevisuals.optimizer.FpsOptimizer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void onShouldRender(E entity, net.minecraft.client.frustum.Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (FpsOptimizer.get().shouldCullEntity(entity)) {
            cir.setReturnValue(false);
        }
    }
}
