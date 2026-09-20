package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.spell_engine.api.effect.*;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.ElementalMod.tweaksConfig;

public class ElementalEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Effects.Entry CLEANSING_WATER = add(new Effects.Entry(
            new Identifier(MOD_ID, "cleansing_water"),
            "Cleansing Water",
            "Holy water, extinguishing fire and regenerating health.",
            new CleansingWaterEffect(StatusEffectCategory.BENEFICIAL, MoreSpellSchools.WATER.color),
            new EffectConfig(List.of())
    ));
    public static final Effects.Entry BUBBLE_FOAM = add(new Effects.Entry(
            new Identifier(MOD_ID, "bubble_foam"),
            "Bubble Shield",
            "On contact protecting the player and pushing targets away.",
            new BubbleFoamEffect(StatusEffectCategory.BENEFICIAL, MoreSpellSchools.WATER.color),
            new EffectConfig(List.of())
    ));
    public static final Effects.Entry STONE_FLESH = add(new Effects.Entry(
            new Identifier(MOD_ID, "stone_flesh"),
            "Stone Flesh",
            "Gives the user armor and armor toughness, if you have all hearts, the next attack will get reduced by 50%.",
            new StoneFleshEffect(StatusEffectCategory.BENEFICIAL, MoreSpellSchools.EARTH.color),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ARMOR),
                            1.0F,
                            EntityAttributeModifier.Operation.ADDITION
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ARMOR_TOUGHNESS),
                            0.25F,
                            EntityAttributeModifier.Operation.ADDITION
                    )
            ))
    ));
    public static final Effects.Entry UPDRAFT = add(new Effects.Entry(
            new Identifier(MOD_ID, "updraft"),
            "Updraft",
            "Keeps the target in the air and makes the target more vulnerable to air spell damage, if its in the air.",
            new UpdraftEffect(StatusEffectCategory.HARMFUL, MoreSpellSchools.AIR.color),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_SPEED),
                            -0.25F,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            ))
    ));
    public static final Effects.Entry IMPALED = add(new Effects.Entry(
            new Identifier(MOD_ID, "impaled"),
            "Impaled",
            "The Target gets Impaled and trapped.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, MoreSpellSchools.EARTH.color),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                                    -10,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));
    public static final Effects.Entry WINDFIELD = add(new Effects.Entry(
            new Identifier(MOD_ID, "windfield"),
            "Windfield",
            "Reduces the Movement Speed.",
            new UpdraftEffect(StatusEffectCategory.HARMFUL, MoreSpellSchools.AIR.color),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            -0.7F,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            ))
    ));


    private static String attributeId(EntityAttribute attribute) {
        return Registries.ATTRIBUTE.getId(attribute).toString();
    }

    public static StatusEffect getEntry(Effects.Entry entry) {
        return entry.effect;
    }

    public static void register(ConfigFile.Effects config) {
        effectsToRegister(config).forEach((id, effect) ->
                Registry.register(Registries.STATUS_EFFECT, id, effect));
        Effects.linkEntries(entries);
    }

    public static Map<Identifier, StatusEffect> effectsToRegister(ConfigFile.Effects config) {
        ((UpdraftEffect) UPDRAFT.effect).setVulnerability(
                MoreSpellSchools.AIR,
                new SpellPower.Vulnerability(
                        tweaksConfig.value.updraft_air_damage_vulnerability,
                        tweaksConfig.value.updraft_air_spell_crit_chance_vulnerability,
                        tweaksConfig.value.updraft_air_spell_crit_damage_vulnerability
                )
        );
        for (var entry : entries) {
            Synchronized.configure(entry.effect, true);
        }
        return Effects.effectsToRegister(entries, config.effects);
    }
}
