package com.elemental_wizards.fabric;

import net.elemental_wizards_rpg.ElementalMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ElementalMod.init();
        ElementalMod.registerEffects();
        ElementalMod.registerItems();
        ElementalMod.registerEntities();
    }
}
