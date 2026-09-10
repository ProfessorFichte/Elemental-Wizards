package net.elemental_wizards_rpg.item.armor;

import net.elemental_wizards_rpg.item.ElementalGroup;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.more_rpg_classes.item.MRPGCItemGroups;
import net.spell_engine.rpg_series.config.ArmorSetConfig;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.api.item.SpellItemData;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;


import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

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
    private static final float spell_power_t5 = 0.35F;

    private static final float haste_t2 = 0.02F;
    private static final float haste_t3 = 0.03F;
    private static final float haste_t5 = 0.03F;

    private static final float crit_damage_t2 = 0.05F;
    private static final float crit_damage_t3 = 0.06F;
    private static final float crit_damage_t5 = 0.08F;

    private static final float crit_chance_t2 = 0.02F;
    private static final float crit_chance_t3 = 0.03F;
    private static final float crit_chance_t5 = 0.03F;

    /// 1.20.1: `ArmorMaterial` is a plain interface - no registry, no `Layer` list. SpellEngine's
    /// `Armor.material(...)` builds a `CustomMaterial` whose `id` doubles as the (single) layer id,
    /// so `textures/models/armor/<name>_layer_{1,2}.png` keeps working unchanged.
    public static ArmorMaterial material(String name,
                                         int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                         int enchantability, SoundEvent equipSound, Supplier<Ingredient> repairIngredient) {
        return Armor.material(
                new Identifier(MOD_ID, name),
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                0, 0);
    }

    public static ArmorMaterial material_elemental = material(
            "elemental",
            1, 3, 2, 1,
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);

    public static ArmorMaterial material_kelp = material(
            "kelp",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);

    public static ArmorMaterial material_dripstone = material(
            "dripstone",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);
    public static ArmorMaterial material_wind = material(
            "wind",
            1, 3, 2, 1,
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WOOL_INGREDIENTS);
    public static ArmorMaterial material_netherite_kelp = material(
            "netherite_kelp",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static ArmorMaterial material_netherite_dripstone = material(
            "netherite_dripstone",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static ArmorMaterial material_netherite_wind = material(
            "netherite_wind",
            1, 3, 2, 1,
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static ArmorMaterial epic_wizard_robe = material(
            "wizard_robe",
            1, 3, 2, 1,
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });


    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(ArmorMaterial material, Identifier id, int durability,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier, Armor.ItemSettingsTweaker settings) {
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

    public static final Map<Armor.Entry, RegistryKey<ItemGroup>> groupOverrides = new IdentityHashMap<>();

    private static Armor.Entry groupKey(Armor.Entry entry, RegistryKey<ItemGroup> key) {
        groupOverrides.put(entry, key);
        return entry;
    }

    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        // 1.20.1 has no data components: the equipment set rides SpellEngine's NBT-backed item defaults,
        // and rarity is a plain `Item.Settings` property again.
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            SpellItemData.defaults(itemSettings).equipmentSet(equipmentSetId);
            itemSettings.rarity(Rarity.RARE);
        });
    }

    public static final Armor.Entry elementalArmor =
            create(
                    material_elemental,
                    new Identifier(MOD_ID, "elemental"),
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
                    ),1,null)
                    .translatedName("Elemental Apprentice Hat", "Elemental Apprentice Robe Top", "Elemental Apprentice Robe Bottom", "Elemental Apprentice Boots");

    public static final Armor.Entry kelpArmor =
            create(
                    material_kelp,
                    new Identifier(MOD_ID, "kelp"),
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
                    ),2,null)
                    .translatedName("Aqua Hat", "Aqua Robe Top", "Aqua Robe Bottom", "Aqua Boots");

    public static final Armor.Entry dripstoneArmor =
            create(
                    material_dripstone,
                    new Identifier(MOD_ID, "dripstone"),
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
                    ),2,null)
                    .translatedName("Terra Hat", "Terra Robe Top", "Terra Robe Bottom", "Terra Boots");

    public static final Armor.Entry windArmor =
            create(
                    material_wind,
                    new Identifier(MOD_ID, "wind"),
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
                    ),2,null)
                    .translatedName("Wind Hat", "Wind Robe Top", "Wind Robe Bottom", "Wind Boots");

    public static final Armor.Entry netheriteKelpNetheriteArmor =
            create(
                    material_netherite_kelp,
                    new Identifier(MOD_ID, "netherite_kelp"),
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
                    ),3,null)
                    .translatedName("Netherite Aqua Hat", "Netherite Aqua Robe Top", "Netherite Aqua Robe Bottom", "Netherite Aqua Boots");

    public static final Armor.Entry netheriteDripstoneArmor =
            create(
                    material_netherite_dripstone,
                    new Identifier(MOD_ID, "netherite_dripstone"),
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
                    ),3,null)
                    .translatedName("Netherite Terra Hat", "Netherite Terra Robe Top", "Netherite Terra Robe Bottom", "Netherite Terra Boots");

    public static final Armor.Entry netheriteWindArmor =
            create(
                    material_netherite_wind,
                    new Identifier(MOD_ID, "netherite_wind"),
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
                    ),3,null)
                    .translatedName("Netherite Wind Hat", "Netherite Wind Robe Top", "Netherite Wind Robe Bottom", "Netherite Wind Boots");
    public static Armor.Entry oceanArmorSet;
    public static Armor.Entry hurricaneArmorSet;
    public static Armor.Entry mountainArmorSet;
    public static Identifier hurricane_passive = new Identifier(MOD_ID, "hurricane");
    public static Identifier mountain_passive = new Identifier(MOD_ID, "mountain");
    public static Identifier ocean_passive = new Identifier(MOD_ID, "ocean");

    /// Whether the `armoryLoadCheck()`-gated entries below have already been appended to {@link #entries}.
    /// {@link #itemsToRegister} is idempotent, so the conditional half must be too.
    private static boolean conditionalEntriesAdded = false;

    public static void register(Map<String, ArmorSetConfig> configs) {
        itemsToRegister(configs)
                .forEach((id, item) -> Registry.register(Registries.ITEM, id, item));
    }

    /// Creation only — appends the Armory-gated epic sets and returns every armor piece keyed by the id it
    /// registers under. A loader that registers items itself (Forge) iterates this instead of calling
    /// {@link #register}. **Must run inside the ITEM registration window.**
    ///
    /// Calling `Armor.itemsToRegister(...)` directly from a loader entrypoint would silently drop the three
    /// epic sets — they are not in {@link #entries} until this method has run.
    public static Map<Identifier, Item> itemsToRegister(Map<String, ArmorSetConfig> configs) {
        addConditionalEntries();
        return Armor.itemsToRegister(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }

    private static void addConditionalEntries() {
        if (conditionalEntriesAdded) { return; }
        conditionalEntriesAdded = true;
        if (armoryLoadCheck()) {
            hurricaneArmorSet  = groupKey(create(
                    epic_wizard_robe,
                    new Identifier(MOD_ID, "hurricane_robe"),
                    40,
                    Armor.CustomItem::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t5)),
                            new ArmorSetConfig.Piece(3)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t5)),
                            new ArmorSetConfig.Piece(2)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t5)),
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.AIR.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t5))
                    ), 5,commonSettings(hurricane_passive))
                    .translatedName("Hurricane Hat", "Hurricane Robe Top", "Hurricane Robe Bottom", "Hurricane Boots"), MRPGCItemGroups.ARMORY_KEY);
            mountainArmorSet = groupKey(create(
                    epic_wizard_robe,
                    new Identifier(MOD_ID, "mountain_robe"),
                    40,
                    Armor.CustomItem::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t5)),
                            new ArmorSetConfig.Piece(3)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t5)),
                            new ArmorSetConfig.Piece(2)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t5)),
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.EARTH.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t5))
                    ),5,
                    commonSettings(mountain_passive))
                    .translatedName("Mountain Hat", "Mountain Robe Top", "Mountain Robe Bottom", "Mountain Boots"), MRPGCItemGroups.ARMORY_KEY);
            oceanArmorSet = groupKey(create(
                    epic_wizard_robe,
                    new Identifier(MOD_ID, "ocean_robe"),
                    40,
                    Armor.CustomItem::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t5)),
                            new ArmorSetConfig.Piece(3)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t5)),
                            new ArmorSetConfig.Piece(2)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t5)),
                            new ArmorSetConfig.Piece(1)
                                    .add(AttributeModifier.multiply(MoreSpellSchools.WATER.id, spell_power_t5))
                                    .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t5))
                    ),5,
                    commonSettings(ocean_passive))
                    .translatedName("Ocean Hat", "Ocean Robe Top", "Ocean Robe Bottom", "Ocean Boots"), MRPGCItemGroups.ARMORY_KEY);
        }
    }
}
