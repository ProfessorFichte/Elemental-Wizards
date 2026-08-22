package net.elemental_wizards_rpg.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatLoadingCheck {
    public static boolean armoryLoadCheck(){
        return FabricLoader.getInstance().isModLoaded("armory_rpgs") | FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /// Wizards is an optional enrichment target, not a dependency.
    ///
    /// Unlike [#armoryLoadCheck()] this deliberately does NOT fall back to the development
    /// environment: the content it gates (the `wizard_changes` pack) writes into the `wizards`
    /// namespace and lists `wizards:` item ids, which cannot be resolved when Wizards is absent -
    /// and Wizards is not on the dev runtime classpath of this project.
    public static boolean wizardsLoadCheck(){
        return FabricLoader.getInstance().isModLoaded("wizards");
    }
}
