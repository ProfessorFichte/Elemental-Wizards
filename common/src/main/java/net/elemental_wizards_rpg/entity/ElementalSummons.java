package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.Spell.Impact.Action.Summon;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.summon.AttributeScaling;
import net.spell_engine.api.spell.summon.SummonBehaviour;
import net.spell_power.api.SpellSchool;

import java.util.ArrayList;
import java.util.List;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalSummons {

    public static Summon earthGolem() {
        var behaviour = new SummonBehaviour();
        behaviour.lifespan.spawn_ticks = 10;
        behaviour.lifespan.active_seconds = 15;
        behaviour.lifespan.despawn_ticks = 10;

        behaviour.movement.can_move = true;
        behaviour.movement.affected_by_gravity = true;
        behaviour.movement.is_pushable = false;
        behaviour.movement.collision = SummonBehaviour.Movement.CollisionMode.ENEMIES;
        behaviour.movement.follow = new SummonBehaviour.Movement.Follow();
        behaviour.movement.follow.start_distance = 10F;
        behaviour.movement.follow.stop_distance = 3.0F;
        behaviour.movement.follow.teleport_after_distance = 24F;

        behaviour.targeting.automatic_targeting = SummonBehaviour.Targeting.AutoTarget.HOSTILE;
        behaviour.targeting.attack_with_owner = true;
        behaviour.targeting.revenge = true;

        behaviour.sounds.ambient = new Sound(ElementalSounds.GOLEM_GROWL.id());

        var melee = new SummonBehaviour.Action.MeleeAttack();
        melee.max_range = 4.0F;
        melee.radius = 3.0F;
        melee.duration = 33;
        melee.windup = 8F / 33F;
        melee.speed = 0.55F;
        melee.swing_sound = new Sound(ElementalSounds.GOLEM_GROWL.id().toString());
        melee.impact_sound = new Sound(ElementalSounds.GOLEM_GROUND_SLAM.id().toString());

        var stoneThrow = new SummonBehaviour.Action.SpellCast();
        stoneThrow.spell_id = ElementalWizardSpells.terra_stone_throw.id().toString();
        stoneThrow.cooldown = 40;

        var spikeLine = new SummonBehaviour.Action.SpellCast();
        spikeLine.spell_id = Identifier.of(MOD_ID, "terra_earth_golem_spike_line").toString();
        spikeLine.cooldown = 60;

        behaviour.actions = List.of(
                SummonBehaviour.Action.attack(melee),
                SummonBehaviour.Action.spell(stoneThrow),
                SummonBehaviour.Action.spell(spikeLine)
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

    private static List<AttributeScaling.Entry> schoolCombatScaling(SpellSchool school) {
    var s = school.id.toString();
    var entries = new ArrayList<AttributeScaling.Entry>();
        entries.add(scalingEntry(EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(), s, 0, 2.0));
        entries.add(scalingEntry(EntityAttributes.GENERIC_ARMOR.getIdAsString(), s, 10, 0.1));
        entries.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(), s, 0, 0.5));
        entries.add(scalingEntry(s, s, 3, 0.1)); // spell power feeds back into the school attribute
        entries.add(scalingEntry(EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getIdAsString(), s, 0, 0.1));
        entries.add(scalingEntry(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(), s, 5, 0.05));
        return entries;
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
