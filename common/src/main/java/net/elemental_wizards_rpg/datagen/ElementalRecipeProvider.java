package net.elemental_wizards_rpg.datagen;

import net.elemental_wizards_rpg.item.ElementalItems;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalRecipeProvider extends RecipeProvider {

    public ElementalRecipeProvider(FabricDataOutput output) {
        super(output, MOD_ID);
    }

    @Override
    public void generate() {
        // ==========================================
        // MISC
        // ==========================================

        createShapedRecipe("elemental_essence",
                ElementalItems.ELEMENTAL_ESSENCE,
                2,
                new String[]{" A ", "BED", " C "},
                "A", Items.KELP,
                "B", Items.CLAY_BALL,
                "C", Items.FEATHER,
                "D", Items.COAL,
                "E", Items.LAPIS_LAZULI
        );

        // ==========================================
        // ELEMENTAL ARMOR SET
        // ==========================================

        createShapedRecipe("elemental_head",
                Armors.elementalArmor.armorSet().head,
                1,
                new String[]{"AAA", "ABA", "   "},
                "A", ElementalItems.ELEMENTAL_ESSENCE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("elemental_chest",
                Armors.elementalArmor.armorSet().chest,
                1,
                new String[]{"A A", "BBB", "AAA"},
                "A", ElementalItems.ELEMENTAL_ESSENCE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("elemental_legs",
                Armors.elementalArmor.armorSet().legs,
                1,
                new String[]{"AAA", "B B", "A A"},
                "A", ElementalItems.ELEMENTAL_ESSENCE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("elemental_feet",
                Armors.elementalArmor.armorSet().feet,
                1,
                new String[]{"   ", "A A", "B B"},
                "A", ElementalItems.ELEMENTAL_ESSENCE,
                "B", ItemTags.WOOL
        );

        // ==========================================
        // KELP ARMOR SET
        // ==========================================

        createShapedRecipe("kelp_head",
                Armors.kelpArmor.armorSet().head,
                1,
                new String[]{"AAA", "ABA", "   "},
                "A", Items.KELP,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("kelp_chest",
                Armors.kelpArmor.armorSet().chest,
                1,
                new String[]{"A A", "CAC", "BBB"},
                "A", Items.KELP,
                "B", ItemTags.WOOL,
                "C", Items.PRISMARINE_CRYSTALS
        );

        createShapedRecipe("kelp_legs",
                Armors.kelpArmor.armorSet().legs,
                1,
                new String[]{"AAA", "B B", "A A"},
                "A", Items.KELP,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("kelp_feet",
                Armors.kelpArmor.armorSet().feet,
                1,
                new String[]{"   ", "A A", "B B"},
                "A", Items.KELP,
                "B", ItemTags.WOOL
        );

        // ==========================================
        // DRIPSTONE ARMOR SET
        // ==========================================

        createShapedRecipe("dripstone_head",
                Armors.dripstoneArmor.armorSet().head,
                1,
                new String[]{"AAA", "ABA", "   "},
                "A", Items.POINTED_DRIPSTONE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("dripstone_chest",
                Armors.dripstoneArmor.armorSet().chest,
                1,
                new String[]{"A A", "CAC", "BBB"},
                "A", Items.POINTED_DRIPSTONE,
                "B", ItemTags.WOOL,
                "C", Items.DRIPSTONE_BLOCK
        );

        createShapedRecipe("dripstone_legs",
                Armors.dripstoneArmor.armorSet().legs,
                1,
                new String[]{"AAA", "B B", "A A"},
                "A", Items.POINTED_DRIPSTONE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("dripstone_feet",
                Armors.dripstoneArmor.armorSet().feet,
                1,
                new String[]{"   ", "A A", "B B"},
                "A", Items.POINTED_DRIPSTONE,
                "B", ItemTags.WOOL
        );

        // ==========================================
        // WIND ARMOR SET
        // ==========================================

        createShapedRecipe("wind_head",
                Armors.windArmor.armorSet().head,
                1,
                new String[]{"AAA", "ABA", "   "},
                "A", Items.WIND_CHARGE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("wind_chest",
                Armors.windArmor.armorSet().chest,
                1,
                new String[]{"A A", "CAC", "BBB"},
                "A", Items.GOLD_INGOT,
                "B", ItemTags.WOOL,
                "C", Items.WIND_CHARGE
        );

        createShapedRecipe("wind_legs",
                Armors.windArmor.armorSet().legs,
                1,
                new String[]{"AAA", "B B", "A A"},
                "A", Items.WIND_CHARGE,
                "B", ItemTags.WOOL
        );

        createShapedRecipe("wind_feet",
                Armors.windArmor.armorSet().feet,
                1,
                new String[]{"   ", "A A", "B B"},
                "A", Items.WIND_CHARGE,
                "B", ItemTags.WOOL
        );

        // ==========================================
        // WANDS - TIER 0 (Basic)
        // ==========================================

        createShapedRecipe("wand_kelp",
                WeaponsRegister.kelpWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.KELP,
                "B", Items.STICK
        );

        createShapedRecipe("wand_clay",
                WeaponsRegister.clayWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.CLAY_BALL,
                "B", Items.STICK
        );

        createShapedRecipe("wand_feather",
                WeaponsRegister.featherWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.FEATHER,
                "B", Items.STICK
        );

        // ==========================================
        // WANDS - TIER 2 (Advanced)
        // ==========================================

        createShapedRecipe("wand_aqua",
                WeaponsRegister.aquaWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.GLOW_INK_SAC,
                "B", new Item[]{Items.GOLD_INGOT}
        );

        createShapedRecipe("wand_terra",
                WeaponsRegister.terraWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.RAW_IRON,
                "B", new Item[]{Items.IRON_INGOT}
        );

        createShapedRecipe("wand_wind",
                WeaponsRegister.windWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.PHANTOM_MEMBRANE,
                "B", new Item[]{Items.GOLD_INGOT}
        );

        // ==========================================
        // WANDS - TIER 3 (Netherite)
        // ==========================================

        createShapedRecipe("wand_netherite_aqua",
                WeaponsRegister.netheriteAquaWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.PRISMARINE_CRYSTALS,
                "B", new Item[]{Items.NETHERITE_INGOT}
        );

        createShapedRecipe("wand_netherite_terra",
                WeaponsRegister.netheriteTerraWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.OBSIDIAN,
                "B", new Item[]{Items.NETHERITE_INGOT}
        );

        createShapedRecipe("wand_netherite_wind",
                WeaponsRegister.netheriteWindWand.item(),
                1,
                new String[]{" A", "B "},
                "A", Items.WIND_CHARGE,
                "B", new Item[]{Items.NETHERITE_INGOT}
        );

        // ==========================================
        // STAFFS - TIER 1 (Elemental)
        // ==========================================

        createShapedRecipe("staff_elemental",
                WeaponsRegister.elementalStaff.item(),
                1,
                new String[]{" CA", " BC", "B  "},
                "A", Items.NETHER_STAR,
                "B", Items.STICK,
                "C", ElementalItems.ELEMENTAL_ESSENCE
        );

        // ==========================================
        // STAFFS - TIER 2 (Specialized)
        // ==========================================

        createShapedRecipe("staff_aqua",
                WeaponsRegister.aquaStaff.item(),
                1,
                new String[]{" CA", " BC", "A  "},
                "A", Items.PRISMARINE_CRYSTALS,
                "B", Items.GOLD_INGOT,
                "C", net.minecraft.registry.tag.TagKey.of(
                        net.minecraft.registry.RegistryKeys.ITEM,
                        Identifier.of("more_rpg_classes", "coral_plants")
                )
        );

        createShapedRecipe("staff_terra",
                WeaponsRegister.terraStaff.item(),
                1,
                new String[]{" CA", " BC", "A  "},
                "A", Items.DIAMOND,
                "B", Items.GOLD_INGOT,
                "C", Items.OBSIDIAN
        );

        createShapedRecipe("staff_wind",
                WeaponsRegister.windStaff.item(),
                1,
                new String[]{" CA", " BC", "A  "},
                "A", Items.PHANTOM_MEMBRANE,
                "B", Items.GOLD_INGOT,
                "C", Items.WIND_CHARGE
        );

        // ==========================================
        // STAFFS - TIER 3 (Netherite)
        // ==========================================

        createShapedRecipe("staff_netherite_aqua",
                WeaponsRegister.netheriteAquaStaff.item(),
                1,
                new String[]{" BA", " CB", "C  "},
                "A", Items.PRISMARINE_CRYSTALS,
                "B", Items.HEART_OF_THE_SEA,
                "C", Items.NETHERITE_INGOT
        );

        createShapedRecipe("staff_netherite_terra",
                WeaponsRegister.netheriteTerraStaff.item(),
                1,
                new String[]{" BA", " CB", "C  "},
                "A", Items.DIAMOND,
                "B", Items.OBSIDIAN,
                "C", Items.NETHERITE_INGOT
        );

        createShapedRecipe("staff_netherite_wind",
                WeaponsRegister.netheriteWindStaff.item(),
                1,
                new String[]{" BA", " CB", "C  "},
                "A", Items.PHANTOM_MEMBRANE,
                "B", Items.WIND_CHARGE,
                "C", Items.NETHERITE_INGOT
        );

        // ==========================================
        // STAFFS - TIER 4 (Better End)
        // ==========================================

        createConditionalShapedRecipe("staff_crystal_aqua",
                WeaponsRegister.entries.stream()
                        .filter(e -> e.id().getPath().equals("staff_crystal_aqua"))
                        .findFirst()
                        .map(e -> e.item())
                        .orElse(Items.STICK),
                1,
                new String[]{" BA", " CB", "C  "},
                "betterend",
                "A", Items.PRISMARINE_CRYSTALS,
                "B", Identifier.of("betterend", "crystal_shards"),
                "C", Identifier.of("betterend", "aeternium_ingot")
        );

        createConditionalShapedRecipe("staff_aeternium_wind",
                WeaponsRegister.entries.stream()
                        .filter(e -> e.id().getPath().equals("staff_aeternium_wind"))
                        .findFirst()
                        .map(e -> e.item())
                        .orElse(Items.STICK),
                1,
                new String[]{" BA", " CB", "C  "},
                "betterend",
                "A", Items.PHANTOM_MEMBRANE,
                "B", Identifier.of("betterend", "crystal_shards"),
                "C", Identifier.of("betterend", "aeternium_ingot")
        );

        // ==========================================
        // STAFFS - TIER 4 (Better Nether)
        // ==========================================

        createConditionalShapedRecipe("staff_ruby_terra",
                WeaponsRegister.entries.stream()
                        .filter(e -> e.id().getPath().equals("staff_ruby_terra"))
                        .findFirst()
                        .map(e -> e.item())
                        .orElse(Items.STICK),
                1,
                new String[]{" BA", " CB", "C  "},
                "betternether",
                "A", Identifier.of("betternether", "nether_ruby"),
                "B", Items.OBSIDIAN,
                "C", Items.GILDED_BLACKSTONE
        );
    }
}
