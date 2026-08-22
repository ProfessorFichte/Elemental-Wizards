package net.elemental_wizards_rpg.entity.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellExecution;
import net.spell_engine.internals.impact.SpellImpacts;
import net.spell_power.api.SpellPower;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PeriodicAreaImpact {
    private PeriodicAreaImpact() {}

    // Null fixedPosition positions each impact at its own target; otherwise all targets share the given position
    public static void apply(World world, LivingEntity owner, Entity source, Box area,
                              Identifier helperImpactId, Predicate<LivingEntity> filter,
                              Vec3d fixedPosition, boolean additionalTargetLookup,
                              Consumer<LivingEntity> onApplied) {
        RegistryEntry<Spell> spellImpact = SpellRegistry.from(world).getEntry(helperImpactId).orElse(null);
        if (spellImpact == null) return;

        var power = SpellPower.getSpellPower(spellImpact.value().school, owner);

        for (Entity entity : world.getOtherEntities(source, area)) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!filter.test(living)) continue;

            var position = fixedPosition != null ? fixedPosition : living.getPos();
            SpellImpacts.performImpacts(world, owner, living, owner, spellImpact,
                    spellImpact.value().impacts,
                    new SpellExecution.ImpactContext().power(power).position(position),
                    additionalTargetLookup, null);

            if (onApplied != null) onApplied.accept(living);
        }
    }
}
