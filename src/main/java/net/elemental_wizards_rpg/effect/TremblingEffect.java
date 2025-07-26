package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.effect.SpellEngineEffects;

import static net.elemental_wizards_rpg.ElementalMod.effectsConfig;

public class TremblingEffect extends StatusEffect {
    protected TremblingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (entity.isLiving()){
            double minx = -1.0;
            double maxx = 1.0;
            double minz = -1.0;
            double maxz = 1.0;
            double randx = minx + Math.random() * (maxx - minx);
            double randz = minz + Math.random() * (maxz - minz);
            if(entity.isOnGround()){
                Vec3d currentMovement = entity.getVelocity();
                entity.setVelocity(currentMovement.x + randx, currentMovement.y + 0.1, currentMovement.z +randz);
                entity.velocityModified = true;
            }

        }
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(amplifier == 5){
            entity.addStatusEffect(new StatusEffectInstance(SpellEngineEffects.STUN.entry,effectsConfig.value.trembling_stun_apply_duration * 10));
            entity.removeStatusEffect(Effects.TREMBLING.registryEntry);
        }
        return true;
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
