package net.elemental_wizards_rpg.item.weapons;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType category) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, category);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
            entry.loot(Equipment.LootProperties.of(""));
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;
    private static final float T0_WAND_POWER = 3F;
    private static final float T1_WAND_POWER = 4F;
    private static final float T2_WAND_POWER = 5F;
    private static final float T3_WAND_POWER = 5.5F;
    private static final float T1_STAFF_POWER = 5F;
    private static final float T2_STAFF_POWER = 6F;
    private static final float T3_STAFF_POWER = 7F;
    private static final float T4_STAFF_POWER = 8F;

    private static Weapon.Entry wand(String name, Weapon.CustomMaterial material) {
        return entry(name, material, StaffItem::new, new WeaponConfig(wandAttackDamage, wandAttackSpeed), Equipment.WeaponType.DAMAGE_WAND);
    }

    public static final Weapon.Entry kelpWand = wand("wand_kelp",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T0_WAND_POWER))
            .loot(Equipment.LootProperties.of(0))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_splash.id()))
            .translatedName("Kelp Wand");
    public static final Weapon.Entry aquaWand = wand("wand_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_splash.id()))
            .translatedName("Aqua Wand");
    public static final Weapon.Entry netheriteAquaWand = wand("wand_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_splash.id()))
            .translatedName("Netherite Aqua Wand");

    public static final Weapon.Entry clayWand = wand("wand_clay",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T0_WAND_POWER))
            .loot(Equipment.LootProperties.of(0))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_throw.id()))
            .translatedName("Clay Wand");
    public static final Weapon.Entry terraWand = wand("wand_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_throw.id()))
            .translatedName("Terra Wand");
    public static final Weapon.Entry netheriteTerraWand = wand("wand_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_throw.id()))
            .translatedName("Netherite Terra Wand");

    public static final Weapon.Entry featherWand = wand("wand_feather",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T0_WAND_POWER))
            .loot(Equipment.LootProperties.of(0))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_gust.id()))
            .translatedName("Feather Wand");
    public static final Weapon.Entry windWand = wand("wand_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T2_WAND_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_gust.id()))
            .translatedName("Wind Wand");
    public static final Weapon.Entry netheriteWindWand = wand("wand_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T3_WAND_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_gust.id()))
            .translatedName("Netherite Wind Wand");

    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;
    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material) {
        return entry(name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed), Equipment.WeaponType.DAMAGE_STAFF);
    }
    public static final Weapon.Entry elementalStaff= staff("staff_elemental",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T1_STAFF_POWER))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T1_STAFF_POWER))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T1_STAFF_POWER))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, T1_STAFF_POWER))
            .loot(Equipment.LootProperties.of(1))
            .spellContainer(SpellContainers.forMagicWeapon())
            .withSpellChoices("elemental_wizards_rpg:weapon/elemental_staff")
            .translatedName("Elemental Staff");

    public static final Weapon.Entry aquaStaff= staff("staff_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_water_whip.id()))
            .translatedName("Aqua Staff");
    public static final Weapon.Entry netheriteAquaStaff = staff("staff_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_water_whip.id()))
            .translatedName("Netherite Aqua Staff");

    public static final Weapon.Entry terraStaff= staff("staff_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_spear.id()))
            .translatedName("Terra Staff");
    public static final Weapon.Entry netheriteTerraStaff = staff("staff_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_spear.id()))
            .translatedName("Netherite Terra Staff");

    public static final Weapon.Entry windStaff= staff("staff_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T2_STAFF_POWER))
            .loot(Equipment.LootProperties.of(2))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_air_cutter.id()))
            .translatedName("Wind Staff");
    public static final Weapon.Entry netheriteWindStaff = staff("staff_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T3_STAFF_POWER))
            .loot(Equipment.LootProperties.of(3))
            .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_air_cutter.id()))
            .translatedName("Netherite Wind Staff");

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String AETHER = "aether";
    private static final String ARSENAL = "arsenal";
    public static void register(Map<String,WeaponConfig> configs) {
        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            staff( "staff_ruby_terra",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.terra_stone_spear.id()))
                    .translatedName("Ruby Terra Staff");
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            staff( "staff_crystal_aqua",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.aqua_water_whip.id()))
                    .translatedName("Crystal Aqua Staff");
            staff( "staff_aeternium_wind",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of(4))
                    .spellContainer(SpellContainers.forMagicWeapon().withSpellId(ElementalWizardSpells.wind_air_cutter.id()))
                    .translatedName("Aeternium Wind Staff");
        }
        if(FabricLoader.getInstance().isModLoaded(AETHER) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            staff( "staff_aether",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T4_STAFF_POWER))
                    .loot(Equipment.LootProperties.of("aether"))
                    .spellContainer(SpellContainers.forMagicWeapon())
                    .withSpellChoices("elemental_wizards_rpg:weapon/elemental_staff")
                    .translatedName("Valkyrie Elementalist Staff");
        }
        if (FabricLoader.getInstance().isModLoaded(ARSENAL) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            staff( "unique_staff_1",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, T4_STAFF_POWER))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, T4_STAFF_POWER))
                    .spellContainer(SpellContainers.forMagicWeapon())
                    .withSpellChoices("elemental_wizards_rpg:weapon/elemental_staff")
                    .withAdditionalSpell(ElementalWizardSpells.elemental_avatar.id().toString())
                    .loot(Equipment.LootProperties.of(5, "crystal"))
                    .translatedName("Avatar's Staff")
                    .rarity = Rarity.RARE;
        }

        Weapon.register(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }
}
