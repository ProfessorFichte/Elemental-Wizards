package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;

public class BubbleFoamEffect extends StatusEffect {
    protected BubbleFoamEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }


    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        float range = 1.5F;
        Box radius = new Box(entity.getX() + range,
                entity.getY() + (float) range / 3,
                entity.getZ() + range,
                entity.getX() - range,
                entity.getY() - (float) range / 3,
                entity.getZ() - range);

        for(Entity entities : entity.getEntityWorld().getOtherEntities(entity, radius, EntityPredicates.VALID_LIVING_ENTITY)){
            if (entities != null) {
                if(entities instanceof LivingEntity target){
                    target.setVelocity((target.getX() - entity.getX()) /4,  (target.getY() - entity.getY()) /4, (target.getZ() - entity.getZ()) /4);
                }

            }
        }
        return true;
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
