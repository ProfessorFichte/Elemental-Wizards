package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.Spell.Impact.Action.Summon;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.summon.AttributeScaling;
import net.spell_engine.api.spell.summon.SummonBehaviour;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;

import java.util.ArrayList;
import java.util.List;

public class ElementalSummons {

    // spell_power:earth is a base stat so the golem scales off itself; owner-scaling lives on the SUMMON impact's attribute_scaling instead
    public static SummonedEntityConfig.Entry earthGolemDefaults() {
        var defaults = new SummonedEntityConfig.Entry();
        defaults.common = new SummonedEntityConfig.CommonAttributes(40.0, 0.3, 8.0);
        defaults.common.follow_range = 16.0;
        defaults.custom.add(new SummonedEntityConfig.CustomAttribute("spell_power:earth", 1));
        defaults.custom.add(new SummonedEntityConfig.CustomAttribute("minecraft:generic.knockback_resistance", 0.8));
        return defaults;
    }

    public static Summon earthGolem() {
        var behaviour = new SummonBehaviour();
        behaviour.lifespan.active_seconds = 30;
        behaviour.lifespan.spawn_ticks = 20;
        behaviour.lifespan.despawn_ticks = 20;

        behaviour.sounds.death = new Sound(ElementalSounds.GOLEM_DEATH.id());
        behaviour.movement.can_move = true;
        behaviour.movement.affected_by_gravity = true;
        behaviour.movement.is_pushable = false;
        behaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.ENEMIES;
        behaviour.movement.follow = new SummonBehaviour.Movement.Follow();
        behaviour.movement.follow.start_distance = 10F;
        behaviour.movement.follow.stop_distance = 3.0F;
        behaviour.movement.follow.teleport_after_distance = 32F;

        behaviour.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;
        behaviour.targeting.attack_with_owner = true;
        behaviour.targeting.revenge = true;
        // Detect out to stone throw's 20-block range instead of the 16-block follow_range cap.
        behaviour.targeting.detection_range.mode = SummonBehaviour.Targeting.DetectionRange.Mode.MAXIMUM_ACTION_RANGE;

        behaviour.sounds.ambient = new Sound(ElementalSounds.GOLEM_GROWL.id());

        var melee = new SummonBehaviour.Action.MeleeAttack();
        melee.max_range = 4.0F;
        melee.radius = 3.0F;
        melee.duration = 33;
        melee.windup = 8F / 33F;
        melee.speed = 0.55F;
        melee.swing_sound = new Sound(ElementalSounds.GOLEM_GROWL.id().toString());
        melee.impact_sound = new Sound(ElementalSounds.GOLEM_GROUND_SLAM.id().toString());

        // SpellCast.range.min/max are fractions of the spell's own range, not blocks: terra_stone_throw's range is 20, so an 8-20 block band is 0.4-1.0
        var stoneThrow = new SummonBehaviour.Action.SpellCast();
        stoneThrow.spell_id = ElementalWizardSpells.terra_stone_throw.id().toString();
        stoneThrow.range.max = 1.0F;
        stoneThrow.range.min = 0.4F;
        stoneThrow.cooldown = 20;

        // terra_earth_golem_spike_line's range is 8, so a 4-9 block band is 0.5-1.125.
        var spikeLine = new SummonBehaviour.Action.SpellCast();
        spikeLine.spell_id = ElementalWizardSpells.terra_earth_golem_spike_line.id().toString();
        spikeLine.range.max = 1.125F;
        spikeLine.range.min = 0.5F;
        spikeLine.cooldown = 60;

        // Ranged spells before melee - melee is the fallback
        behaviour.actions = List.of(
                SummonBehaviour.Action.spell(stoneThrow),
                SummonBehaviour.Action.spell(spikeLine),
                SummonBehaviour.Action.attack(melee)
        );



        var summon = new Spell.Impact.Action.Summon();
        summon.entity_type_id = "elemental_wizards_rpg:earth_golem";
        summon.behaviour = behaviour;
        summon.spawn_count = 1;
        var placement = new Spell.EntityPlacement();
        placement.apply_yaw = true;
        placement.location_offset_by_look = 2;
        placement.force_onto_ground = true;
        summon.placements = List.of(placement);


        var school = MoreSpellSchools.EARTH.id.toString();
        var scaling = new ArrayList<AttributeScaling.Entry>();
        scaling.add(scalingEntry(EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(), school, 0, 1.2));
        scaling.add(scalingEntry(EntityAttributes.GENERIC_ARMOR.getIdAsString(), school, 5, 0.075));
        scaling.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(), school, 0, 0.35));
        scaling.add(scalingEntry(school, school, 2, 0.1));
        scaling.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getIdAsString(), school, 0, 0.1));
        scaling.add(scalingEntry(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(), school, 2.5, 0.025));
        summon.attribute_scaling.entries = scaling;

        return summon;
    }

private static AttributeScaling.Entry scalingEntry(String targetAttribute, String ownerAttribute,
                                                   double base, double coefficient) {
    var entry = new AttributeScaling.Entry();
    entry.attribute_id = targetAttribute;
    entry.modifiers = List.of(new AttributeScaling.Entry.OwnerModifier(
            ownerAttribute, EntityAttributeModifier.Operation.ADD_VALUE, base, coefficient));
    return entry;
}
}
