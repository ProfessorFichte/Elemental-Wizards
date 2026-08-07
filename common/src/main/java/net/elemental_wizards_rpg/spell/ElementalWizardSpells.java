package net.elemental_wizards_rpg.spell;

import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.entity.ElementalSummons;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.effect.SpellEngineEffects;
import net.more_rpg_classes.sounds.MRPGLibSounds;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.api.spell.fx.ModelEffectBuilder;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalWizardSpells {
    public enum WeaponGroup { ELEMENTAL_STAFF, AQUA_STAFF, TERRA_STAFF, WIND_STAFF }
    public enum Book { AQUA, TERRA, WIND }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator,
                        @Nullable List<WeaponGroup> weaponGroups,
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null,List.of(), null);
        }
        public Entry mutator(SpellTooltip.DescriptionMutator mutator) {
            return new Entry(id, spell, title, description, mutator,weaponGroups ,book);
        }
        public Entry weaponGroup(WeaponGroup weaponGroup) {
            var newGroups = new ArrayList<>(weaponGroups != null ? weaponGroups : List.of());
            newGroups.add(weaponGroup);
            return new Entry(id, spell, title, description, mutator, newGroups, book);
        }
        public Entry book(Book book) {
            return new Entry(id, spell, title, description, mutator, weaponGroups,book);
        }
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final String CURING =  "aqua_curing";
    public static final String CONTROL = "aqua_control";
    public static final String STONESHAPING = "terra_stoneshaping";
    public static final String GEOMANCING = "terra_geomancing";
    public static final String COMBO = "wind_combo";
    public static final String PRESSURE = "wind_pressure";

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
    private static void bossImmuneDeny(Spell.Impact impact) {
        var modifier = createImpactModifier("#c:bosses");
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
    private static ParticleBatch[] aquaCastingParticles(Spell spell) {
        return new ParticleBatch[] {
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                        1, 0.01F, 0.05F).extent(0.5F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.002F, 0.003F)
        };
    }
    private static ParticleBatch[] terraCastingParticles(Spell spell) {
        return new ParticleBatch[] {
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                        1, 0.01F, 0.02F)
        };
    }
    private static ParticleBatch[] windCastingParticles(Spell spell) {
        return new ParticleBatch[] {
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        1, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        1, 0.05F, 0.2F)
        };
    }
    public static final Color WATER_SPELL_COLOR = Color.from(0x4a8bff);
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

        return new Entry(id, spell, title, description);
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

        return new Entry(id, spell, title, description);
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

        return new Entry(id, spell, title, description);
    }
    public static Entry elemental_avatar = add(elemental_avatar());
    private static Entry elemental_avatar() {
        var id = Identifier.of(MOD_ID, "elemental_avatar");
        var title = "Elemental Avatar";
        var description = "{trigger_chance} chance on spell impact: {avatar_impact}.";
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

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            double air_power = args.player().getAttributeValue(MoreSpellSchools.AIR.attributeEntry);
            double earth_power = args.player().getAttributeValue(MoreSpellSchools.EARTH.attributeEntry);
            double water_power = args.player().getAttributeValue(MoreSpellSchools.WATER.attributeEntry);
            String subSpellPath;
            if (air_power >= earth_power && air_power >= water_power) {
                subSpellPath = "avatar_passives/air_draft";
            } else if (earth_power >= air_power && earth_power >= water_power) {
                subSpellPath = "avatar_passives/earth_stoning";
            } else {
                subSpellPath = "avatar_passives/water_undercurrent";
            }
            var subSpellId = Identifier.of(MOD_ID, subSpellPath);
            var optional = SpellRegistry.from(world).getEntry(subSpellId);
            if (optional.isEmpty()) return args.description();
            var subSpell = optional.get().value();
            var subDesc = I18n.translate(SpellTooltip.spellDescriptionTranslationKey(subSpellId));
            var estimated = SpellHelper.estimate(subSpell, args.player(), ItemStack.EMPTY);
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                subDesc = subDesc.replace(SpellTooltip.placeholder(SpellTooltip.damageToken), SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            if (!estimated.heal().isEmpty()) {
                var heal = estimated.heal().get(0);
                subDesc = subDesc.replace(SpellTooltip.placeholder(SpellTooltip.healToken), SpellTooltip.formattedRange(heal.min(), heal.max()));
            }
            return args.description().replace("{avatar_impact}", subDesc);
        };

        return new Entry(id, spell, title, description).mutator(mutator);
    }
    public static final Entry aqua_splash = add(aqua_splash());
    private static Entry aqua_splash() {
        var id = Identifier.of(MOD_ID, "aqua_splash");
        var title = "Splash";
        var description = "Release a splash of water that heals allies by {heal} and deals {damage} damage to targets.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.group = "primary";
        spell.range = 16;
        spell.tier = 0;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.2F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound("block.bubble_column.whirlpool_ambient");
        spell.active.cast.particles = aquaCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.use_caster_as_fallback = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        var heal = createHeal(0.5F);
        heal.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_heal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.05F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        1.0F, 0.2F, 1.0F)
        };
        heal.sound = new Sound("spell_engine:generic_healing_impact_1");

        var damage = damageImpact(0.6F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        10, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F),
                new ParticleBatch("more_rpg_classes:water_splash",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        2.0F, 0.2F, 0.5F)
        };
        damage.sound = Sound.withVolume(Identifier.of("more_rpg_classes:water_magic_impact1"), 0.4F);

        var soaked = createEffectImpact(Identifier.of("more_rpg_classes:soaked"), 4);
        soaked.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        soaked.action.status_effect.show_particles = false;
        var soakedModifier = createImpactModifier("#more_rpg_classes:resistant_to_water");
        soakedModifier.execute = TriState.DENY;
        soaked.target_modifiers = List.of(soakedModifier);

        spell.impacts = List.of(heal, damage, soaked);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        return new Entry(id, spell, title, description);
    }
    public static final Entry aqua_water_whip = add(aqua_water_whip());
    private static Entry aqua_water_whip() {
        var id = Identifier.of(MOD_ID, "aqua_water_whip");
        var title = "Water Whip";
        var description = "Lash out with a tendril of water that deals {damage} damage and soaks the target.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.group = "primary";
        spell.range = 16;
        spell.tier = 1;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("block.bubble_column.whirlpool_ambient"), 1.0F);
        spell.active.cast.particles = aquaCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = false;
        spell.target.aim.required = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:splash",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        10.0F, 0.05F, 0.1F)
        };
        spell.release.sound = new Sound(ElementalSounds.WATER_WHIP_RELEASE.id().toString());

        var damage = damageImpact(0.75F, 0.5F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:water_whip",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.1F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F)
        };
        damage.sound = new Sound(ElementalSounds.WATER_WHIP_IMPACT.id().toString());

        var soaked = createEffectImpact(Identifier.of("more_rpg_classes:soaked"), 6);
        soaked.action.status_effect.amplifier = 0;
        soaked.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        soaked.action.status_effect.show_particles = false;
        var soakedModifier = createImpactModifier("#more_rpg_classes:resistant_to_water");
        soakedModifier.execute = TriState.DENY;
        soaked.target_modifiers = List.of(soakedModifier);

        spell.impacts = List.of(damage, soaked);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        return new Entry(id, spell, title, description).weaponGroup(WeaponGroup.AQUA_STAFF).weaponGroup(WeaponGroup.ELEMENTAL_STAFF);
    }
    public static final Entry aqua_bubble_beam = add(aqua_bubble_beam());
    private static Entry aqua_bubble_beam() {
        var id = Identifier.of(MOD_ID, "aqua_bubble_beam");
        var title = "Bubble Beam";
        var description = "Channel a cone of bubble {damage} damage to enemies and heal allies by {heal}..";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 7;
        spell.tier = 2;
        spell.group = CURING;
        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell, 4, 22);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_healing_charge");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("block.bubble_column.upwards_ambient"), 2.5F);
        spell.active.cast.start_sound = new Sound("block.bubble_column.whirlpool_ambient");
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:bubble",
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        3.0F, 0.2F, 1.0F, 65)
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.target.area.angle_degrees = 65;

        spell.release = new Spell.Release();
        spell.release.sound = new Sound("block.bubble_column.whirlpool_ambient");

        var heal = createHeal(0.3F);
        heal.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_heal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        1.0F, 0.2F, 1.0F)
        };
        heal.sound = Sound.withVolume(Identifier.of("spell_engine:generic_healing_impact_2"), 1.2F);

        var damage = damageImpact(0.85F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:bubble_pop",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        10.0F, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:bubble",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5.0F, 0.001F, 0.1F)
        };
        damage.sound = new Sound("block.bubble_column.bubble_pop");

        var bubbleFoam = createEffectImpact(Identifier.of(MOD_ID, "bubble_foam"), 4);
        bubbleFoam.action.status_effect.amplifier = 0;
        bubbleFoam.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        bubbleFoam.action.status_effect.amplifier_power_multiplier = 0.2F;
        bubbleFoam.action.status_effect.show_particles = false;
        bubbleFoam.action.apply_to_caster = true;

        spell.impacts = List.of(heal, damage, bubbleFoam);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 15);
        spell.cost.cooldown.haste_affected = true;
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        return new Entry(id, spell, title, description).book(Book.AQUA);
    }
    public static final Entry aqua_waterball = add(aqua_waterball());
    private static Entry aqua_waterball() {
        var id = Identifier.of(MOD_ID, "aqua_waterball");
        var title = "Waterballs";
        var description = "Shoots multiple water balls that deal {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 32;
        spell.group = CONTROL;
        spell.tier = 2;

        spell.active.cast.duration = 0.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("block.bubble_column.whirlpool_ambient"), 1.0F);
        spell.active.cast.particles = aquaCastingParticles(spell);;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
        spell.release.sound = new Sound(MRPGLibSounds.WATER_RELEASE_1.id().toString());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.0F;
        spell.deliver.projectile.launch_properties.extra_launch_count = 4;
        spell.deliver.projectile.launch_properties.extra_launch_delay = 3;

        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:bubble",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        0.1F, 0.6F, 0.9F, 0)
        };
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.single("elemental_wizards_rpg:spell_projectile/big_bubble", 0.7F, LightEmission.NONE);
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(0.4F, 0.5F);
        damage.sound = new Sound(MRPGLibSounds.WATER_MAGIC_IMPACT_1.id().toString());

        var heal = createHeal(0.1F);
        heal.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_heal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        1.0F, 0.2F, 1.0F)
        };
        heal.sound = Sound.withVolume(Identifier.of("spell_engine:generic_healing_impact_2"), 1.2F);

        spell.impacts = List.of(damage, heal);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);
        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 10);

        return new Entry(id, spell, title, description).book(Book.AQUA);
    }
    public static final Entry aqua_springwater = add(aqua_springwater());
    private static Entry aqua_springwater() {
        var id = Identifier.of(MOD_ID, "aqua_springwater");
        var title = "Springwater";
        var description = "Release a burst of cleansing water that heals allies by {heal} and damages targets by {damage} damage in an area around you.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 6;
        spell.tier = 3;
        spell.group = CURING;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.sound = new Sound("block.bubble_column.whirlpool_ambient");
        spell.active.cast.particles = aquaCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.include_caster = true;
        spell.target.area.angle_degrees = 360.0F;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");
        spell.release.sound = new Sound("");

        var heal = createHeal(0.2F);
        heal.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_heal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.05F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F)
        };
        heal.sound = Sound.withVolume(Identifier.of(SpellEngineSounds.GENERIC_HEALING_IMPACT_2.id().toString()), 1.2F);

        var damage = damageImpact(0.7F, 0.5F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        25, 0.3F, 0.7F).extent(0.75F),
                new ParticleBatch("more_rpg_classes:splash",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        5.0F, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F)
        };
        damage.sound = Sound.withVolume(MRPGLibSounds.WATER_MAGIC_IMPACT_1.id(),1.2F);

        var soaked = createEffectImpact(Identifier.of("more_rpg_classes:soaked"), 6);
        soaked.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        soaked.action.status_effect.show_particles = false;
        var soakedModifier = createImpactModifier("#more_rpg_classes:resistant_to_water");
        soakedModifier.execute = TriState.DENY;
        soaked.target_modifiers = List.of(soakedModifier);

        var cleansing = createEffectImpact(Identifier.of(MOD_ID, "cleansing_water"), 3);
        cleansing.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        cleansing.action.status_effect.amplifier_power_multiplier = 0.2F;
        cleansing.action.status_effect.show_particles = false;

        spell.impacts = List.of(heal, damage, soaked, cleansing);

        SpellBuilder.Cost.exhaust(spell, 0.4F);
        SpellBuilder.Cost.cooldown(spell, 20);
        spell.cost.cooldown.haste_affected = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        return new Entry(id, spell, title, description).book(Book.AQUA);
    }
    public static final Entry aqua_hydro_beam = add(aqua_hydro_beam());
    private static Entry aqua_hydro_beam() {
        var id = Identifier.of(MOD_ID, "aqua_hydro_beam");
        var title = "Hydro Beam";
        var description = "Channel a powerful beam of pressurized water that deals {damage} damage and soaks enemies in its path.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 32;
        spell.tier = 3;
        spell.group = CONTROL;
        spell.learn = new Spell.Learn();


        SpellBuilder.Casting.channel(spell, 5, 25);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("entity.boat.paddle_water"), 3.0F);
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        4.0F, 10.0F, 15.0F, 20),
                new ParticleBatch("more_rpg_classes:water_mist",
                        ParticleBatch.Shape.CONE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        2.0F, 0.5F, 1.0F, 35)
        };

        spell.target.type = Spell.Target.Type.BEAM;
        spell.target.beam = new Spell.Target.Beam();
        spell.target.beam.texture_id = "elemental_wizards_rpg:textures/entity/hydro_beam.png";
        spell.target.beam.width = 0.08F;
        spell.target.beam.flow = 2.0F;
        spell.target.beam.block_hit_particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_mist",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        1.5F, 0.1F, 0.2F, 0)
        };

        spell.release = new Spell.Release();
        spell.release.sound = new Sound("block.bubble_column.whirlpool_ambient");

        var damage = damageImpact(0.8F, 1.5F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        20.0F, 0.1F, 0.4F),
                new ParticleBatch("more_rpg_classes:water_mist",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        2.0F, 0.01F, 0.03F)
        };
        damage.sound = Sound.withVolume(Identifier.of("more_rpg_classes:water_magic_impact1"), 0.4F);

        var soaked = createEffectImpact(Identifier.of("more_rpg_classes:soaked"), 10);
        soaked.action.status_effect.amplifier = 0;
        soaked.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        soaked.action.status_effect.show_particles = false;
        var soakedModifier = createImpactModifier("#more_rpg_classes:resistant_to_water");
        soakedModifier.execute = TriState.DENY;
        soaked.target_modifiers = List.of(soakedModifier);

        spell.impacts = List.of(damage, soaked);

        SpellBuilder.Cost.exhaust(spell, 0.2F);
        SpellBuilder.Cost.cooldown(spell, 22);
        spell.cost.cooldown.haste_affected = true;
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        return new Entry(id, spell, title, description).book(Book.AQUA);
    }
    public static final Entry aqua_healing_rain = add(aqua_healing_rain());
    private static Entry aqua_healing_rain() {
        var id = Identifier.of(MOD_ID, "aqua_healing_rain");
        var title = "Healing Rain Cloud";
        var description = "Calls a healing rain cloud that deals {rain_damage} damage to targets and heals allies by {rain_heal} hearts.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 5;
        spell.tier = 4;
        spell.group = CURING;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.movement_speed = 0;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_sky_charge");
        spell.active.cast.sound = new Sound("weather.rain.above");
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch("elemental_wizards_rpg:healing_rain",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                        10.0F, 0.01F, 0.1F).extent(1.5F),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.HOLY,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.2F, 0.25F)
                        .color(WATER_SPELL_COLOR.toRGBA()).extent(1.5F)
        };

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:two_handed_channeling_release");

        int delay = 0;
        int toLiveSeconds = 12;
        String entityId = "elemental_wizards_rpg:healing_rain_cloud";
        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        var cloud = new Spell.Impact.Action.Spawn();
        cloud.entity_type_id = entityId;
        cloud.delay_ticks = delay;
        cloud.time_to_live_seconds = toLiveSeconds;
        cloud.placement.apply_yaw = true;
        spawn.action.spawns = List.of(cloud);


        spell.impacts = List.of(spawn);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 40);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/aqua_healing_rain_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{rain_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            if (!estimated.heal().isEmpty()) {
                var heal = estimated.heal().get(0);
                desc = desc.replace("{rain_heal}", SpellTooltip.formattedRange(heal.min(), heal.max()));
            }
            return desc;
        };
        return new Entry(id, spell, title, description).book(Book.AQUA).mutator(mutator);
    }
    public static final Entry aqua_tidal_wave = add(aqua_tidal_wave());
    private static Entry aqua_tidal_wave() {
        var id = Identifier.of(MOD_ID, "aqua_tidal_wave");
        var title = "Tidal Wave";
        var description = "Tidal Wave that travels back and forth and deals {wave_damage} damage and knocks targets back.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 16;
        spell.tier = 4;
        spell.group = CONTROL;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.sound = new Sound("block.bubble_column.upwards_inside");
        spell.active.cast.particles = aquaCastingParticles(spell);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:two_handed_channeling_release");
        spell.release.sound = new Sound(ElementalSounds.TIDAL_WAVE_RELEASE.id().toString());

        int delay = 0;
        int toLiveSeconds = 20;
        String entityId = "elemental_wizards_rpg:tidal_wave";
        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        var wave = new Spell.Impact.Action.Spawn();
        wave.entity_type_id = entityId;
        wave.delay_ticks = delay;
        wave.time_to_live_seconds = toLiveSeconds;
        wave.placement.apply_yaw = true;
        spawn.action.spawns = List.of(wave);

        spell.impacts = List.of(spawn);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 32);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:aqua_stone", 1);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/aqua_tidal_wave_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{wave_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            return desc;
        };
        return new Entry(id, spell, title, description).book(Book.AQUA).mutator(mutator);
    }
    public static final Entry terra_stone_throw = add(terra_stone_throw());
    private static Entry terra_stone_throw() {
        var id = Identifier.of(MOD_ID, "terra_stone_throw");
        var title = "Stone Throw";
        var description = "Hurl a chunk of stone at an enemy, that deals {damage} damage in a small area on impact.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.group = "primary";
        spell.range = 20;
        spell.tier = 0;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_cast1"), 0.5F);
        spell.active.cast.particles = terraCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.1F;

        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        1.0F, 0.1F, 0.2F, 0)
        };
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.single("elemental_wizards_rpg:spell_projectile/spell_stone", 0.5F);
        spell.deliver.projectile.projectile = projectile;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");
        spell.release.sound = Sound.withVolume(Identifier.of("block.pointed_dripstone.fall"), 1.5F);

        var damage = damageImpact(0.7F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        3.0F, 0.005F, 0.008F),
                new ParticleBatch("more_rpg_classes:stone_explosion",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.6F, 1.0F)
        };

        spell.impacts = List.of(damage);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 1.5F;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.6F, 1.0F)
        };
        spell.area_impact.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_impact1"), 0.5F);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);

        return new Entry(id, spell, title, description);
    }
    public static final Entry terra_stone_spear = add(terra_stone_spear());
    private static Entry terra_stone_spear() {
        var id = Identifier.of(MOD_ID, "terra_stone_spear");
        var title = "Stone Spear";
        var description = "Launch a sharp stone spear that pierces enemies, causing {damage} damage and bleeding wounds.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.group = "primary";
        spell.range = 32;
        spell.tier = 1;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.2F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_cast1"), 0.5F);
        spell.active.cast.particles = terraCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;

        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[]{
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        0.1F, 0.6F, 0.9F, 0)
        };
        var stoneSpearModel = SpellBuilder.ProjectileModels.model("elemental_wizards_rpg:spell_projectile/stone_spear", 1.1F, LightEmission.NONE);
        stoneSpearModel.rotate_degrees_per_tick = 1.3F;
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.composite(stoneSpearModel);
        spell.deliver.projectile.projectile = projectile;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");
        spell.release.sound = Sound.withVolume(Identifier.of("block.pointed_dripstone.fall"), 1.5F);

        var damage = damageImpact(0.65F, 0);
        damage.sound = new Sound("block.pointed_dripstone.land");

        var bleeding = createEffectImpact(SpellEngineEffects.BLEED.id, 3);
        bleeding.action.status_effect.amplifier = 0;
        bleeding.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        bleeding.action.status_effect.amplifier_power_multiplier = 0.2F;
        bleeding.action.status_effect.amplifier_cap = 2;
        bleeding.action.status_effect.show_particles = false;
        bleeding.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5.0F, 0.02F, 0.07F)
        };

        spell.impacts = List.of(damage, bleeding);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2.5F;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_explosion",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.6F, 1.0F)
        };
        spell.area_impact.sound = Sound.withVolume(Identifier.of("block.pointed_dripstone.break"), 1.5F);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);

        return new Entry(id, spell, title, description).weaponGroup(WeaponGroup.TERRA_STAFF).weaponGroup(WeaponGroup.ELEMENTAL_STAFF);
    }
    public static final Entry terra_stone_flesh = add(terra_stone_flesh());
    private static Entry terra_stone_flesh() {
        var id = Identifier.of(MOD_ID, "terra_stone_flesh");
        var title = "Stone Flesh";
        var effect = ElementalEffects.STONE_FLESH;
        var description = "Encase yourself and nearby allies in protective stone armor, with full health the next incoming damage will be reduced by 50%%. " +
                " Also increases armor by {bonus} and armor toughness by {bonus2} per stack.";
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().attributes().get(1);
            var modifier2 = effect.config().attributes().get(0);
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            var bonus2 = SpellTooltip.bonus(modifier2.value, modifier2.operation);
            return args.description()
                    .replace("{bonus}", bonus)
                    .replace("{bonus2}", bonus2);
        };

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 5;
        spell.tier = 2;
        spell.group = GEOMANCING;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 0.75F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_area_charge");
        spell.active.cast.particles = terraCastingParticles(spell);
        spell.active.cast.sound = new Sound(MRPGLibSounds.EARTH_MAGIC_CAST_1.id().toString());

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 1.0F;
        spell.target.area.include_caster = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        3.0F, 0.01F, 0.05F)
        };

        var stoneFlesh = createEffectImpact(effect.id, 10);
        stoneFlesh.action.status_effect.amplifier = 0;
        stoneFlesh.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        stoneFlesh.action.status_effect.amplifier_power_multiplier = 0.15F;
        stoneFlesh.action.status_effect.show_particles = false;
        stoneFlesh.sound = Sound.withVolume(ElementalSounds.STONE_FLESH.id(),0.6F);

        spell.impacts = List.of(stoneFlesh);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 22);
        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);
        spell.cost.cooldown.haste_affected = false;

        return new Entry(id, spell, title, description).book(Book.TERRA).mutator(mutator);
    }
    public static final Entry terra_impale = add(terra_impale());
    private static Entry terra_impale() {
        var id = Identifier.of(MOD_ID, "terra_impale");
        var title = "Impale";
        var description = "Impales a target with a sharp stone, blocking its movement and dealing {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 16;
        spell.tier = 2;
        spell.group = STONESHAPING;
        spell.learn = new Spell.Learn();

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");
        spell.release.sound = Sound.withVolume(Identifier.of(MRPGLibSounds.EARTH_MAGIC_CAST_1.id().toString()), 0.5F);
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        3.0F, 0.01F, 0.05F)
        };

        var damage = SpellBuilder.Impacts.damage(0.7F,0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5.0F, 0.02F, 0.07F)
        };

        var impale = SpellBuilder.Impacts.effectSet(ElementalEffects.IMPALED.id.toString(),3,0);
        impale.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        impale.action.status_effect.show_particles = false;
        impale.action.status_effect.apply_limit.health_base = 50;
        impale.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        impale.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        10, 0.1F, 0.2F)
        };
        impale.sound = new Sound(MRPGLibSounds.EARTH_MAGIC_IMPACT_1.id().toString());

        spell.impacts = List.of(damage,impale);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 15);
        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);

        return new Entry(id, spell, title, description).book(Book.TERRA);
    }
    public static Entry terra_drip_circle = add(terra_drip_circle());
    private static Entry terra_drip_circle() {
        var id = Identifier.of(MOD_ID, "terra_drip_circle");
        var name = "Terra Circle";
        var description = "Creates a stone circle at the targets location that blocks it form passing through and deals {terra_circle_damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.range = 15;
        spell.tier = 3;
        spell.group = STONESHAPING;
        spell.school = MoreSpellSchools.EARTH;

        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.cast(spell,0.7F);
        SpellBuilder.Casting.visuals(spell,"more_rpg_classes:two_handed_ground_channeling",
                spell.active.cast.particles = terraCastingParticles(spell),
                new Sound(MRPGLibSounds.EARTH_MAGIC_CAST_1.id().toString()));
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
        spell.target.aim.required = true;
        spell.target.aim.sticky = true;

        int delay = 0;
        int toLiveSeconds = 8;
        int offset = 3;
        String entityId = "elemental_wizards_rpg:terra_stone";
        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.sound = Sound.withVolume(ElementalSounds.EARTH_SUMMON.id(),0.6F);
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        var stone1 = new Spell.Impact.Action.Spawn();
        stone1.intent = SpellTarget.Intent.HARMFUL;
        stone1.entity_type_id = entityId;
        stone1.delay_ticks = delay;
        stone1.time_to_live_seconds = toLiveSeconds;
        stone1.placement.location_offset_by_look = offset;
        stone1.placement.apply_yaw = true;
        var stone2 = new Spell.Impact.Action.Spawn();
        stone2.intent = SpellTarget.Intent.HARMFUL;
        stone2.entity_type_id = entityId;
        stone2.delay_ticks = delay;
        stone2.time_to_live_seconds = toLiveSeconds;
        stone2.placement.location_offset_by_look = offset;
        stone2.placement.location_yaw_offset = 32;
        stone2.placement.apply_yaw = true;
        var stone3 = new Spell.Impact.Action.Spawn();
        stone3.intent = SpellTarget.Intent.HARMFUL;
        stone3.entity_type_id = entityId;
        stone3.delay_ticks = delay;
        stone3.time_to_live_seconds = toLiveSeconds;
        stone3.placement.location_offset_by_look = offset;
        stone3.placement.location_yaw_offset = 32;
        stone3.placement.apply_yaw = true;
        var stone4 = new Spell.Impact.Action.Spawn();
        stone4.intent = SpellTarget.Intent.HARMFUL;
        stone4.entity_type_id = entityId;
        stone4.delay_ticks = delay;
        stone4.time_to_live_seconds = toLiveSeconds;
        stone4.placement.location_offset_by_look = offset;
        stone4.placement.location_yaw_offset = 64;
        stone4.placement.apply_yaw = true;
        var stone5 = new Spell.Impact.Action.Spawn();
        stone5.intent = SpellTarget.Intent.HARMFUL;
        stone5.entity_type_id = entityId;
        stone5.delay_ticks = delay;
        stone5.time_to_live_seconds = toLiveSeconds;
        stone5.placement.location_offset_by_look = offset;
        stone5.placement.location_yaw_offset = 96;
        stone5.placement.apply_yaw = true;
        var stone6 = new Spell.Impact.Action.Spawn();
        stone6.intent = SpellTarget.Intent.HARMFUL;
        stone6.entity_type_id = entityId;
        stone6.delay_ticks = delay;
        stone6.time_to_live_seconds = toLiveSeconds;
        stone6.placement.location_offset_by_look = offset;
        stone6.placement.location_yaw_offset = 128;
        stone6.placement.apply_yaw = true;
        var stone7 = new Spell.Impact.Action.Spawn();
        stone7.intent = SpellTarget.Intent.HARMFUL;
        stone7.entity_type_id = entityId;
        stone7.delay_ticks = delay;
        stone7.time_to_live_seconds = toLiveSeconds;
        stone7.placement.location_offset_by_look = offset;
        stone7.placement.location_yaw_offset = 160;
        stone7.placement.apply_yaw = true;
        var stone8 = new Spell.Impact.Action.Spawn();
        stone8.intent = SpellTarget.Intent.HARMFUL;
        stone8.entity_type_id = entityId;
        stone8.delay_ticks = delay;
        stone8.time_to_live_seconds = toLiveSeconds;
        stone8.placement.location_offset_by_look = offset;
        stone8.placement.location_yaw_offset = 32;
        stone8.placement.apply_yaw = true;
        var stone9 = new Spell.Impact.Action.Spawn();
        stone9.intent = SpellTarget.Intent.HARMFUL;
        stone9.entity_type_id = entityId;
        stone9.delay_ticks = delay;
        stone9.time_to_live_seconds = toLiveSeconds;
        stone9.placement.location_offset_by_look = offset;
        stone9.placement.location_yaw_offset = -32;
        stone9.placement.apply_yaw = true;
        var stone10 = new Spell.Impact.Action.Spawn();
        stone10.intent = SpellTarget.Intent.HARMFUL;
        stone10.entity_type_id = entityId;
        stone10.delay_ticks = delay;
        stone10.time_to_live_seconds = toLiveSeconds;
        stone10.placement.location_offset_by_look = offset;
        stone10.placement.location_yaw_offset = -64;
        stone10.placement.apply_yaw = true;
        var stone11 = new Spell.Impact.Action.Spawn();
        stone11.intent = SpellTarget.Intent.HARMFUL;
        stone11.entity_type_id = entityId;
        stone11.delay_ticks = delay;
        stone11.time_to_live_seconds = toLiveSeconds;
        stone11.placement.location_offset_by_look = offset;
        stone11.placement.location_yaw_offset = -96;
        stone11.placement.apply_yaw = true;
        var stone12 = new Spell.Impact.Action.Spawn();
        stone12.intent = SpellTarget.Intent.HARMFUL;
        stone12.entity_type_id = entityId;
        stone12.delay_ticks = delay;
        stone12.time_to_live_seconds = toLiveSeconds;
        stone12.placement.location_offset_by_look = offset;
        stone12.placement.location_yaw_offset = -128;
        stone12.placement.apply_yaw = true;
        var stone13 = new Spell.Impact.Action.Spawn();
        stone13.intent = SpellTarget.Intent.HARMFUL;
        stone13.entity_type_id = entityId;
        stone13.delay_ticks = delay;
        stone13.time_to_live_seconds = toLiveSeconds;
        stone13.placement.location_offset_by_look = offset;
        stone13.placement.location_yaw_offset = -160;
        stone13.placement.apply_yaw = true;

        spawn.action.spawns = List.of(stone1,stone2,stone3,stone4,stone5,stone6,stone7,
                stone8,stone9,stone10,stone11,stone12,stone13);
        spell.impacts = List.of(spawn);

        SpellBuilder.Cost.cooldown(spell, 22);
        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/terra_drip_circle_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{terra_circle_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            return desc;
        };
        return new Entry(id, spell, name, description).book(Book.TERRA).mutator(mutator);
    }
    public static final Entry terra_shattering_stone = add(terra_shattering_stone());
    private static Entry terra_shattering_stone() {
        var id = Identifier.of(MOD_ID, "terra_shattering_stone");
        var title = "Shattering Stone";
        var description = "Launch a stone that shatters on impact, sending fragments in all directions that cause {damage} damage and bleeding wounds.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 30;
        spell.tier = 3;
        spell.group = GEOMANCING;

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_impact1"), 0.5F);
        spell.active.cast.particles = terraCastingParticles(spell);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");
        spell.release.sound = Sound.withVolume(Identifier.of("block.pointed_dripstone.fall"), 1.5F);

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.2F;

        var projectile = new Spell.ProjectileData();
        projectile.divergence = 0;
        projectile.perks = new Spell.ProjectileData.Perks();
        projectile.perks.chain_reaction_size = 6;
        projectile.perks.chain_reaction_increment = -1;
        projectile.perks.chain_reaction_triggers = 3;
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[]{};
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.single("elemental_wizards_rpg:spell_projectile/stone_shard", 0.5F);

        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(0.7F, 0);
        damage.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_impact1"), 0.5F);

        var bleeding = SpellBuilder.Impacts.effectSet(SpellEngineEffects.BLEED.id.toString(), 5,1);
        bleeding.action.status_effect.amplifier_power_multiplier = 0.2F;
        bleeding.action.status_effect.amplifier_cap = 2;
        bleeding.action.status_effect.show_particles = false;
        bleeding.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5.0F, 0.02F, 0.07F)
        };

        spell.impacts = List.of(damage, bleeding);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 1.5F;
        spell.area_impact.area = new Spell.Target.Area();
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.NONE;
        spell.area_impact.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:stone_explosion",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.6F, 1.0F)
        };
        spell.area_impact.sound = Sound.withVolume(Identifier.of("block.pointed_dripstone.break"), 1.5F);

        SpellBuilder.Cost.exhaust(spell, 0.4F);
        SpellBuilder.Cost.cooldown(spell, 18);
        spell.cost.cooldown.haste_affected = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:terra_stone", 1);

        return new Entry(id, spell, title, description).book(Book.TERRA);
    }
    public static final Entry terra_earthquake = add(terra_earthquake());
    private static Entry terra_earthquake() {
        var id = Identifier.of(MOD_ID, "terra_earthquake");
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 8;
        spell.tier = 4;
        spell.group = GEOMANCING;
        var title = "Earthquake";
        var description = "Charge a powerful Earthquake that deals {eq_damage} damage to all targets on the ground - the longer it's held, the bigger it gets.";

        var charge = SpellBuilder.Casting.charge(spell, 2.0F);
        charge.min_release_ratio = 0.25F;
        charge.bonus.range_add = 16.0F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        10, 0.001F, 0.1F),
                new ParticleBatch(
                        "more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        3, 0.01F, 0.05F).extent(1.0F)
        };
        spell.active.cast.sound = Sound.withVolume(Identifier.of("more_rpg_classes:earth_magic_impact2"),0.5F);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");

        int delay = 0;
        int toLiveSeconds = 5;
        String entityId = "elemental_wizards_rpg:earthquake";
        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        var eqspawn = new Spell.Impact.Action.Spawn();
        eqspawn.entity_type_id = entityId;
        eqspawn.delay_ticks = delay;
        eqspawn.time_to_live_seconds = toLiveSeconds;
        eqspawn.placement.apply_yaw = true;
        spawn.action.spawns = List.of(eqspawn);

        spell.impacts = List.of(spawn);


        SpellBuilder.Cost.cooldown(spell, 35);
        SpellBuilder.Cost.exhaust(spell, 0.5F);
        SpellBuilder.Cost.item(spell,"more_rpg_classes:terra_stone",1);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/terra_earthquake_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{eq_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            return desc;
        };
        return new Entry(id, spell, title, description).book(Book.TERRA).mutator(mutator);
    }
    public static final Entry terra_earth_golem_spike_line = add(terra_earth_golem_spike_line());
    private static Entry terra_earth_golem_spike_line() {
        var id = Identifier.of(MOD_ID, "terra_earth_golem_spike_line");
        var title = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 8;
        spell.tier = 4;
        spell.target.type = Spell.Target.Type.CASTER;

        int spikeCount = 12;
        float spacing = 1.0F;
        var spikes = new ArrayList<Spell.Impact.Action.Spawn>(spikeCount);
        for (int i = 0; i < spikeCount; i++) {
            var spike = new Spell.Impact.Action.Spawn();
            spike.entity_type_id = "elemental_wizards_rpg:earth_golem_spike";
            spike.delay_ticks = i * 2;
            spike.placement.apply_yaw = true;
            spike.placement.location_offset_by_look = spacing * (i + 1);
            spike.placement.force_onto_ground = true;
            spikes.add(spike);
        }

        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        spawn.action.spawns = spikes;

        spell.impacts = List.of(spawn);

        return new Entry(id, spell, title, description);
    }
    public static final Entry terra_earth_golem = add(terra_earth_golem());
    private static Entry terra_earth_golem() {
        var id = Identifier.of(MOD_ID, "terra_earth_golem");
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 3;
        spell.tier = 4;
        spell.group = STONESHAPING;
        var title = "Earth Golem";
        var description = "Summons an Earth Golem that smashes the ground around it, hurls stones at range, and calls damaging spikes that deal {golem_spike_damage} damage and knock up on contact.";

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_channeling");
        spell.active.cast.particles = terraCastingParticles(spell);
        spell.active.cast.sound = Sound.withVolume(Identifier.of(MRPGLibSounds.EARTH_MAGIC_CAST_1.id().toString()),0.5F);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_ground_release");

        var summonImpact = new Spell.Impact();
        summonImpact.action = new Spell.Impact.Action();
        summonImpact.action.type = Spell.Impact.Action.Type.SUMMON;
        summonImpact.action.summon = ElementalSummons.earthGolem();
        summonImpact.sound = new Sound(ElementalSounds.EARTH_SUMMON.id().toString());

        spell.impacts = List.of(summonImpact);

        SpellBuilder.Cost.cooldown(spell, 35);
        spell.cost.cooldown.haste_affected = false;
        SpellBuilder.Cost.exhaust(spell, 0.5F);
        SpellBuilder.Cost.item(spell,"more_rpg_classes:terra_stone",1);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/terra_earth_golem_spike_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{golem_spike_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            return desc;
        };
        return new Entry(id, spell, title, description).book(Book.TERRA).mutator(mutator);
    }
    public static final Entry wind_gust = add(wind_gust());
    private static Entry wind_gust() {
        var id = Identifier.of(MOD_ID, "wind_gust");
        var title = "Gust";
        var description = "Conjure a quick gust of wind that pushes enemies back with moderate force and deals {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.group = "primary";
        spell.range = 16;
        spell.tier = 0;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound("spell_engine:generic_wind_charging");
        spell.active.cast.particles = windCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        var damage = damageImpact(0.65F, 0.75F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("gust",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.5F, 0.8F)
        };
        damage.sound = new Sound("more_rpg_classes:air_magic_impact3");

        spell.impacts = List.of(damage);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description);
    }
    public static final Entry wind_air_cutter = add(wind_air_cutter());
    private static Entry wind_air_cutter() {
        var id = Identifier.of(MOD_ID, "wind_air_cutter");
        var title = "Air Cutter";
        var description = "Slash the air to create sharp wind blades that cut through enemies with considerable knockback and deal {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.group = "primary";
        spell.range = 16;
        spell.tier = 1;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.sound = new Sound("spell_engine:generic_wind_charging");
        spell.active.cast.particles = windCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        var damage = damageImpact(0.75F, 1.25F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        25.0F, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        15.0F, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:small_gust",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                        5.0F, 0.05F, 0.2F)
        };
        damage.sound = new Sound("more_rpg_classes:air_magic_impact3");

        spell.impacts = List.of(damage);

        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).weaponGroup(WeaponGroup.WIND_STAFF).weaponGroup(WeaponGroup.ELEMENTAL_STAFF);
    }
    public static final Entry wind_aeroblast = add(wind_aeroblast());
    private static Entry wind_aeroblast() {
        var id = Identifier.of(MOD_ID, "wind_aeroblast");
        var title = "Aeroblast";
        var description = "Unleash a powerful blast of concentrated air that launches enemies skyward and deals {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 20;
        spell.tier = 2;
        spell.group = COMBO;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.sound = new Sound("spell_engine:generic_wind_charging");
        spell.active.cast.particles = windCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");

        var damage = damageImpact(0.8F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:wind_vacuum",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        1.0F, 0.1F, 1.0F, 0),
                new ParticleBatch("gust",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.5F, 0.8F)
        };
        damage.sound = new Sound("more_rpg_classes:air_magic_impact2");

        var knockUp = new Spell.Impact();
        knockUp.action = new Spell.Impact.Action();
        knockUp.action.type = Spell.Impact.Action.Type.VELOCITY;
        knockUp.action.velocity = new Spell.Impact.Action.Velocity();
        knockUp.action.velocity.frame = Spell.Impact.Action.Velocity.Frame.ORIGIN;
        knockUp.action.velocity.push = new Vector3f(0F, 0.7F, 0F);
        knockUp.action.velocity.power_coefficient = 0.075F;
        knockUp.action.velocity.intent = SpellTarget.Intent.HARMFUL;
        bossImmuneDeny(knockUp);

        var debuff = SpellBuilder.Impacts.effectSet(StatusEffects.SLOW_FALLING.getIdAsString(),1.0F,0);
        debuff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();

        spell.impacts = List.of(damage, knockUp, debuff);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 8);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).book(Book.WIND);
    }
    public static final Entry wind_twister = add(wind_twister());
    private static Entry wind_twister() {
        var id = Identifier.of(MOD_ID, "wind_twister");
        var title = "Twister";
        var description = "Launch a circling-moving whirlwind, that knock's back targets it hits and damages them by {twister_damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 22;
        spell.tier = 2;
        spell.group = PRESSURE;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.0F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_charge");
        spell.active.cast.sound = new Sound(MRPGLibSounds.AIR_MAGIC_CAST_1.id().toString());
        spell.active.cast.particles = windCastingParticles(spell);

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        int delay = 0;
        int toLiveSeconds = 7;
        String entityId = "elemental_wizards_rpg:whirlwind";
        var spawn = new Spell.Impact();
        spawn.action = new Spell.Impact.Action();
        spawn.action.type = Spell.Impact.Action.Type.SPAWN;
        var twister = new Spell.Impact.Action.Spawn();
        twister.entity_type_id = entityId;
        twister.delay_ticks = delay;
        twister.time_to_live_seconds = toLiveSeconds;
        twister.placement.apply_yaw = true;
        spawn.action.spawns = List.of(twister);

        spell.impacts = List.of(spawn);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 12);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var world = args.player().getWorld();
            if (world == null) return args.description();
            var optional = SpellRegistry.from(world).getEntry(Identifier.of(MOD_ID, "helper/wind_twister_impact"));
            if (optional.isEmpty()) return args.description();
            var estimated = SpellHelper.estimate(optional.get().value(), args.player(), ItemStack.EMPTY);
            var desc = args.description();
            if (!estimated.damage().isEmpty()) {
                var dmg = estimated.damage().get(0);
                desc = desc.replace("{twister_damage}", SpellTooltip.formattedRange(dmg.min(), dmg.max()));
            }
            return desc;
        };

        return new Entry(id, spell, title, description).book(Book.WIND).mutator(mutator);
    }
    public static final Entry wind_updraft = add(wind_updraft());
    private static Entry wind_updraft() {
        var id = Identifier.of(MOD_ID, "wind_updraft");
        var title = "Updraft";
        var description = "Create a powerful updraft that lifts enemies into the air, leaving them vulnerable while suspended and dealing {damage} damage.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 20;
        spell.tier = 3;
        spell.group = COMBO;
        spell.learn = new Spell.Learn();


        SpellBuilder.Casting.channel(spell, 2, 10);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_sky_charge");
        spell.active.cast.particles = windCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        spell.release = new Spell.Release();

        var damage = damageImpact(0.75F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("gust",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.5F, 0.8F),
                new ParticleBatch("spell_engine:smoke_medium",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        ParticleBatch.Rotation.LOOK,
                        15.0F, 0.1F, 0.1F, 0)
        };
        damage.sound = new Sound("spell_engine:generic_wind_charging");

        var updraft = createEffectImpact(Identifier.of(MOD_ID, "updraft"), 2);
        updraft.action.status_effect.amplifier = 0;
        updraft.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        updraft.action.status_effect.show_particles = false;
        updraft.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        updraft.action.status_effect.apply_limit.health_base = 50;
        updraft.action.status_effect.apply_limit.spell_power_multiplier = 3.0F;
        bossImmuneDeny(updraft);

        spell.impacts = List.of(damage, updraft);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 20);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).book(Book.WIND);
    }
    public static final Entry wind_windfield = add(wind_windfield());
    private static Entry wind_windfield() {
        var id = Identifier.of(MOD_ID, "wind_windfield");
        var title = "Windfield";
        var description = "Calls a field of strong wind that deals {damage} damage and reduces movement speed by {bonus} for {effect_duration} sec.";
        var effect = ElementalEffects.WINDFIELD;

        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };


        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 16;
        spell.tier = 3;
        spell.group = PRESSURE;
        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.cast(spell,1,"spell_engine:one_handed_projectile_charge");
        spell.active.cast.particles = windCastingParticles(spell);
        spell.active.cast.sound = new Sound(MRPGLibSounds.AIR_MAGIC_CAST_1.id().toString());

        spell.release = new Spell.Release();
        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_projectile_release");

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        var damage = damageImpact(0.8F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        ParticleBatch.Rotation.LOOK,
                        25.0F, 0.4F, 0.8F, 0).color(Color.WHITE.toRGBA())
        };
        damage.sound = new Sound(MRPGLibSounds.AIR_MAGIC_IMPACT_3.id().toString());

        var debuff = SpellBuilder.Impacts.effectSet(effect.id.toString(),5,0);
        debuff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        debuff.action.status_effect.apply_limit.health_base = 65;
        debuff.action.status_effect.apply_limit.spell_power_multiplier = 2F;

        spell.impacts = List.of(damage,debuff);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 3.0F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.area_impact.particles = new ParticleBatch[] {
                new ParticleBatch(
                        SpellEngineParticles.smoke_large.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.3F, 0.8F).color(Color.WHITE.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.3F, 0.8F).extent(0.5F).color(Color.WHITE.toRGBA())
        };

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 20);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).book(Book.WIND).mutator(mutator);
    }
    public static final Entry wind_tornado = add(wind_tornado());
    private static Entry wind_tornado() {
        var id = Identifier.of(MOD_ID, "wind_tornado");
        var title = "Tornado";
        var description = "Summons a devastating tornado, pulling and lifting enemies while dealing {damage} damage to them.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 20;
        spell.tier = 4;
        spell.group = COMBO;
        spell.learn = new Spell.Learn();

        spell.active.cast.duration = 1.5F;
        spell.active.cast.movement_speed = 0;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_sky_charge");
        spell.active.cast.sound = new Sound("spell_engine:generic_wind_charging");
        spell.active.cast.particles = windCastingParticles(spell);

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;
        spell.target.aim.sticky = true;
        spell.target.aim.use_caster_as_fallback = true;

        spell.release = new Spell.Release();

        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        var cloud = new Spell.Delivery.Cloud();
        cloud.presence_sound = new Sound(ElementalSounds.TORNADO_IDLE.id().toString());
        cloud.volume.radius = 5.0F;
        cloud.volume.extra_radius.power_coefficient = 0.02F;
        cloud.volume.extra_radius.power_cap = 50F;
        cloud.volume.area.vertical_range_multiplier = 1.5F;
        cloud.impact_tick_interval = 5;
        cloud.time_to_live_seconds = 7;
        cloud.spawn_ticks = 25;
        cloud.despawn_ticks = 15;
        var tornadoTotalTicks = cloud.spawn_ticks + Math.round(cloud.time_to_live_seconds * 20F) + cloud.despawn_ticks;
        cloud.client_data = new Spell.Delivery.Cloud.ClientData();
        cloud.client_data.model_fx = List.of(
                ModelEffectBuilder.create("elemental_wizards_rpg:spell_effect/tornado")
                        .scale(3.5F)
                        .light(LightEmission.RADIATE)
                        .duration(tornadoTotalTicks)
                        .scaleIn(0, cloud.spawn_ticks, ModelEffect.Easing.EASE_OUT_CUBIC)
                        .scaleOut(tornadoTotalTicks - cloud.despawn_ticks, tornadoTotalTicks, ModelEffect.Easing.EASE_IN_CUBIC)
                        .rotate(0, 25F * tornadoTotalTicks, 0, 0, tornadoTotalTicks, ModelEffect.Easing.LINEAR)
                        .build()
        );
        cloud.placement.apply_yaw = true;
        spell.deliver.clouds = List.of(cloud);

        var damage = damageImpact(0.2F, 0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_mist",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        ParticleBatch.Rotation.LOOK,
                        0.2F, 0.1F, 0.1F, 0),
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        ParticleBatch.Rotation.LOOK,
                        1.5F, 0.5F, 1.0F, 0)
        };
        damage.sound = Sound.withVolume(Identifier.of(MRPGLibSounds.AIR_MAGIC_IMPACT_2.id().toString()), 0.25F);

        var pull = new Spell.Impact();
        pull.action = new Spell.Impact.Action();
        pull.action.type = Spell.Impact.Action.Type.VELOCITY;
        pull.action.velocity = new Spell.Impact.Action.Velocity();
        pull.action.velocity.frame = Spell.Impact.Action.Velocity.Frame.ORIGIN;
        pull.action.velocity.push = new Vector3f(0.08F, 0.28F, -0.35F);
        pull.action.velocity.power_coefficient = 0.02F;
        pull.action.velocity.intent = SpellTarget.Intent.HARMFUL;
        bossImmuneDeny(pull);

        spell.impacts = List.of(damage, pull);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 30);
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).book(Book.WIND);
    }
    public static final Entry wind_stormdraft = add(wind_stormdraft());
    private static Entry wind_stormdraft() {
        var id = Identifier.of(MOD_ID, "wind_stormdraft");
        var title = "Storm Draft";
        var description = "Channel bursts of high pressure air that deal {damage} damage and shortly stun enemies.";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 26;
        spell.tier = 4;
        spell.group = PRESSURE;
        spell.learn = new Spell.Learn();

        SpellBuilder.Casting.channel(spell,5,5);
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:two_handed_channeling");
        spell.active.cast.particles = windCastingParticles(spell);
        spell.active.cast.sound = new Sound(ElementalSounds.STORM_DRAFT_LAUNCH.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.release = new Spell.Release();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 2.2F;

        var projectile = new Spell.ProjectileData();
        projectile.client_data = new Spell.ProjectileData.Client();
        projectile.client_data.travel_particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:wind_vacuum",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        3.0F, 0.1F, 0.1F),
                new ParticleBatch("minecraft:small_gust",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        2.0F, 0.05F, 0.05F)
        };
        spell.deliver.projectile.projectile = projectile;

        var damage = damageImpact(1.1F, 1.0F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:wind_vacuum",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        1.0F, 0.1F, 1.0F, 0),
                new ParticleBatch("gust",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.5F, 0.8F)
        };
        damage.sound = new Sound(MRPGLibSounds.AIR_MAGIC_IMPACT_2.id().toString());
        var stun = SpellBuilder.Impacts.stun(1.5F);

        spell.impacts = List.of(damage, stun);

        SpellBuilder.Cost.exhaust(spell, 0.3F);
        SpellBuilder.Cost.cooldown(spell, 36);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.item(spell, "more_rpg_classes:storm_stone", 1);

        return new Entry(id, spell, title, description).book(Book.WIND);
    }
    public static final Entry aqua_healing_rain_impact = add(aqua_healing_rain_impact());
    private static Entry aqua_healing_rain_impact() {
        var id = Identifier.of(MOD_ID, "helper/aqua_healing_rain_impact");
        var title = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 6;
        spell.tier = 3;
        spell.learn = new Spell.Learn();

        var heal = createHeal(0.5F);
        heal.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:water_heal",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        5, 0.05F, 0.1F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F)
        };
        heal.sound = Sound.withVolume(Identifier.of("spell_engine:generic_healing_impact_2"), 1.2F);

        var damage = damageImpact(0.7F, 0.2F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        25, 0.3F, 0.7F).extent(0.75F),
                new ParticleBatch("more_rpg_classes:splash",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        5.0F, 0.05F, 0.2F),
                new ParticleBatch("more_rpg_classes:water_circle",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        0.1F, 0.2F, 1.0F)
        };
        damage.sound = Sound.withVolume(Identifier.of("more_rpg_classes:water_magic_impact1"), 0.4F);

        spell.impacts = List.of(heal, damage);

        return new Entry(id, spell, title, description);
    }
    public static final Entry aqua_tidal_wave_impact = add(aqua_tidal_wave_impact());
    private static Entry aqua_tidal_wave_impact() {
        var id = Identifier.of(MOD_ID, "helper/aqua_tidal_wave_impact");
        var title = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.WATER;
        spell.range = 6;
        spell.tier = 3;
        spell.learn = new Spell.Learn();

        var damage = damageImpact(1.0F, 1.0F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:big_splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        25, 0.3F, 0.7F).extent(0.75F),
                new ParticleBatch("more_rpg_classes:splash",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        5.0F, 0.05F, 0.2F)
        };
        damage.sound = new Sound(ElementalSounds.TIDAL_WAVE_IMPACT.id().toString());

        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description);
    }
    public static Entry terra_drip_circle_impact = add(terra_drip_circle_impact());
    private static Entry terra_drip_circle_impact() {
        var id = Identifier.of(MOD_ID, "helper/terra_drip_circle_impact");
        var name = "";
        var description = "";
        var debuffEffect = SpellEngineEffects.BLEED;

        var spell = SpellBuilder.createSpellActive();
        spell.range = 15;
        spell.tier = 3;
        spell.school = MoreSpellSchools.EARTH;

        var damage = SpellBuilder.Impacts.damage(0.65F, 0F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F)
        };
        damage.sound = new Sound(MRPGLibSounds.EARTH_MAGIC_IMPACT_1.id().toString());

        var debuff = createEffectImpact(debuffEffect.id, 4);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier_power_multiplier = 0.2F;
        debuff.action.status_effect.amplifier_cap = 2;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F)
        };

        spell.impacts = List.of(damage, debuff);

        return new Entry(id, spell, name, description);
    }
    public static Entry terra_earth_golem_spike_impact = add(terra_earth_golem_spike_impact());
    private static Entry terra_earth_golem_spike_impact() {
        var id = Identifier.of(MOD_ID, "helper/terra_earth_golem_spike_impact");
        var name = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.range = 12;
        spell.tier = 3;
        spell.school = MoreSpellSchools.EARTH;

        var damage = SpellBuilder.Impacts.damage(0.8F, 0.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F)
        };
        damage.sound = new Sound("block.pointed_dripstone.break");

        var knockUp = new Spell.Impact();
        knockUp.action = new Spell.Impact.Action();
        knockUp.action.type = Spell.Impact.Action.Type.VELOCITY;
        knockUp.action.velocity = new Spell.Impact.Action.Velocity();
        knockUp.action.velocity.frame = Spell.Impact.Action.Velocity.Frame.ORIGIN;
        knockUp.action.velocity.push = new Vector3f(0F, 0.15F, 0F);
        knockUp.action.velocity.power_coefficient = 0.05F;
        knockUp.action.velocity.intent = SpellTarget.Intent.HARMFUL;
        bossImmuneDeny(knockUp);

        spell.impacts = List.of(damage, knockUp);

        return new Entry(id, spell, name, description);
    }
    public static Entry terra_earth_golem_slam_impact = add(terra_earth_golem_slam_impact());
    private static Entry terra_earth_golem_slam_impact() {
        var id = Identifier.of(MOD_ID, "helper/terra_earth_golem_slam_impact");
        var name = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.range = 3;
        spell.tier = 3;
        spell.school = MoreSpellSchools.EARTH;

        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        50, 0.2F, 0.3F),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        50, 0.2F, 0.3F).extent(1.0F),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        50, 0.2F, 0.3F).extent(2.5F),
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        var damage = SpellBuilder.Impacts.damage(0.5F, 1.5F);
        damage.particles = new ParticleBatch[] {
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        0.25F, 0.0001F, 0.0008F)
        };
        damage.sound = new Sound("block.pointed_dripstone.break");

        spell.impacts = List.of(damage);

        return new Entry(id, spell, name, description);
    }
    public static final Entry terra_earthquake_impact = add(terra_earthquake_impact());
    private static Entry terra_earthquake_impact() {
        var id = Identifier.of(MOD_ID, "helper/terra_earthquake_impact");
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.EARTH;
        spell.range = 16;
        spell.tier = 4;
        var title = "";
        var description = "";

        var damage = damageImpact(0.8F,0);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "campfire_cosy_smoke",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        5, 0.005F, 0.01F)
        };
        damage.sound = new Sound("block.pointed_dripstone.break");
        var custom = new Spell.Impact();
        bossImmuneDeny(custom);
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "more_rpg_classes:trembling";

        spell.impacts = List.of(damage, custom);

        return new Entry(id, spell, title, description);
    }
    public static final Entry wind_twister_impact = add(wind_twister_impact());
    private static Entry wind_twister_impact() {
        var id = Identifier.of(MOD_ID, "helper/wind_twister_impact");
        var title = "";
        var description = "";

        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.AIR;
        spell.range = 20;
        spell.tier = 4;
        spell.learn = new Spell.Learn();

        var damage = damageImpact(0.7F, 1.2F);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:wind_vacuum",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        ParticleBatch.Rotation.LOOK,
                        1.0F, 0.1F, 1.0F, 0),
                new ParticleBatch("gust",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        1.0F, 0.5F, 0.8F)
        };
        damage.sound = new Sound(MRPGLibSounds.AIR_MAGIC_IMPACT_1.id().toString());


        spell.impacts = List.of(damage);

        return new Entry(id, spell, title, description);
    }
    public static Entry avatar_passives_air_draft = add(avatar_passives_air_draft());
    private static Entry avatar_passives_air_draft() {
        var id = Identifier.of(MOD_ID, "avatar_passives/air_draft");
        var title = "";
        var description = "Wind: Deals {damage} damage around the caster and lets them levitate for {effect_duration} sec.";
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
        return new Entry(id, spell, title, description);
    }
    public static Entry avatar_passives_earth_stoning = add(avatar_passives_earth_stoning());
    private static Entry avatar_passives_earth_stoning() {
        var id = Identifier.of(MOD_ID, "avatar_passives/earth_stoning");
        var title = "";
        var description = "Earth: Summons falling stones from the sky that deal {damage} damage.";
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
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.single("elemental_wizards_rpg:spell_projectile/spell_stone", 0.4F);

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
        return new Entry(id, spell, title, description);
    }
    public static final Entry avatar_passives_water_undercurrent= add(avatar_passives_water_undercurrent());
    private static Entry avatar_passives_water_undercurrent() {
        var id = Identifier.of(MOD_ID, "avatar_passives/water_undercurrent");
        var title = "";
        var description = "Water: Creates a Water zone, that deals {damage} damage and heals allies for {heal}.";
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
        damage.sound = new Sound("more_rpg_classes:water_magic_impact1");
        damage.particles = new ParticleBatch[]{
                new ParticleBatch(
                        "more_rpg_classes:splash",
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        20, 0, 0)
        };

        var heal = createHeal(0.2F);
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
        return new Entry(id, spell, title, description);
    }
}
