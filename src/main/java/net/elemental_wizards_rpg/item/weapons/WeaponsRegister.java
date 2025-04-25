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
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults) {
        return entry(null, name, material, factory, defaults);
    }

    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, Equipment.WeaponType.DAMAGE_STAFF);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
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

    //WANDS
    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;
    private static Weapon.Entry wand(String name, Weapon.CustomMaterial material) {
        return entry(name, material, StaffItem::new, new WeaponConfig(wandAttackDamage, wandAttackSpeed));
    }

    public static final Weapon.Entry kelpWand = wand("wand_kelp",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 3))
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry aquaWand = wand("wand_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 4))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteAquaWand = wand("wand_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 4.5F))
            .loot(Equipment.LootProperties.of(3));

    public static final Weapon.Entry clayWand = wand("wand_clay",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 3))
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry terraWand = wand("wand_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 4))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteTerraWand = wand("wand_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 4.5F))
            .loot(Equipment.LootProperties.of(3));

    public static final Weapon.Entry featherWand = wand("wand_feather",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 3))
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry windWand = wand("wand_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 4))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteWindWand = wand("wand_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 4.5F))
            .loot(Equipment.LootProperties.of(3));

    //STAFFS
    private static final float staffAttackDamage = 4;
    private static final float staffAttackSpeed = -3F;
    private static Weapon.Entry staff(String name, Weapon.CustomMaterial material) {
        return staff(null, name, material);
    }

    private static Weapon.Entry staff(String requiredMod, String name, Weapon.CustomMaterial material) {
        return entry(requiredMod, name, material, StaffItem::new, new WeaponConfig(staffAttackDamage, staffAttackSpeed));
    }
    public static final Weapon.Entry elementalStaff= staff("staff_elemental",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.STICK)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 4))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 4))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 4))
            .attribute(AttributeModifier.bonus(SpellSchools.FIRE.id, 4))
            .loot(Equipment.LootProperties.of(1));

    public static final Weapon.Entry aquaStaff= staff("staff_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 5))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteAquaStaff = staff("staff_netherite_aqua",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 6))
            .loot(Equipment.LootProperties.of(3));

    public static final Weapon.Entry terraStaff= staff("staff_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 5))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteTerraStaff = staff("staff_netherite_terra",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 6))
            .loot(Equipment.LootProperties.of(3));

    public static final Weapon.Entry windStaff= staff("staff_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.GOLD_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 5))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netheriteWindStaff = staff("staff_netherite_wind",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)))
            .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 6))
            .loot(Equipment.LootProperties.of(3));

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String AETHER = "aether";
    //Registration
    public static void register(Map<String,WeaponConfig> configs) {
        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            staff("betternether", "staff_ruby_terra",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 7))
                    .loot(Equipment.LootProperties.of(4));
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            staff("betterend", "staff_crystal_aqua",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 7))
                    .loot(Equipment.LootProperties.of(4));
            staff("betterend", "staff_aeternium_wind",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 7))
                    .loot(Equipment.LootProperties.of(4));
        }
        if(FabricLoader.getInstance().isModLoaded(AETHER) || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            staff(AETHER, "staff_aether",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.EARTH.id, 7))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, 7))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.AIR.id, 7))
                    .loot(Equipment.LootProperties.of("aether"));
        }

        Weapon.register(configs, entries, ElementalGroup.ELEMENTAL_WIZARD_KEY);
    }
}
