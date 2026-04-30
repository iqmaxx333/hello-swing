package com.anomalith;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Swinganimation implements ModInitializer {
    public static final String MOD_ID = "swinganimation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("SwingAnimation mod initialized! (Fixed version)");
    }
}
