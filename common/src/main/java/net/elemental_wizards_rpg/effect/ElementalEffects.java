package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
import net.spell_engine.api.effect.*;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.ElementalMod.tweaksConfig;

public class ElementalEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Effects.Entry CLEANSING_WATER = add(new Effects.Entry(
            Identifier.of(MOD_ID, "cleansing_water"),
            "Cleansing Water",
            "Holy water, extinguishing fire and regenerating health. If the target is full health, cleansing negative effects on applying the effect.",
            new CleansingWaterEffect(StatusEffectCategory.BENEFICIAL, 0x01d9cf),
            new EffectConfig(List.of())
    ));

    public static final Effects.Entry BUBBLE_FOAM = add(new Effects.Entry(
            Identifier.of(MOD_ID, "bubble_foam"),
            "Bubble Shield",
            "On contact protecting the player and pushing targets away.",
            new BubbleFoamEffect(StatusEffectCategory.BENEFICIAL, 0x01d9cf),
            new EffectConfig(List.of())
    ));

    public static final Effects.Entry STONE_FLESH = add(new Effects.Entry(
            Identifier.of(MOD_ID, "stone_flesh"),
            "Stone Flesh",
            "Gives the user armor and armor toughness, if you have all hearts, the next attack will get reduced by 50%.",
            new StoneFleshEffect(StatusEffectCategory.BENEFICIAL, 0xbd8b00),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ARMOR.getIdAsString(),
                            1.0F,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ARMOR_TOUGHNESS.getIdAsString(),
                            0.5F,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            ))
    ));

    public static final Effects.Entry UPDRAFT = add(new Effects.Entry(
            Identifier.of(MOD_ID, "updraft"),
            "Updraft",
            "Keeps the target in the air and makes the target more vulnerable to air spell damage, if its in the air.",
            new UpdraftEffect(StatusEffectCategory.HARMFUL, 0xd5ebff),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                            -0.25F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            ))
    ));

    public static final Effects.Entry TORNADO = add(new Effects.Entry(
            Identifier.of(MOD_ID, "tornado"),
            "Tornado",
            "Makes the target more vulnerable to fire spell damage, sucks it in the nearest tornado and knocks the target up.",
            new TornadoEffect(StatusEffectCategory.HARMFUL, 0xd5ebff),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            -0.99F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            ))
    ));

    public static RegistryEntry<StatusEffect> getEntry(Effects.Entry entry) {
        return Registries.STATUS_EFFECT.getEntry(entry.id).orElseThrow();
    }

    public static void register(ConfigFile.Effects config) {
        ((UpdraftEffect) UPDRAFT.effect).setVulnerability(
                MoreSpellSchools.AIR,
                new SpellPower.Vulnerability(
                        tweaksConfig.value.updraft_air_damage_vulnerability,
                        tweaksConfig.value.updraft_air_spell_crit_chance_vulnerability,
                        tweaksConfig.value.updraft_air_spell_crit_damage_vulnerability
                )
        );

        ((TornadoEffect) TORNADO.effect).setVulnerability(
                SpellSchools.FIRE,
                new SpellPower.Vulnerability(
                        tweaksConfig.value.tornado_fire_spell_vulnerability, 0, 0
                )
        );

        for (var entry : entries) {
            Synchronized.configure(entry.effect, true);
        }
        Effects.register(entries, config.effects);
    }
}
