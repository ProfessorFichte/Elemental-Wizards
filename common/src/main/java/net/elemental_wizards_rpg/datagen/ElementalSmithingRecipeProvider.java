package net.elemental_wizards_rpg.datagen;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.datagen.SmithingRecipeGenerator;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

public class ElementalSmithingRecipeProvider extends SmithingRecipeGenerator {

    public ElementalSmithingRecipeProvider(FabricDataOutput output) {
        super(output, MOD_ID);
    }

    @Override
    public void generate() {
        // ==========================================
        // NETHERITE UPGRADES (No mod dependencies)
        // ==========================================

        // Netherite Kelp Armor Set
        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.kelpArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteKelpNetheriteArmor.armorSet()
        );

        // Netherite Dripstone Armor Set
        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.dripstoneArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteDripstoneArmor.armorSet()
        );

        // Netherite Wind Armor Set
        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.windArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteWindArmor.armorSet()
        );
        // Netherite Weapons
        createSimpleSmithingRecipe(
                "smithing_netherite_aqua_wand",
                WeaponsRegister.aquaWand.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteAquaWand.item()
        );
        createSimpleSmithingRecipe(
                "smithing_netherite_terra_wand",
                WeaponsRegister.terraWand.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteTerraWand.item()
        );
        createSimpleSmithingRecipe(
                "smithing_netherite_wind_wand",
                WeaponsRegister.windWand.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteWindWand.item()
        );
        createSimpleSmithingRecipe(
                "smithing_netherite_aqua_staff",
                WeaponsRegister.aquaStaff.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteAquaStaff.item()
        );
        createSimpleSmithingRecipe(
                "smithing_netherite_terra_staff",
                WeaponsRegister.terraStaff.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteTerraStaff.item()
        );
        createSimpleSmithingRecipe(
                "smithing_netherite_wind_staff",
                WeaponsRegister.windStaff.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netheriteWindStaff.item()
        );


        // ==========================================
        // ARMORY RPG UPGRADES (With mod load conditions)
        // ==========================================

        // Only generate these recipes if armory_rpgs is loaded
        if (armoryLoadCheck()) {
            // Hurricane Robe upgrades (from Wind armor)
            createArmorSetUpgrade(
                    "smithing_wind",
                    Armors.windArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.hurricaneArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // Hurricane Robe upgrades (from Netherite Wind armor)
            createArmorSetUpgrade(
                    "smithing_netherite_wind",
                    Armors.netheriteWindArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.hurricaneArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // Mountain Robe upgrades (from Dripstone armor)
            createArmorSetUpgrade(
                    "smithing_dripstone",
                    Armors.dripstoneArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.mountainArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // Mountain Robe upgrades (from Netherite Dripstone armor)
            createArmorSetUpgrade(
                    "smithing_netherite_dripstone",
                    Armors.netheriteDripstoneArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.mountainArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // Ocean Robe upgrades (from Kelp armor)
            createArmorSetUpgrade(
                    "smithing_kelp",
                    Armors.kelpArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.oceanArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // Ocean Robe upgrades (from Netherite Kelp armor)
            createArmorSetUpgrade(
                    "smithing_netherite_kelp",
                    Armors.netheriteKelpNetheriteArmor.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.oceanArmorSet.armorSet(),
                    "armory_rpgs"
            );
        }
    }
}
