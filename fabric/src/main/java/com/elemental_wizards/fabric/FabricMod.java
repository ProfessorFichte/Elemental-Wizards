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

        // Register entity attributes for living entities (Fabric-specific)
        registerEntityAttributes();
    }

    private void registerEntityAttributes() {
        // Use the centralized registry with a lambda that delegates to Fabric's attribute registry
        ModEntitiesRegistry.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
    }
}
