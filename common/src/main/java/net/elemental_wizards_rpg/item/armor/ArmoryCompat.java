package net.elemental_wizards_rpg.item.armor;

import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_power.api.SpellPowerMechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ArmoryCompat {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, int tier,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, Armor.ItemSettingsTweaker settings) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier),
                settings
        );
        entries.add(entry);
        return entry;
    }

    public static RegistryEntry<ArmorMaterial> material(
            String name, int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
            int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {

        var material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(MOD_ID, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(MOD_ID, name), material);
    }
    public static Identifier hurricane_passive = Identifier.of(MOD_ID, "hurricane");
    public static Identifier mountain_passive = Identifier.of(MOD_ID, "mountain");
    public static Identifier ocean_passive = Identifier.of(MOD_ID, "ocean");

    public static RegistryEntry<ArmorMaterial> wizard_robe = material(
            "wizard_robe",
            1, 3, 2, 1,
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    private static final float caster_spell_power = 0.35F;
    private static final float crit_damage_t3 = 0.1F;
    private static final float crit_chance_t3 = 0.02F;
    private static final float haste_t3 = 0.03F;

    public static final int durability = 40;

    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            itemSettings
                    .component(SpellDataComponents.EQUIPMENT_SET, equipmentSetId)
                    .component(DataComponentTypes.RARITY, Rarity.RARE);
        });
    }

    public static final Armor.Entry hurricane = create(
            wizard_robe,
            Identifier.of(MOD_ID, "hurricane_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3))
            ),
            commonSettings(hurricane_passive))
            .translatedName("", "", "", "");
    public static final Armor.Entry mountain = create(
            wizard_robe,
            Identifier.of(MOD_ID, "mountain_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3))
            ),
            commonSettings(mountain_passive))
            .translatedName("", "", "", "");
    public static final Armor.Entry ocean = create(
            wizard_robe,
            Identifier.of(MOD_ID, "ocean_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3))
            ),
            commonSettings(ocean_passive))
            .translatedName("", "", "", "");

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }
}
