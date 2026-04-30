package com.anomalith.mixin.client;

import com.anomalith.client.config.SwingAnimationConfig;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract int getItemUseTime();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!SwingAnimationConfig.isEnabled()) {
            return;
        }

        // Дополнительная логика для обработки анимаций
        LivingEntity entity = (LivingEntity) (Object) this;
        
        // Если сущность использует предмет (ест, пьет), 
        // можно добавить дополнительную логику здесь
        if (this.isUsingItem() && this.getItemUseTime() > 0) {
            // Логика обработки использования предмета
        }
    }
}
