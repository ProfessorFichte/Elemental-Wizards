package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import net.spell_power.api.statuseffects.SpellVulnerabilityStatusEffect;

public class UpdraftEffect extends SpellVulnerabilityStatusEffect {
    protected UpdraftEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.isOnGround()) {
            Vec3d currentMovement = entity.getVelocity();
            entity.setVelocity(currentMovement.x, currentMovement.y + 0.075F, currentMovement.z);
            entity.velocityModified = true;
        }
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (!entity.isOnGround()) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 15, 0, false, false, false));
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
