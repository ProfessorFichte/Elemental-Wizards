package net.elemental_wizards_rpg.spell;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.more_rpg_classes.effect.MRPGCEffects;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalWizardSpells {
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) {
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }


    private static Spell.Impact damageImpact(float coefficient, float knockback) {
        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = coefficient;
        damage.action.damage.knockback = knockback;
        return damage;
    }
    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.cooldown = new Spell.Cost.Cooldown();
        spell.cost.cooldown.duration = duration;
    }
    private static Spell.Impact createEffectImpact(Identifier effectId, float duration) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
        buff.action.status_effect.effect_id = effectId.toString();
        buff.action.status_effect.duration = duration;
        return buff;
    }

    private static Spell modifierSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 1;

        spell.type = Spell.Type.MODIFIER;

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
        spell.tooltip.description.color = Formatting.GRAY.asString();
        spell.tooltip.description.show_in_compact = true;
        spell.tooltip.name.show_in_compact = false;
        spell.tooltip.name.show_in_details = false;
        spell.tooltip.show_header = false;

        return spell;
    }
    private static Spell.Impact.TargetModifier createImpactModifier(String entityType) {
        var condition = new Spell.TargetCondition();
        condition.entity_type = entityType;
        var modifier = new Spell.Impact.TargetModifier();
        modifier.conditions = List.of(condition);
        return modifier;
    }
    private static void bleedImmuneDeny(Spell.Impact impact) {
        var modifier = createImpactModifier("#minecraft:undead");
        modifier.execute = TriState.DENY;
        impact.target_modifiers = List.of(modifier);
    }
    private static void bossImmuneDeny(Spell.Impact impact) {
        var modifier = createImpactModifier("#c:bosses");
        modifier.execute = TriState.DENY;
        impact.target_modifiers = List.of(modifier);
    }
    private static void impactDeniedForMechanical(Spell.Impact impact) {
        var modifier = createImpactModifier("#spell_engine:mechanical");
        modifier.execute = TriState.DENY;
        impact.target_modifiers = List.of(modifier);
    }
    private static Spell passiveSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.PASSIVE;
        spell.passive = new Spell.Passive();
        return spell;
    }
    private static Spell.Impact createHeal(float coefficient) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.HEAL;
        buff.action.heal = new Spell.Impact.Action.Heal();
        buff.action.heal.spell_power_coefficient = coefficient;
        return buff;
    }
    private static Spell.Impact.TargetModifier critAgainstWaterVulnerable() {
        var modifier = createImpactModifier("#more_rpg_classes:vulnerable_to_water_spells");
        var powerModifier = new Spell.Impact.Modifier();
        powerModifier.critical_chance_bonus = 0.3F;
        modifier.modifier = powerModifier;
        return modifier;
    }
    ///MODIFIERS
    public static Entry improved_wind_updraft = add(improved_wind_updraft());
    private static Entry improved_wind_updraft() {
        var id = Identifier.of(MOD_ID, "improved_wind_updraft");
        var title = "Improved Updraft";
        var description = "Increases critical chance of Updraft by {critical_chance_bonus}";
        var spell = modifierSpellBase();
        spell.school = MoreSpellSchools.AIR;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "elemental_wizards_rpg:wind_updraft";
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.critical_chance_bonus = 0.05F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null);
    }
    public static final Entry improved_terra_drip_circle = add(improved_terra_drip_circle());
    private static Entry improved_terra_drip_circle() {
        var id = Identifier.of(MOD_ID, "improved_terra_drip_circle");
        var title = "Improved Terra Circle";
        var description = "Increases the duration of Terra Circle by {spawn_duration_add} sec.";
        var spell = modifierSpellBase();
        spell.school = MoreSpellSchools.EARTH;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "elemental_wizards_rpg:terra_drip_circle";
        modifier.spawn_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null);
    }
    public static Entry improved_aqua_springwater = add(improved_aqua_springwater());
    private static Entry improved_aqua_springwater() {
        var id = Identifier.of(MOD_ID, "improved_aqua_springwater");
        var title = "Improved Springwater";
        var description = "Increases duration of Springwater Regeneration by {effect_duration_add} sec";
        var spell = modifierSpellBase();
        spell.school = MoreSpellSchools.WATER;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "elemental_wizards_rpg:aqua_springwater";
        modifier.effect_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null);
    }
    ///PASSIVES
    public static Entry elemental_avatar = add(elemental_avatar());
    private static Entry elemental_avatar() {
        var id = Identifier.of(MOD_ID, "elemental_avatar");
        var title = "";
        var description = "";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.GENERIC;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.chance = 0.2F;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        trigger.spell = new Spell.Trigger.SpellCondition();
        trigger.spell.type = Spell.Type.ACTIVE;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "elemental_wizards_rpg:avatar_impact";

        spell.impacts = List.of(custom);

        configureCooldown(spell, 20);

        return new Entry(id, spell, title, description, null);
    }
    public static Entry avatar_passives_air_draft = add(avatar_passives_air_draft());
    private static Entry avatar_passives_air_draft() {
        var id = Identifier.of(MOD_ID, "avatar_passives/air_draft");
        var title = "";
        var description = "";
        var spell = passiveSpellBase();
        spell.school = MoreSpellSchools.AIR;

        var damage = damageImpact(0.4F, 0.1F);
        damage.sound = new Sound("more_rpg_classes:air_magic_impact2");

        var debuff = createEffectImpact(Identifier.of("minecraft:levitation"), 2);
        bossImmuneDeny(debuff);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier_power_multiplier = 0.3F;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        25, 0.2F, 1.0F).extent(1),
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        25, 0.2F, 1.0F).extent(3),
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        25, 0.2F, 1.0F).extent(5),
        };

        spell.impacts = List.of(debuff, damage);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 5.0F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "more_rpg_classes:small_gust",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        50, 0.5F, 1.0F)
        };
        configureCooldown(spell, 20);
        return new Entry(id, spell, title, description, null);
    }
    public static Entry avatar_passives_earth_stoning = add(avatar_passives_earth_stoning());
    private static Entry avatar_passives_earth_stoning() {
        var id = Identifier.of(MOD_ID, "avatar_passives/earth_stoning");
        var title = "";
        var description = "";
        var spell = passiveSpellBase();
        spell.school = MoreSpellSchools.EARTH;

        spell.deliver.type = Spell.Delivery.Type.METEOR;
        var meteor = new Spell.Delivery.Meteor();
        meteor.launch_height = 12;
        meteor.launch_radius = 4;
        meteor.launch_properties.velocity = 0.8F;
        meteor.launch_properties.extra_launch_count = 12;
        meteor.launch_properties.extra_launch_delay = 4;
        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[] {
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        2, 0.1F, 0.3F,0),
        };
        var model = new Spell.ProjectileModel();
        model.model_id = "elemental_wizards_rpg:projectile/spell_stone";
        model.scale = 0.7F;
        projectile.client_data.model = model;

        meteor.projectile = projectile;
        spell.deliver.meteor = meteor;


        var damage = damageImpact(0.5F, 1.5F);
        spell.impacts = List.of(damage);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2.0F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.1F, 0.2F),
                new ParticleBatch(
                        "more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.1F, 0.2F)
        };
        spell.area_impact.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_impact1"),0.7F);
        configureCooldown(spell, 20);
        return new Entry(id, spell, title, description, null);
    }
    public static final Color WATER_SPELL_COLOR = Color.from(0x4a8bff);
    public static final Entry avatar_passives_water_undercurrent= add(avatar_passives_water_undercurrent());
    private static Entry avatar_passives_water_undercurrent() {
        var id = Identifier.of(MOD_ID, "avatar_passives/water_undercurrent");
        var title = "";
        var description = "";
        var spell = passiveSpellBase();
        spell.school = MoreSpellSchools.WATER;

        var areaParticle = SpellEngineParticles.area_effect_658;

        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 4.0F;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.volume.sound = new Sound("");
        cloud.impact_tick_interval = 15;
        cloud.time_to_live_seconds = 5;
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "more_rpg_classes:splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0, 0)
        };
        cloud.client_data.particle_spawn_interval = SpellEngineParticles.area_effect_480.texture().frames();
        cloud.client_data.interval_particles = new ParticleBatch[] {
                new ParticleBatch(areaParticle.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .scale(4)
                        .color(WATER_SPELL_COLOR.alpha(0.75F).toRGBA()),
        };
        spell.deliver.clouds = List.of(cloud);


        var damage = damageImpact(0.15F, 1.5F);
        damage.target_modifiers = List.of(critAgainstWaterVulnerable());
        damage.sound = new Sound("more_rpg_classes:water_magic_impact1");
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "more_rpg_classes:splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0, 0)
        };

        var heal = createHeal(0.2F);
        impactDeniedForMechanical(heal);
        heal.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.HEAL,
                                SpellEngineParticles.MagicParticles.Motion.ASCEND).id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        15, 0.02F, 0.15F)
                        .color(WATER_SPELL_COLOR.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.HOLY,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15, 0.2F, 0.25F)
                        .color(WATER_SPELL_COLOR.toRGBA())
        };
        heal.sound = new Sound("spell_engine:generic_healing_impact_2");

        spell.impacts = List.of(damage, heal);

        configureCooldown(spell, 20);
        return new Entry(id, spell, title, description, null);
    }
    ///ACTIVE SPELLS
    public static Entry terra_drip_circle = add(terra_drip_circle());
    private static Entry terra_drip_circle() {
        var id = Identifier.of(MOD_ID, "terra_drip_circle");
        var name = "";
        var description = "";
        var debuffEffect = MRPGCEffects.BLEEDING;

        var spell = SpellBuilder.createSpellActive();
        spell.range = 15;
        spell.tier = 3;
        spell.school = MoreSpellSchools.EARTH;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.cast(spell,0.7F);
        SpellBuilder.Casting.visuals(spell,"more_rpg_classes:two_handed_ground_channeling",
                new ParticleBatch[] {
                        new ParticleBatch("more_rpg_classes:stone_particle",
                                ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                                3, 0.01F, 0.05F),
                },
                new Sound("more_rpg_classes:earth_magic_cast1"));
        SpellBuilder.Release.visuals(spell,
                "more_rpg_classes:two_handed_ground_release",
                new ParticleBatch[] {
                        new ParticleBatch("more_rpg_classes:stone_particle",
                                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                                3, 0.01F, 0.05F),
                        new ParticleBatch("campfire_cosy_smoke",
                                ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                                3.0F, 0.001F, 0.001F),
                } ,
                new Sound("more_rpg_classes:earth_magic_impact1"));

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.use_caster_as_fallback = true;
        spell.target.aim.sticky = true;

        spell.deliver.type = Spell.Delivery.Type.CLOUD;

        int delay1 = 2;
        int delay2 = 6;
        int delay3 = 10;
        float offset1 = 1.0F;
        float offset2 = 2.0F;
        float offset3 = 3.0F;
        var cloud = new Spell.Delivery.Cloud();
        cloud.volume.radius = 0.4F;
        cloud.volume.area.vertical_range_multiplier = 0.3F;
        cloud.delay_ticks = delay1;
        cloud.impact_tick_interval = 20;
        cloud.time_to_live_seconds = 5;
        cloud.spawn = new Spell.Delivery.Cloud.Spawn();
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.model = new Spell.ProjectileModel();
        cloud.client_data.model.model_id = "elemental_wizards_rpg:effect/dripstone_big";
        cloud.client_data.model.rotate_degrees_per_tick = 0;
        cloud.client_data.model.light_emission = LightEmission.NONE;
        cloud.client_data.model.scale = 0.3F;
        cloud.client_data.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F),
        };

        cloud.placement = SpellBuilder.Deliver.placementByLook(0, 0, 0);
        cloud.additional_placements = List.of(
                SpellBuilder.Deliver.placementByLook(offset1, 0, 0),
                SpellBuilder.Deliver.placementByLook(offset1, 32, 0),
                SpellBuilder.Deliver.placementByLook(offset1, 64, 0),
                SpellBuilder.Deliver.placementByLook(offset1, 96, 0),
                SpellBuilder.Deliver.placementByLook(offset1, 128, 0),
                SpellBuilder.Deliver.placementByLook(offset1, 160, 0),
                SpellBuilder.Deliver.placementByLook(offset1, -32, 0),
                SpellBuilder.Deliver.placementByLook(offset1, -64, 0),
                SpellBuilder.Deliver.placementByLook(offset1, -96, 0),
                SpellBuilder.Deliver.placementByLook(offset1, -128, 0),
                SpellBuilder.Deliver.placementByLook(offset1, -160, 0)
        );
        var cloud2 = new Spell.Delivery.Cloud();
        cloud2.volume.radius = 0.4F;
        cloud2.volume.area.vertical_range_multiplier = 0.5F;
        cloud2.delay_ticks = delay2;
        cloud2.impact_tick_interval = 20;
        cloud2.time_to_live_seconds = 5;
        cloud2.spawn = new Spell.Delivery.Cloud.Spawn();
        cloud2.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud2.client_data.model = new Spell.ProjectileModel();
        cloud2.client_data.model.model_id = "elemental_wizards_rpg:effect/dripstone_big";
        cloud2.client_data.model.rotate_degrees_per_tick = 0;
        cloud2.client_data.model.light_emission = LightEmission.NONE;
        cloud2.client_data.model.scale = 0.5F;
        cloud2.client_data.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F),
        };

        cloud2.placement = SpellBuilder.Deliver.placementByLook(offset2, 0, 0);
        cloud2.additional_placements = List.of(
                SpellBuilder.Deliver.placementByLook(offset2, 32, 0),
                SpellBuilder.Deliver.placementByLook(offset2, 64, 0),
                SpellBuilder.Deliver.placementByLook(offset2, 96, 0),
                SpellBuilder.Deliver.placementByLook(offset2, 128, 0),
                SpellBuilder.Deliver.placementByLook(offset2, 160, 0),
                SpellBuilder.Deliver.placementByLook(offset2, -32, 0),
                SpellBuilder.Deliver.placementByLook(offset2, -64, 0),
                SpellBuilder.Deliver.placementByLook(offset2, -96, 0),
                SpellBuilder.Deliver.placementByLook(offset2, -128, 0),
                SpellBuilder.Deliver.placementByLook(offset2, -160, 0)
        );
        var cloud3 = new Spell.Delivery.Cloud();
        cloud3.volume.radius = 0.7F;
        cloud3.volume.area.vertical_range_multiplier = 1.25F;
        cloud3.delay_ticks = delay3;
        cloud3.impact_tick_interval = 20;
        cloud3.time_to_live_seconds = 5;
        cloud3.spawn = new Spell.Delivery.Cloud.Spawn();
        cloud3.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud3.client_data.model = new Spell.ProjectileModel();
        cloud3.client_data.model.model_id = "elemental_wizards_rpg:effect/dripstone_big";
        cloud3.client_data.model.rotate_degrees_per_tick = 0;
        cloud3.client_data.model.light_emission = LightEmission.NONE;
        cloud3.client_data.model.scale = 1.0F;
        cloud3.client_data.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F),
        };

        cloud3.placement = SpellBuilder.Deliver.placementByLook(offset3, 0, 0);
        cloud3.additional_placements = List.of(
                SpellBuilder.Deliver.placementByLook(offset3, 32, 0),
                SpellBuilder.Deliver.placementByLook(offset3, 64, 0),
                SpellBuilder.Deliver.placementByLook(offset3, 96, 0),
                SpellBuilder.Deliver.placementByLook(offset3, 128, 0),
                SpellBuilder.Deliver.placementByLook(offset3, 160, 0),
                SpellBuilder.Deliver.placementByLook(offset3, -32, 0),
                SpellBuilder.Deliver.placementByLook(offset3, -64, 0),
                SpellBuilder.Deliver.placementByLook(offset3, -96, 0),
                SpellBuilder.Deliver.placementByLook(offset3, -128, 0),
                SpellBuilder.Deliver.placementByLook(offset3, -160, 0)
        );

        spell.deliver.clouds = List.of(cloud, cloud2,cloud3);

        var damage = SpellBuilder.Impacts.damage(0.65F, 0F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F)
        };
        damage.sound = new Sound("block.pointed_dripstone.break");

        var debuff = createEffectImpact(debuffEffect.id, 4);
        bleedImmuneDeny(debuff);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier_power_multiplier = 0.2F;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F),
                new ParticleBatch("more_rpg_classes:blood_drop",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                        10, 0.2F, 0.4F),
        };

        spell.impacts = List.of(damage, debuff);

        SpellBuilder.Cost.cooldown(spell, 22);
        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

        return new Entry(id, spell, name, description, null);
    }
}
