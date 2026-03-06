package com.elemental_wizards.neoforge.client;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.client.ElementalClient;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemEntityModel;
import net.more_rpg_classes.client.MoreRPGClassesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.spell_engine.client.gui.ConfigMenuScreen;

@EventBusSubscriber(modid = ElementalMod.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClient {
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EarthGolemEntityModel.LAYER_LOCATION, EarthGolemEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ElementalClient.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, parent) -> new ConfigMenuScreen(parent));
    }
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        ElementalClient.registerParticleAppearances();
    }
}
