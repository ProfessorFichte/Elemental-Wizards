package net.elemental_wizards_rpg.datagen;

import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.more_rpg_classes.datagen.BetterCombatWeaponAttributeGenerator;

import java.util.concurrent.CompletableFuture;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class WeaponAttributesGenerator implements DataProvider {
    private final FabricDataOutput output;

    public WeaponAttributesGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        try {
            BetterCombatWeaponAttributeGenerator.generateBetterCombatWeaponAttributes(
                    output.getPath(),
                    MOD_ID,
                    WeaponsRegister.entries,
                    "wand",
                    "bettercombat:wand"
            );
            BetterCombatWeaponAttributeGenerator.generateBetterCombatWeaponAttributes(
                    output.getPath(),
                    MOD_ID,
                    WeaponsRegister.entries,
                    "staff",
                    "bettercombat:staff"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate weapon attributes", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Elemental Wizards Weapon Attributes";
    }
}
