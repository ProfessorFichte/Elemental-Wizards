package com.elemental_wizards.neoforge.client;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.client.ElementalClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.spell_engine.client.gui.ConfigMenuScreen;

@EventBusSubscriber(modid = ElementalMod.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ElementalClient.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, parent) -> new ConfigMenuScreen(parent));
    }
}
