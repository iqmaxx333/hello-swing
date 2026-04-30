package com.anomalith.client.render;

import com.anomalith.client.config.SwingAnimationConfig;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SwingAnimationTransformer {

    /**
     * Применяет трансформацию анимации взмаха к матрице
     * ИСПРАВЛЕНО: Предметы больше не уходят за голову
     */
    public static void applySwingTransformation(MatrixStack matrices, float swingProgress, boolean isRightHand) {
        if (swingProgress <= 0.0F) {
            return;
        }

        SwingAnimationType animationType = SwingAnimationConfig.getAnimationType();
        float intensity = SwingAnimationConfig.getRotationIntensity();
        float speed = SwingAnimationConfig.getSwingSpeed();
        
        // Нормализуем прогресс с учетом скорости
        float normalizedProgress = swingProgress * speed;
        normalizedProgress = MathHelper.clamp(normalizedProgress, 0.0F, 1.0F);
        
        // Применяем easing для плавности
        float easedProgress = easeInOutQuad(normalizedProgress);
        
        float handMultiplier = isRightHand ? 1.0F : -1.0F;
        
        switch (animationType) {
            case CLASSIC -> applyClassicSwing(matrices, easedProgress, handMultiplier, intensity);
            case SMOOTH -> applySmoothSwing(matrices, easedProgress, handMultiplier, intensity);
            case DYNAMIC -> applyDynamicSwing(matrices, easedProgress, handMultiplier, intensity);
            case REALISTIC -> applyRealisticSwing(matrices, easedProgress, handMultiplier, intensity);
        }
    }

    /**
     * Классический стиль - простой взмах
     */
    private static void applyClassicSwing(MatrixStack matrices, float progress, float handMultiplier, float intensity) {
        // ИСПРАВЛЕНО: Ограничиваем максимальную ротацию
        float rotationAmount = progress * intensity * 60.0F; // Макс 60 градусов вместо 90
        rotationAmount = Math.min(rotationAmount, 60.0F);
        
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-rotationAmount));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handMultiplier * rotationAmount * 0.3F));
    }

    /**
     * Плавный стиль - с дополнительным смещением
     */
    private static void applySmoothSwing(MatrixStack matrices, float progress, float handMultiplier, float intensity) {
        // Вычисляем синусоидальное движение для плавности
        float wave = MathHelper.sin(progress * (float)Math.PI);
        
        // ИСПРАВЛЕНО: Уменьшаем амплитуду движения
        float rotationX = -wave * intensity * 50.0F; // Макс 50 градусов
        float rotationY = handMultiplier * wave * intensity * 25.0F;
        float rotationZ = handMultiplier * wave * intensity * 10.0F;
        
        // Ограничиваем ротацию
        rotationX = MathHelper.clamp(rotationX, -50.0F, 10.0F);
        
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ));
        
        // Небольшое смещение для глубины
        matrices.translate(0.0F, wave * 0.05F, -wave * 0.1F);
    }

    /**
     * Динамический стиль - с ускорением
     */
    private static void applyDynamicSwing(MatrixStack matrices, float progress, float handMultiplier, float intensity) {
        // Создаем эффект ускорения в начале и замедления в конце
        float accelerated = progress < 0.5F 
            ? 2.0F * progress * progress 
            : 1.0F - 2.0F * (1.0F - progress) * (1.0F - progress);
        
        // ИСПРАВЛЕНО: Контролируем максимальную ротацию
        float rotationX = -accelerated * intensity * 55.0F;
        float rotationY = handMultiplier * accelerated * intensity * 30.0F;
        
        rotationX = MathHelper.clamp(rotationX, -55.0F, 5.0F);
        
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        
        // Динамическое смещение
        float translateZ = -accelerated * 0.15F;
        matrices.translate(0.0F, 0.0F, translateZ);
    }

    /**
     * Реалистичный стиль - имитация настоящего взмаха
     */
    private static void applyRealisticSwing(MatrixStack matrices, float progress, float handMultiplier, float intensity) {
        // Сложная кривая для реалистичности
        float phase1 = progress < 0.3F ? progress / 0.3F : 1.0F;
        float phase2 = progress > 0.7F ? (progress - 0.7F) / 0.3F : 0.0F;
        
        // ИСПРАВЛЕНО: Безопасные углы
        float rotationX = -(phase1 * 0.7F + phase2 * 0.3F) * intensity * 45.0F;
        float rotationY = handMultiplier * (phase1 - phase2 * 0.5F) * intensity * 20.0F;
        float rotationZ = handMultiplier * MathHelper.sin(progress * (float)Math.PI) * intensity * 8.0F;
        
        rotationX = MathHelper.clamp(rotationX, -45.0F, 5.0F);
        
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ));
        
        // Тонкое смещение для реалистичности
        float wave = MathHelper.sin(progress * (float)Math.PI);
        matrices.translate(handMultiplier * wave * 0.03F, wave * 0.02F, -wave * 0.08F);
    }

    /**
     * Easing функция для плавной анимации
     */
    private static float easeInOutQuad(float t) {
        return t < 0.5F ? 2.0F * t * t : 1.0F - 2.0F * (1.0F - t) * (1.0F - t);
    }

    /**
     * Дополнительная функция для обработки специальных случаев (еда, питье и т.д.)
     */
    public static void applyItemUseTransformation(MatrixStack matrices, float useProgress, 
                                                  boolean isRightHand, boolean isFood) {
        if (useProgress <= 0.0F) {
            return;
        }

        float handMultiplier = isRightHand ? 1.0F : -1.0F;
        
        if (isFood) {
            // ИСПРАВЛЕНИЕ: Специальная трансформация для еды
            // Держим предмет перед камерой, не даем ему уйти за голову
            
            // Плавное приближение к лицу
            float eatProgress = MathHelper.clamp(useProgress, 0.0F, 1.0F);
            float wave = MathHelper.sin(eatProgress * (float)Math.PI * 4.0F) * 0.1F;
            
            // Смещаем к центру и немного вперед
            matrices.translate(
                -handMultiplier * 0.2F * eatProgress, // К центру
                0.2F + wave, // Немного вверх с колебанием
                -0.5F // Перед камерой
            );
            
            // Поворачиваем к лицу
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handMultiplier * 20.0F * eatProgress));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15.0F));
            
        } else {
            // Для других предметов (лук, щит и т.д.)
            matrices.translate(0.0F, useProgress * 0.1F, -useProgress * 0.2F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-useProgress * 20.0F));
        }
    }
}
