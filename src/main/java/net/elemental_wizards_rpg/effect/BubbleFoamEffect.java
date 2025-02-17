package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.spell_engine.utils.TargetHelper;

public class BubbleFoamEffect extends StatusEffect {
    protected BubbleFoamEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    public static boolean isProtected(Entity target, LivingEntity caster) {
        var relation = TargetHelper.getRelation(caster, target);
        switch (relation) {
            case FRIENDLY, SEMI_FRIENDLY -> {
                return true;
            }
            case NEUTRAL, MIXED, HOSTILE -> {
                return false;
            }
        }
        return false;
    }


    @Override
    public void applyUpdateEffect(LivingEntity livingEntity, int amplifier) {
        float range = 1.5F;
        Box radius = new Box(livingEntity.getX() + range,
                livingEntity.getY() + (float) range / 3,
                livingEntity.getZ() + range,
                livingEntity.getX() - range,
                livingEntity.getY() - (float) range / 3,
                livingEntity.getZ() - range);

        for(Entity entities : livingEntity.getEntityWorld().getOtherEntities(livingEntity, radius, EntityPredicates.VALID_LIVING_ENTITY)){
            if (entities != null) {
                if(entities instanceof LivingEntity target && !isProtected(target,livingEntity)){
                    target.setVelocity((target.getX() - livingEntity.getX()) /4,  (target.getY() - livingEntity.getY()) /4, (target.getZ() - livingEntity.getZ()) /4);
                }

            }
        }
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity pLivingEntity, AttributeContainer attributes, int pAmplifier){
        pLivingEntity.heal(0.5F * (pAmplifier+1));
    }
}
