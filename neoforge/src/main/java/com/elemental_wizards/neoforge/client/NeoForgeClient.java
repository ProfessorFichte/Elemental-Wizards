package com.elemental_wizards.neoforge.client;

import net.elemental_wizards_rpg.ElementalMod;
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
import net.more_rpg_classes.client.MoreRPGClassesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
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
        ElementalClient.registerParticleAppearances((type, factory) ->
                event.registerSpriteSet(type, factory::apply));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WhirlwindEntity.ENTITY_TYPE, WhirlwindRenderer::new);
        event.registerEntityRenderer(TerraStoneEntity.ENTITY_TYPE, TerraStoneRenderer::new);
        event.registerEntityRenderer(EarthGolemEntity.ENTITY_TYPE, EarthGolemEntityRenderer::new);
        event.registerEntityRenderer(EarthGolemSpikeEntity.ENTITY_TYPE, EarthGolemSpikeEntityRenderer::new);
        event.registerEntityRenderer(TidalWaveEntity.ENTITY_TYPE, TidalWaveEntityRenderer::new);
        event.registerEntityRenderer(HealingRainCloudEntity.ENTITY_TYPE, HealingRainCloudEntityRenderer::new);
        event.registerEntityRenderer(EarthquakeEntity.ENTITY_TYPE, EarthquakeEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            PlayerEntityRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addFeature(new StoneFleshPlayerRenderLayer(renderer));
            }
        }
    }
}
