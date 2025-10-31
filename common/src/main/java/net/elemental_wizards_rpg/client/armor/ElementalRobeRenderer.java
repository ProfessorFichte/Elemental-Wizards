package net.elemental_wizards_rpg.client.armor;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererConfig;
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

    public static ElementalRobeRenderer hurricane() {
        return new ElementalRobeRenderer("hurricane", "hurricane");
    }
    public static ElementalRobeRenderer mountain() {
        return new ElementalRobeRenderer("mountain", "mountain");
    }
    public static ElementalRobeRenderer ocean() {
        return new ElementalRobeRenderer("ocean", "ocean");
    }

    public ElementalRobeRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png")
        ).build());
    }
}
