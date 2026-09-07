package net.elemental_wizards_rpg.client.armor;

import net.minecraft.util.Identifier;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public final class ElementalRobeRenderer {
    private ElementalRobeRenderer() { }

    public static GeoArmorRenderer elemental() {
        return make("wizard_t1", "elemental");
    }

    public static GeoArmorRenderer kelp() {
        return make("wizard_t1", "kelp");
    }
    public static GeoArmorRenderer dripstone() {
        return make("wizard_t1", "dripstone");
    }
    public static GeoArmorRenderer wind() {
        return make("wizard_t1", "wind");
    }

    public static GeoArmorRenderer netherite_kelp() {
        return make("wizard_t1", "netherite_kelp");
    }
    public static GeoArmorRenderer netherite_dripstone() {
        return make("wizard_t1", "netherite_dripstone");
    }
    public static GeoArmorRenderer netherite_wind() {
        return make("wizard_t1", "netherite_wind");
    }

    public static GeoArmorRenderer hurricane() {
        return make("hurricane", "hurricane");
    }
    public static GeoArmorRenderer mountain() {
        return make("mountain", "mountain");
    }
    public static GeoArmorRenderer ocean() {
        return make("ocean", "ocean");
    }

    private static GeoArmorRenderer make(String modelName, String textureName) {
        return GeoArmorRenderer.of(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png"));
    }
}
