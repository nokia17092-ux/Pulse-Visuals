package com.pulsevisuals;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulseVisuals implements ModInitializer {
    public static final String MOD_ID = "pulse-visuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Pulse Visuals loaded!");
    }
}
