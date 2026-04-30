package com.anomalith;

import com.anomalith.client.config.SwingAnimationConfig;
import net.fabricmc.api.ClientModInitializer;

public class SwinganimationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Загружаем конфигурацию
        SwingAnimationConfig.load();
        
        Swinganimation.LOGGER.info("SwingAnimation client initialized!");
        Swinganimation.LOGGER.info("Food animation fix: {}", SwingAnimationConfig.shouldFixFoodAnimation());
        Swinganimation.LOGGER.info("Head clipping prevention: {}", SwingAnimationConfig.shouldPreventHeadClipping());
    }
}
