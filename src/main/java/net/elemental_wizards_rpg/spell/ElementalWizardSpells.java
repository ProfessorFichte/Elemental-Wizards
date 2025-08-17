package net.elemental_wizards_rpg.spell;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
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
    private static Spell passiveSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.PASSIVE;
        spell.passive = new Spell.Passive();
        return spell;
    }
    ///PASSIVES
    public static Entry elemental_avatar = add(elemental_avatar());
    private static Entry elemental_avatar() {
        var id = Identifier.of(MOD_ID, "elemental_avatar");
        var title = "";
        var description = "";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.GENERIC;
        spell.range = 64F;

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.chance = 1F;
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

        configureCooldown(spell, 5);

        return new Entry(id, spell, title, description, null);
    }
}
