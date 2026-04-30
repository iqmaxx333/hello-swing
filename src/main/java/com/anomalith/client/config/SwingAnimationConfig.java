package com.anomalith.client.config;

import com.anomalith.client.render.SwingAnimationType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwingAnimationConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("swinganimation.json");

    private static ConfigData config = new ConfigData();

    public static class ConfigData {
        public boolean enabled = true;
        public SwingAnimationType animationType = SwingAnimationType.SMOOTH;
        public float rotationIntensity = 1.0F;
        public float swingSpeed = 1.0F;
        public boolean applyToAllItems = true;
        public boolean fixFoodAnimation = true; // НОВОЕ: Специальное исправление для еды
        public boolean preventHeadClipping = true; // НОВОЕ: Предотвращение ухода за голову
        public float maxRotationAngle = 60.0F; // НОВОЕ: Максимальный угол поворота
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, ConfigData.class);
            } else {
                save();
            }
        } catch (IOException e) {
            System.err.println("Failed to load SwingAnimation config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            System.err.println("Failed to save SwingAnimation config: " + e.getMessage());
        }
    }

    public static boolean isEnabled() {
        return config.enabled;
    }

    public static void setEnabled(boolean enabled) {
        config.enabled = enabled;
        save();
    }

    public static SwingAnimationType getAnimationType() {
        return config.animationType;
    }

    public static void setAnimationType(SwingAnimationType type) {
        config.animationType = type;
        save();
    }

    public static float getRotationIntensity() {
        return config.rotationIntensity;
    }

    public static void setRotationIntensity(float intensity) {
        config.rotationIntensity = Math.max(0.1F, Math.min(2.0F, intensity));
        save();
    }

    public static float getSwingSpeed() {
        return config.swingSpeed;
    }

    public static void setSwingSpeed(float speed) {
        config.swingSpeed = Math.max(0.5F, Math.min(2.0F, speed));
        save();
    }

    public static boolean shouldApplyToAllItems() {
        return config.applyToAllItems;
    }

    public static void setApplyToAllItems(boolean apply) {
        config.applyToAllItems = apply;
        save();
    }

    public static boolean shouldFixFoodAnimation() {
        return config.fixFoodAnimation;
    }

    public static void setFixFoodAnimation(boolean fix) {
        config.fixFoodAnimation = fix;
        save();
    }

    public static boolean shouldPreventHeadClipping() {
        return config.preventHeadClipping;
    }

    public static void setPreventHeadClipping(boolean prevent) {
        config.preventHeadClipping = prevent;
        save();
    }

    public static float getMaxRotationAngle() {
        return config.maxRotationAngle;
    }

    public static void setMaxRotationAngle(float angle) {
        config.maxRotationAngle = Math.max(30.0F, Math.min(90.0F, angle));
        save();
    }
}
