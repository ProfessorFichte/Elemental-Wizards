package com.elemental_wizards.fabric;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ElementalMod.init();
        ElementalMod.registerEffects();
        ElementalMod.registerItems();
        ElementalMod.registerEntities();
        ElementalMod.registerParticles();

        registerEntityAttributes();
    }

    private void registerEntityAttributes() {
        ModEntitiesRegistry.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
    }
}
