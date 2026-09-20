package com.elemental_wizards.forge.client;

import net.elemental_wizards_rpg.client.ElementalClient;
import net.elemental_wizards_rpg.client.effect.StoneFleshPlayerRenderLayer;
import net.elemental_wizards_rpg.client.entity.EarthquakeEntityRenderer;
import net.elemental_wizards_rpg.client.entity.HealingRainCloudEntityRenderer;
import net.elemental_wizards_rpg.client.entity.TerraStoneRenderer;
import net.elemental_wizards_rpg.client.entity.TidalWaveEntityRenderer;
import net.elemental_wizards_rpg.client.entity.WhirlwindRenderer;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemEntityModel;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemEntityRenderer;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemSpikeEntityRenderer;
import net.elemental_wizards_rpg.entity.EarthGolemEntity;
import net.elemental_wizards_rpg.entity.EarthGolemSpikeEntity;
import net.elemental_wizards_rpg.entity.EarthquakeEntity;
import net.elemental_wizards_rpg.entity.HealingRainCloudEntity;
import net.elemental_wizards_rpg.entity.TerraStoneEntity;
import net.elemental_wizards_rpg.entity.TidalWaveEntity;
import net.elemental_wizards_rpg.entity.WhirlwindEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.spell_engine.client.gui.ConfigMenuScreen;

public final class ForgeClient {
    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClient::onClientSetup);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterLayerDefinitions.class,
                ForgeClient::onRegisterLayerDefinitions);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers.class,
                ForgeClient::onRegisterRenderers);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.AddLayers.class,
                ForgeClient::onAddLayers);
        modBus.addListener(EventPriority.NORMAL, false, RegisterParticleProvidersEvent.class,
                ForgeClient::registerParticleProviders);
    }

    private static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EarthGolemEntityModel.LAYER_LOCATION, EarthGolemEntityModel::createBodyLayer);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        ElementalClient.init();
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ConfigMenuScreen(parent)));
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        ElementalClient.registerParticleAppearances((type, factory) ->
                event.registerSpriteSet(type, factory::apply));
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhirlwindEntity.ENTITY_TYPE, WhirlwindRenderer::new);
        event.registerEntityRenderer(TerraStoneEntity.ENTITY_TYPE, TerraStoneRenderer::new);
        event.registerEntityRenderer(EarthGolemEntity.ENTITY_TYPE, EarthGolemEntityRenderer::new);
        event.registerEntityRenderer(EarthGolemSpikeEntity.ENTITY_TYPE, EarthGolemSpikeEntityRenderer::new);
        event.registerEntityRenderer(TidalWaveEntity.ENTITY_TYPE, TidalWaveEntityRenderer::new);
        event.registerEntityRenderer(HealingRainCloudEntity.ENTITY_TYPE, HealingRainCloudEntityRenderer::new);
        event.registerEntityRenderer(EarthquakeEntity.ENTITY_TYPE, EarthquakeEntityRenderer::new);
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            PlayerEntityRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addFeature(new StoneFleshPlayerRenderLayer(renderer));
            }
        }
    }
}
