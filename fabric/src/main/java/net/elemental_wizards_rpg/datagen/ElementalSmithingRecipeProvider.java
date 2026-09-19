package net.elemental_wizards_rpg.datagen;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

/// Uses the local {@link SmithingRecipeProvider} rather than More RPG Library's
/// `SmithingRecipeGenerator`: the library's copy still writes the 1.21 shapes
/// (`data/<ns>/recipe/`, `"result": {"id": ...}`, `neoforge:conditions`), which 1.20.1 cannot read.
public class ElementalSmithingRecipeProvider extends SmithingRecipeProvider {

    public ElementalSmithingRecipeProvider(FabricDataOutput output) {
        super(output, MOD_ID);
    }

    @Override
    public void generate() {
        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.kelpArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteKelpNetheriteArmor.armorSet()
        );

        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.dripstoneArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteDripstoneArmor.armorSet()
        );

        createSimpleArmorSetUpgrade(
                "netherite",
                Armors.windArmor.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteWindArmor.armorSet()
        );
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

        if (armoryLoadCheck()) {
            createArmorSetUpgrade(
                    "smithing_wind",
                    Armors.windArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.hurricaneArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_netherite_wind",
                    Armors.netheriteWindArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.hurricaneArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_dripstone",
                    Armors.dripstoneArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.mountainArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_netherite_dripstone",
                    Armors.netheriteDripstoneArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.mountainArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_kelp",
                    Armors.kelpArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.oceanArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_netherite_kelp",
                    Armors.netheriteKelpNetheriteArmor.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "warden_upgrade_crystal"),
                    Armors.oceanArmorSet.armorSet(),
                    "armory_rpgs"
            );
        }
    }
}
