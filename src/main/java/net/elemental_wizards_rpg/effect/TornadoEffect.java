package net.elemental_wizards_rpg.effect;

import net.elemental_wizards_rpg.entity.TornadoEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spell_power.api.statuseffects.SpellVulnerabilityStatusEffect;

public class TornadoEffect extends SpellVulnerabilityStatusEffect {
    protected TornadoEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1, 0, false, false, false));

        float range = 4.5F;
        Box radius = new Box(
                entity.getX() + range, entity.getY() + range / 3, entity.getZ() + range,
                entity.getX() - range, entity.getY() - range / 3, entity.getZ() - range
        );

        TornadoEntity nearestTornado = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity otherEntity : entity.getEntityWorld().getOtherEntities(entity, radius, e -> e instanceof TornadoEntity)) {
            double dist = otherEntity.squaredDistanceTo(entity);
            if (dist < minDistance) {
                minDistance = dist;
                nearestTornado = (TornadoEntity) otherEntity;
            }
        }

        if (nearestTornado != null) {
            Vec3d tornadoPos = nearestTornado.getPos();
            Vec3d entityPos = entity.getPos();

            double dx = entityPos.x - tornadoPos.x;
            double dz = entityPos.z - tornadoPos.z;

            double radiusDistance = Math.sqrt(dx * dx + dz * dz);
            if (radiusDistance == 0) radiusDistance = 0.01;

            radiusDistance = Math.max(1.0, radiusDistance - 0.1);

            double angle = Math.atan2(dz, dx);
            angle += 0.25;

            double newX = tornadoPos.x + Math.cos(angle) * radiusDistance;
            double newZ = tornadoPos.z + Math.sin(angle) * radiusDistance;

            double velocityX = (newX - entityPos.x);
            double velocityZ = (newZ - entityPos.z);
            double velocityY = 0.1 + (range - radiusDistance) * 0.03; // je näher, desto höher
            entity.setVelocity(velocityX * 1.2, velocityY, velocityZ * 1.2);
        } else {
            entity.removeStatusEffect(Effects.TORNADO.registryEntry);
        }
        return true;
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        Vec3d currentMovement = entity.getVelocity();
        entity.setVelocity(currentMovement.x, currentMovement.y + 0.65F, currentMovement.z);
        entity.velocityModified = true;
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,1,0,false,false,false));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}
