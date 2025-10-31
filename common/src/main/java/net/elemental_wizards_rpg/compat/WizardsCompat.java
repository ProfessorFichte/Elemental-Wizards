package net.elemental_wizards_rpg.compat;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class WizardsCompat {
    public static void registerCompat(){
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.of(MOD_ID, "wizard_changes"),
                    modContainer,
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        });
    }
}
