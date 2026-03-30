package net.elemental_wizards_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.ParticleHelper;

public class StoneFleshParticleSpawner implements CustomParticleStatusEffect.Spawner{

    public static final ParticleBatch particles = new ParticleBatch(
            "more_rpg_classes:stone_particle",
            ParticleBatch.Shape.WIDE_PIPE,
            ParticleBatch.Origin.FEET,
            null,
            0.5F,
            0.001F,
            0.1F,
            0).extent(0.5F);

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            var scaledParticles = new ParticleBatch(particles);
            scaledParticles.count *= (amplifier + 1);
            scaledParticles.max_speed *= livingEntity.getScaleFactor();
            ParticleHelper.play(world, livingEntity, scaledParticles);
        }
    }
}
