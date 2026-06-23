package com.pulsevisuals.mixin;

import com.pulsevisuals.gui.PulseVisualsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addVisualsButton(CallbackInfo ci) {
        GameMenuScreen self = (GameMenuScreen)(Object)this;
        MinecraftClient client = MinecraftClient.getInstance();
        ButtonWidget button = ButtonWidget.builder(
            Text.literal("✦ Visuals"),
            b -> client.setScreen(new PulseVisualsScreen(self))
        ).dimensions(self.width / 2 - 102, self.height / 4 + 8, 98, 20).build();
        self.addDrawableChild(button);
    }
}
