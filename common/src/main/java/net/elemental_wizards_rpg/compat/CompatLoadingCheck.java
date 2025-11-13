package net.elemental_wizards_rpg.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatLoadingCheck {
    public static boolean armoryLoadCheck(){
        return FabricLoader.getInstance().isModLoaded("armory_rpgs") | FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
