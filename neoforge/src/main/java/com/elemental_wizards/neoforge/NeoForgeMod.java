package com.elemental_wizards.neoforge;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(ElementalMod.MOD_ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        ElementalMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(EntityAttributeCreationEvent.class, NeoForgeMod::registerAttributes);
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.ITEM, reg -> {
            ElementalMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            ElementalMod.registerEffects();
        });
        event.register(RegistryKeys.ENTITY_TYPE, reg -> {
            ElementalMod.registerEntities();
        });
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ModEntitiesRegistry.registerEntityAttributes((entityType, builder) -> event.put(entityType, builder.build()));
    }
}
