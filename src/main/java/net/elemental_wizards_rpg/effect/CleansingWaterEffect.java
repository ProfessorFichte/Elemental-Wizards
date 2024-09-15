package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.more_rpg_classes.util.CustomMethods;
import net.minecraft.server.world.ServerWorld;


public class CleansingWaterEffect extends StatusEffect {
    public CleansingWaterEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }


    @Override
    public void applyUpdateEffect(LivingEntity pLivingEntity, int pAmplifier) {
        World world = pLivingEntity.getEntityWorld();

        if (pLivingEntity.getHealth() < pLivingEntity.getMaxHealth()) {
            pLivingEntity.heal(1.0F);
        }
        if(pLivingEntity.isOnFire()){
            pLivingEntity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH,2,1);
            if(world.isClient){
                world.addParticle(MoreParticles.WATER_MIST,1,1,1,2,2,2);
                world.addParticle(ParticleTypes.LARGE_SMOKE,1,1,1,2,2,2);
            }else{
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(MoreParticles.WATER_MIST,1,1,1,4,2,2,2,2);
                    serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE,1,1,1,4,2,2,2,2);
                }
            }
            pLivingEntity.extinguish();
        }
        if(pLivingEntity.isInLava()){
            if(world.isClient){
                world.addParticle(MoreParticles.WATER_MIST,1,1,1,2,2,2);
                world.addParticle(ParticleTypes.LARGE_SMOKE,1,1,1,2,2,2);
            }else{
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(MoreParticles.WATER_MIST,1,1,1,4,2,2,2,2);
                    serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE,1,1,1,4,2,2,2,2);
                }
            }
            pLivingEntity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH,2,1);
            pLivingEntity.removeStatusEffect(Effects.CLEANSING_WATER);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i;
        if (this == Effects.CLEANSING_WATER) {
            i = 35 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onApplied(LivingEntity pLivingEntity, AttributeContainer attributes, int pAmplifier){
        if(pLivingEntity.isPlayer() && pLivingEntity.getMaxHealth() == pLivingEntity.getHealth()){
            CustomMethods.clearNegativeEffects(pLivingEntity,true);
        }
    }
}
