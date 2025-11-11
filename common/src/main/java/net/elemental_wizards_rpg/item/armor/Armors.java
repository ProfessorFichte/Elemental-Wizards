package net.elemental_wizards_rpg.item.armor;

import net.elemental_wizards_rpg.item.ElementalGroup;
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
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class Armors {
    private static final Supplier<Ingredient> WOOL_INGREDIENTS = () -> { return Ingredient.ofItems(
            Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL, Items.YELLOW_WOOL,
            Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL, Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL,
            Items.PURPLE_WOOL, Items.BLUE_WOOL, Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL
    );
    };

    private static final float spell_power_t1 = 0.2F;
    private static final float spell_power_t2 = 0.25F;
    private static final float spell_power_t3 = 0.3F;

    private static final float haste_t2 = 0.02F;
    private static final float haste_t3 = 0.03F;
    private static final float haste_t4 = 0.03F;

    private static final float crit_damage_t2 = 0.05F;
    private static final float crit_damage_t3 = 0.06F;
    private static final float crit_damage_t4 = 0.08F;

    private static final float crit_chance_t2 = 0.02F;
    private static final float crit_chance_t3 = 0.03F;
    private static final float crit_chance_t4 = 0.03F;

    public static RegistryEntry<ArmorMaterial> material(String name,
                                                        int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
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

    public static RegistryEntry<ArmorMaterial> material_elemental = material(
            "elemental",
            1, 3, 2, 1,
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_kelp = material(
            "kelp",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);

    public static RegistryEntry<ArmorMaterial> material_dripstone = material(
            "dripstone",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);
    public static RegistryEntry<ArmorMaterial> material_wind = material(
            "wind",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);
    public static RegistryEntry<ArmorMaterial> material_netherite_kelp = material(
            "netherite_kelp",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> material_netherite_dripstone = material(
            "netherite_dripstone",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> material_netherite_wind = material(
            "netherite_wind",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });


    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier)
        );
        entries.add(entry);
        return entry;
    }

    public static final Armor.Set elementalArmor =
            create(
                    material_elemental,
                    Identifier.of(MOD_ID, "elemental"),
                    10,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t1),
                                            AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t1),
                                            AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t1),
                                            AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t1),
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t1),
                                            AttributeModifier.multiply(SpellSchools.FIRE.id, spell_power_t1)
                                    ))
                    ),1)
                    .armorSet();

    public static final Armor.Set kelpArmor =
            create(
                    material_kelp,
                    Identifier.of(MOD_ID, "kelp"),
                    20,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t2)
                                    ))
                    ),2)
                    .armorSet();

    public static final Armor.Set dripstoneArmor =
            create(
                    material_dripstone,
                    Identifier.of(MOD_ID, "dripstone"),
                    20,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t2)
                                    ))
                    ),2)
                    .armorSet();

    public static final Armor.Set windArmor =
            create(
                    material_wind,
                    Identifier.of(MOD_ID, "wind"),
                    20,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t2),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t2)
                                    ))
                    ),2)
                    .armorSet();

    public static final Armor.Set netheriteKelpNetheriteArmor =
            create(
                    material_netherite_kelp,
                    Identifier.of(MOD_ID, "netherite_kelp"),
                    30,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)
                                    ))
                    ),3)
                    .armorSet();

    public static final Armor.Set netheriteDripstoneArmor =
            create(
                    material_netherite_dripstone,
                    Identifier.of(MOD_ID, "netherite_dripstone"),
                    30,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.EARTH.id,  spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)
                                    ))
                    ),3)
                    .armorSet();

    public static final Armor.Set netheriteWindArmor =
            create(
                    material_netherite_wind,
                    Identifier.of(MOD_ID, "netherite_wind"),
                    30,
                    ElementalRobe::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t3),
                                            AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)
                                    ))
                    ),3)
                    .armorSet();

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }
}
