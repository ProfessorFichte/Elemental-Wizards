package com.elemental_wizards.fabric.client;

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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public final class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElementalClient.init();

        ElementalClient.registerParticleAppearances((type, factory) ->
                ParticleFactoryRegistry.getInstance().register(type, factory::apply));

        EntityModelLayerRegistry.registerModelLayer(EarthGolemEntityModel.LAYER_LOCATION, EarthGolemEntityModel::createBodyLayer);

        EntityRendererRegistry.register(WhirlwindEntity.ENTITY_TYPE, WhirlwindRenderer::new);
        EntityRendererRegistry.register(TerraStoneEntity.ENTITY_TYPE, TerraStoneRenderer::new);
        EntityRendererRegistry.register(EarthGolemEntity.ENTITY_TYPE, EarthGolemEntityRenderer::new);
        EntityRendererRegistry.register(EarthGolemSpikeEntity.ENTITY_TYPE, EarthGolemSpikeEntityRenderer::new);
        EntityRendererRegistry.register(TidalWaveEntity.ENTITY_TYPE, TidalWaveEntityRenderer::new);
        EntityRendererRegistry.register(HealingRainCloudEntity.ENTITY_TYPE, HealingRainCloudEntityRenderer::new);
        EntityRendererRegistry.register(EarthquakeEntity.ENTITY_TYPE, EarthquakeEntityRenderer::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                registrationHelper.register(new StoneFleshPlayerRenderLayer(playerRenderer));
            }
        });
    }
}
