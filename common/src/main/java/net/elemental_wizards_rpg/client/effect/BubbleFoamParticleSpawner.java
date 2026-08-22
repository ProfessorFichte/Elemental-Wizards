package net.elemental_wizards_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;

/// Bubbles boiling off an entity under Bubble Foam.
///
/// V1: `ParticleBatch("more_rpg_classes:bubble", SPHERE, CENTER, null, 1, 0.001F, 0.1F, 0)`.
/// `Origin.CENTER` is the V2 default (`vertical_origin 0.5`), so it is simply omitted;
/// the id becomes the [MoreParticles#BUBBLE] entry constant so the appearance defaults
/// registered with it apply.
public class BubbleFoamParticleSpawner implements CustomParticleStatusEffect.Spawner {

    public static final ParticleGroup particles = ParticleGroupBuilder.of(MoreParticles.BUBBLE)
            .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                    .count(1F).speed(0.001F, 0.1F));

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            var scaledParticles = particles.copy();
            scaledParticles.batch.count *= (amplifier + 1);
            // V1 scaled only the upper speed bound with the entity's size.
            scaledParticles.batch.max_speed *= livingEntity.getScaleFactor();
            ParticleHelper.play(world, livingEntity, scaledParticles);
        }
    }
}
