package net.elemental_wizards_rpg.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ModEntitiesRegistry {
    public static void registerEntities() {
        // Spell Effect Entities
        registerDripstoneEntities();
        registerWindEntities();
        registerWaterEntities();
        registerEarthEntities();
        registerLivingEntities();
    }

    private static void registerDripstoneEntities() {
        TerraStoneEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "terra_stone"),
                FabricEntityTypeBuilder.<TerraStoneEntity>create(SpawnGroup.MISC, TerraStoneEntity::new)
                        .dimensions(EntityDimensions.fixed(1.25F, 2.0F))
                        .fireImmune()
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(20)
                        .build()
        );

        EarthGolemSpikeEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "earth_golem_spike"),
                FabricEntityTypeBuilder.<EarthGolemSpikeEntity>create(SpawnGroup.MISC, EarthGolemSpikeEntity::new)
                        .dimensions(EntityDimensions.fixed(1.25F, 1.5F))
                        .fireImmune()
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(20)
                        .build()
        );

        EarthquakeEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "earthquake"),
                FabricEntityTypeBuilder.<EarthquakeEntity>create(SpawnGroup.MISC, EarthquakeEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
    }

    private static void registerWindEntities() {
        TornadoEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "tornado"),
                FabricEntityTypeBuilder.<TornadoEntity>create(SpawnGroup.MISC, TornadoEntity::new)
                        .dimensions(EntityDimensions.changing(4.0F, 4.0F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(20)
                        .build()
        );

        WhirlwindEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "whirlwind"),
                FabricEntityTypeBuilder.<WhirlwindEntity>create(SpawnGroup.MISC, WhirlwindEntity::new)
                        .dimensions(EntityDimensions.changing(1.5F, 2F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );

        StormDraftEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "storm_draft"),
                FabricEntityTypeBuilder.<StormDraftEntity>create(SpawnGroup.MISC, StormDraftEntity::new)
                        .dimensions(EntityDimensions.fixed(4.0F, 4.0F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
    }

    private static void registerWaterEntities() {
        TidalWaveEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "tidal_wave"),
                FabricEntityTypeBuilder.<TidalWaveEntity>create(SpawnGroup.MISC, TidalWaveEntity::new)
                        .dimensions(EntityDimensions.changing(6.0F, 2.0F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );

        HealingRainCloudEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "healing_rain_cloud"),
                FabricEntityTypeBuilder.<HealingRainCloudEntity>create(SpawnGroup.MISC, HealingRainCloudEntity::new)
                        .dimensions(EntityDimensions.fixed(4.0F, 2.0F))
                        .fireImmune()
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
    }

    private static void registerEarthEntities() {
        EarthGolemEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(MOD_ID, "earth_golem"),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EarthGolemEntity::new)
                        .dimensions(EntityDimensions.fixed(2.6F, 3.5F))
                        .trackRangeBlocks(64)
                        .trackedUpdateRate(2)
                        .build()
        );
    }

    private static void registerLivingEntities() {

    }
    public static void registerEntityAttributes(AttributeRegistrar registrar) {
        registrar.register(
            EarthGolemEntity.ENTITY_TYPE,
            EarthGolemEntity.createEarthGolemAttributes()
        );
    }

    @FunctionalInterface
    public interface AttributeRegistrar {
        void register(net.minecraft.entity.EntityType<? extends net.minecraft.entity.LivingEntity> entityType,
                     net.minecraft.entity.attribute.DefaultAttributeContainer.Builder builder);
    }
}
