package net.elemental_wizards_rpg.client.armor;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalRobeRenderer extends AzArmorRenderer {
    public static ElementalRobeRenderer elemental() {
        return new ElementalRobeRenderer("wizard_t1", "elemental");
    }

    public static ElementalRobeRenderer kelp() {
        return new ElementalRobeRenderer("wizard_t1", "kelp");
    }
    public static ElementalRobeRenderer dripstone() {
        return new ElementalRobeRenderer("wizard_t1", "dripstone");
    }
    public static ElementalRobeRenderer wind() {
        return new ElementalRobeRenderer("wizard_t1", "wind");
    }

    public static ElementalRobeRenderer netherite_kelp() {
        return new ElementalRobeRenderer("wizard_t1", "netherite_kelp");
    }
    public static ElementalRobeRenderer netherite_dripstone() {
        return new ElementalRobeRenderer("wizard_t1", "netherite_dripstone");
    }
    public static ElementalRobeRenderer netherite_wind() {
        return new ElementalRobeRenderer("wizard_t1", "netherite_wind");
    }

    public ElementalRobeRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png")
        ).build());
    }
}
