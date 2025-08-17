package net.elemental_wizards_rpg.spell.custom_spell_impacts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class AvatarImpact implements SpellHandlers.CustomImpact {
    private RegistryEntry<Spell> spellEntry;

    @Override
    public SpellHandlers.ImpactResult onSpellImpact(
            RegistryEntry<Spell> spell,
            SpellPower.Result powerResult,
            LivingEntity caster,
            Entity target,
            SpellHelper.ImpactContext context
    ) {
        double air_power = caster.getAttributeValue(MoreSpellSchools.AIR.attributeEntry);
        double earth_power = caster.getAttributeValue(MoreSpellSchools.EARTH.attributeEntry);
        double water_power = caster.getAttributeValue(MoreSpellSchools.WATER.attributeEntry);
        String air_spell = "wind_aeroburst";
        String earth_spell = "terra_drip_circle";
        String water_spell = "aqua_explosive_bubbles";

        if(target instanceof LivingEntity){
            if (air_power >= earth_power && air_power >= water_power) {
                /// AREA IMPACT EXAMPLE
                ParticleHelper.sendBatches(caster, SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, air_spell)).release.particles);
                for(Entity entity : TargetHelper.targetsFromArea(caster,SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, air_spell)).range,SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, air_spell)).target.area, entity ->{ return entity != caster;})) {
                    SpellHelper.performImpacts(caster.getWorld(), caster, entity, caster, SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, air_spell)).get(),
                            SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, air_spell)).impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(MoreSpellSchools.AIR, caster)).position(caster.getPos()));
                    ParticleHelper.sendBatches(entity, SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, air_spell)).impacts.get(0).particles);
                }

            } else if (earth_power >= air_power && earth_power >= water_power) {
                /// SPAWN CLOUD EXAMPLE
                ParticleHelper.sendBatches(caster, SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, earth_spell)).release.particles);
                spellEntry = SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID,earth_spell)).orElse(null);
                var targetSpecificContext = context;
                SpellHelper.placeCloud(caster.getWorld(), caster, target, target.getPos(), spellEntry , targetSpecificContext);

            } else {
                /// PROJECTILE EXAMPLE
                ParticleHelper.sendBatches(caster, SpellRegistry.from(caster.getWorld()).get(Identifier.of(MOD_ID, water_spell)).release.particles);
                SpellHelper.shootProjectile(caster.getWorld(), caster, target, SpellRegistry.from(caster.getWorld()).getEntry(Identifier.of(MOD_ID, water_spell)).get(),
                        new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(MoreSpellSchools.WATER, caster)).position(caster.getPos()));
            }
        }
        return new SpellHandlers.ImpactResult(true, false);
    }
}

