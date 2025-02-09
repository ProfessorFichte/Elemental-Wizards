package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.more_rpg_classes.effect.MRPGCEffects;

import static net.elemental_wizards_rpg.ElementalMod.effectsConfig;

public class TremblingEffect extends StatusEffect {
    protected TremblingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int pAmplifier) {
        if(pAmplifier == 5){
            entity.addStatusEffect(new StatusEffectInstance(MRPGCEffects.STUNNED,effectsConfig.value.trembling_stun_apply_duration * 10));
            entity.removeStatusEffect(Effects.TREMBLING);
        }
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
