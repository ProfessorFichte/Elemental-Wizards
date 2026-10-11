package net.elemental_wizards_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import com.mrpg_lib.compat.spell_engine.particle.MoreSpellParticles;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.fx.ParticleHelper;

public class CleansingWaterParticleSpawner implements CustomParticleStatusEffect.Spawner {

    public static final ParticleGroup particles = ParticleGroupBuilder.of(MoreSpellParticles.SPLASH)
            .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                    .count(10F).speed(0.001F, 0.1F)
                    .verticalOrigin(Batches.FEET));

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            var scaledParticles = particles.copy();
            scaledParticles.batch.count *= (amplifier + 1);
            scaledParticles.batch.max_speed *= livingEntity.getScaleFactor();
            ParticleHelper.play(world, livingEntity, scaledParticles);
        }
    }
}
