package net.elemental_wizards_rpg.spell.custom_spell_impacts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellExecution;
import net.spell_engine.internals.delivery.CloudPlacer;
import net.spell_engine.internals.delivery.ProjectileLauncher;
import net.spell_engine.internals.impact.SpellImpacts;
import net.spell_power.api.SpellPower;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class AvatarImpact implements SpellHandlers.CustomImpact {
    @Override
    public SpellHandlers.ImpactResult onSpellImpact(
            RegistryEntry<Spell> spell,
            SpellPower.Result powerResult,
            LivingEntity caster,
            Entity target,
            SpellExecution.ImpactContext context
    ) {
        double air_power = caster.getAttributeValue(MoreSpellSchools.AIR.attributeEntry);
        double earth_power = caster.getAttributeValue(MoreSpellSchools.EARTH.attributeEntry);
        double water_power = caster.getAttributeValue(MoreSpellSchools.WATER.attributeEntry);
        RegistryEntry<Spell> air_spell = SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, "avatar_passives/air_draft")).get();
        RegistryEntry<Spell> earth_spell = SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, "avatar_passives/earth_stoning")).get();
        RegistryEntry<Spell> water_spell = SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, "avatar_passives/water_undercurrent")).get();
        if(target instanceof LivingEntity){
            if (air_power >= earth_power && air_power >= water_power) {
                SpellImpacts.performImpacts(caster.getWorld(), caster, target, target, air_spell,
                        air_spell.value().impacts, new SpellExecution.ImpactContext().power(SpellPower.getSpellPower(MoreSpellSchools.AIR, caster)).position(target.getPos()));
            } else if (earth_power >= air_power && earth_power >= water_power) {
                ProjectileLauncher.fallProjectile(caster.getWorld(),caster,target,target.getPos(),earth_spell,
                        new SpellExecution.ImpactContext().power(SpellPower.getSpellPower(MoreSpellSchools.EARTH, caster)).position(caster.getPos()));
            } else {
                CloudPlacer.placeCloud(caster.getWorld(), caster, caster, caster.getPos(), water_spell, context);
            }
        }
        return new SpellHandlers.ImpactResult(true, false);
    }
}

