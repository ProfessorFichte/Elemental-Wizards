package net.elemental_wizards_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.fx.ParticleHelper;

/// Stone flaking off an entity under Stone Flesh.
///
/// V1: `ParticleBatch("more_rpg_classes:stone_particle", WIDE_PIPE, FEET, null,
/// 0.5F, 0.001F, 0.1F, 0).extent(0.5F)`.
/// - `WIDE_PIPE` was "`PIPE` at double the radius" — now `PIPE` plus `widthFactor(2)`.
/// - `Origin.FEET` is `height * 0.1`, not `0` — [Batches#FEET] carries that exact value.
/// - `extent` is well under V1's `EXTENT_TRESHOLD`, so the entity width still applies.
/// - the id becomes the [MoreParticles#STONE_PARTICLE] entry constant so the appearance
///   defaults registered with it apply.
///
/// The fractional count is the one place the two engines disagree, so it is resolved
/// per spawn in [#spawnParticles] rather than baked into the batch.
public class StoneFleshParticleSpawner implements CustomParticleStatusEffect.Spawner {

    public static final ParticleGroup particles = ParticleGroupBuilder.of(MoreParticles.STONE_PARTICLE)
            .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                    .count(1F).chance(0.5F).speed(0.001F, 0.1F)
                    .verticalOrigin(Batches.FEET).extent(0.5F));

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            var scaledParticles = particles.copy();
            // V1 scaled the authored `0.5` count by the amplifier, and read a count below
            // `1` as the probability of emitting a single particle. V2 reads a sub-`1`
            // count as an emission *period* instead, which would turn "half the ticks"
            // into a deterministic every-other-tick — so the fraction is carried by
            // `chance` and the count only ever holds the whole part.
            float v1Count = 0.5F * (amplifier + 1);
            if (v1Count < 1F) {
                scaledParticles.batch.count(1F).chance(v1Count);
            } else {
                // At or above 1 both engines run the same `i < count` float loop, so a
                // count of e.g. `1.5` emits two particles in either.
                scaledParticles.batch.count(v1Count).chance(1F);
            }
            // V1 scaled only the upper speed bound with the entity's size.
            scaledParticles.batch.max_speed *= livingEntity.getScaleFactor();
            ParticleHelper.play(world, livingEntity, scaledParticles);
        }
    }
}
