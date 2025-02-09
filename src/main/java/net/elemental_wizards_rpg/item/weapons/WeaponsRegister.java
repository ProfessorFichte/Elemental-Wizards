package net.elemental_wizards_rpg.item.weapons;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        return entry(null, name, material, item, defaults);
    }

    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        var entry = new Weapon.Entry(ElementalMod.MOD_ID, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = new Identifier(idString);
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

    //WANDS
    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;
    private static Weapon.Entry wand(String name, Weapon.CustomMaterial material,boolean fireproof) {
        var settings = new Item.Settings();
        if (fireproof) {
            settings = settings.fireproof();
        }
        var item = new StaffItem(material, settings);
        return entry(name, material, item, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }
    public static final Weapon.Entry kelpWand = wand("wand_kelp",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 3));
    public static final Weapon.Entry aquaWand = wand("wand_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 4));
    public static final Weapon.Entry netheriteAquaWand = wand("wand_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 4.5F));

    public static final Weapon.Entry clayWand = wand("wand_clay",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 3));
    public static final Weapon.Entry terraWand = wand("wand_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 4));
    public static final Weapon.Entry netheriteTerraWand = wand("wand_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 4.5F));

    public static final Weapon.Entry featherWand = wand("wand_feather",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 3));
    public static final Weapon.Entry windWand = wand("wand_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 4));
    public static final Weapon.Entry netheriteWindWand = wand("wand_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 4.5F));

    //STAFFS
    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;
    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material,boolean fireproof) {
        return staff(null, name, material, fireproof);
    }
    private static Weapon.Entry staff(String requiredMod, String name, Weapon.CustomMaterial material,boolean fireproof) {
        var settings = new Item.Settings();
        if (fireproof) {
            settings = settings.fireproof();
        }
        var item = new StaffItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(staffAttackDamage, staffAttackSpeed));
    }
    public static final Weapon.Entry elementalStaff= staff("staff_elemental",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.STICK)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 4))
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 4))
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 4))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.FIRE.id, 4))
            ;

    public static final Weapon.Entry aquaStaff= staff("staff_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 5));
    public static final Weapon.Entry netheriteAquaStaff = staff("staff_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 6))
            ;

    public static final Weapon.Entry terraStaff= staff("staff_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 5));
    public static final Weapon.Entry netheriteTerraStaff = staff("staff_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 6))
            ;

    public static final Weapon.Entry windStaff= staff("staff_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)),false)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 5));
    public static final Weapon.Entry netheriteWindStaff = staff("staff_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),true)
            .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 6))
            ;

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String AETHER = "aether";
    //Registration
    public static void register(Map<String,ItemConfig.Weapon> configs) {
        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            staff(BETTER_NETHER, "staff_ruby_terra",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),true)
                    .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.EARTH.id, 7))
            ;
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            staff(BETTER_END, "staff_crystal_aqua",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),true)
                    .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, 7))
            ;
            staff(BETTER_END, "staff_aeternium_wind",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),true)
                    .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.AIR.id, 7))
            ;
        }

        Weapon.register(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }
}
