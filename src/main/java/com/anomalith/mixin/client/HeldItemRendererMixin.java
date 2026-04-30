package com.anomalith.mixin.client;

import com.anomalith.client.config.SwingAnimationConfig;
import com.anomalith.client.render.SwingAnimationTransformer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void applySwingAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, 
                                     Hand hand, float swingProgress, ItemStack item, float equipProgress, 
                                     MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                     int light, CallbackInfo ci) {
        
        if (!SwingAnimationConfig.isEnabled()) {
            return;
        }

        Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean isRightHand = arm == Arm.RIGHT;
        
        // Получаем прогресс свинга для правильной анимации
        float actualSwingProgress = hand == Hand.MAIN_HAND 
            ? player.getHandSwingProgress(tickDelta) 
            : 0.0F;
        
        if (actualSwingProgress > 0.0F) {
            // ИСПРАВЛЕНИЕ: Применяем трансформации в правильном порядке
            // и с правильными значениями для предотвращения ухода за голову
            
            matrices.push();
            
            // 1. Компенсируем базовое смещение для правильного центрирования
            float handSide = isRightHand ? 1.0F : -1.0F;
            
            // 2. Применяем анимацию взмаха с ограничением
            float swingAmount = actualSwingProgress;
            
            // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: Ограничиваем максимальный угол поворота
            // чтобы предметы не уходили за камеру
            float maxRotation = SwingAnimationConfig.getRotationIntensity() * 0.8F; // Уменьшаем на 20%
            float rotationX = -swingAmount * maxRotation;
            
            // Ограничиваем ротацию, чтобы предмет не уходил за голову
            rotationX = Math.max(rotationX, -75.0F); // Не больше 75 градусов вниз
            
            // 3. Применяем правильное смещение при поедании
            if (player.getItemUseTime() > 0 && player.isUsingItem()) {
                // ИСПРАВЛЕНИЕ: При использовании предмета (еда) держим его перед лицом
                float useProgress = (float)player.getItemUseTime() - tickDelta + 1.0F;
                float eatAmount = useProgress / (float)item.getMaxUseTime(player);
                
                if (eatAmount < 1.0F) {
                    // Смещаем предмет ближе к камере при еде
                    matrices.translate(handSide * 0.1F, 0.3F, -0.6F);
                    
                    // Легкий наклон для реалистичности
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handSide * 15.0F));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0F));
                } else {
                    // Обычная анимация взмаха
                    SwingAnimationTransformer.applySwingTransformation(
                        matrices, 
                        actualSwingProgress, 
                        isRightHand
                    );
                }
            } else {
                // Обычная анимация взмаха без использования предмета
                SwingAnimationTransformer.applySwingTransformation(
                    matrices, 
                    actualSwingProgress, 
                    isRightHand
                );
            }
            
            matrices.pop();
        }
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"))
    private void fixArmPosition(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                int light, float equipProgress, float swingProgress, 
                                Arm arm, CallbackInfo ci) {
        if (!SwingAnimationConfig.isEnabled()) {
            return;
        }

        // Дополнительная коррекция позиции руки при анимации
        if (swingProgress > 0.0F) {
            matrices.push();
            
            // Небольшое смещение руки для плавности
            float handSide = arm == Arm.RIGHT ? 1.0F : -1.0F;
            matrices.translate(handSide * 0.02F, 0.0F, -0.05F);
            
            matrices.pop();
        }
    }
}
