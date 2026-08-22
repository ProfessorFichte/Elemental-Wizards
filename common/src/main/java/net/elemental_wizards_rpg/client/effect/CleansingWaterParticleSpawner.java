package net.elemental_wizards_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.fx.ParticleHelper;

/// Water splashing around the feet of an entity under Cleansing Water.
///
/// V1: `ParticleBatch("more_rpg_classes:splash", SPHERE, FEET, null, 10, 0.001F, 0.1F, 0)`.
/// `Origin.FEET` is `height * 0.1`, not `0` — [Batches#FEET] carries that exact value.
/// The id becomes the [MoreParticles#SPLASH] entry constant so the appearance defaults
/// registered with it apply.
public class CleansingWaterParticleSpawner implements CustomParticleStatusEffect.Spawner {

    public static final ParticleGroup particles = ParticleGroupBuilder.of(MoreParticles.SPLASH)
            .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                    .count(10F).speed(0.001F, 0.1F)
                    .verticalOrigin(Batches.FEET));

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
