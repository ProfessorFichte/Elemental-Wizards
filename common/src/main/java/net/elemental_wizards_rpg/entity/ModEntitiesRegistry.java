package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.ElementalMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.summon.SummonedEntities;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ModEntitiesRegistry {
    public static final Identifier EARTH_GOLEM_ID = Identifier.of(MOD_ID, "earth_golem");

    public static void registerEntities() {
        registerDripstoneEntities();
        registerWindEntities();
        registerWaterEntities();
        registerEarthEntities();
        registerLivingEntities();
    }

    private static void registerDripstoneEntities() {
        var terraStoneId = Identifier.of(MOD_ID, "terra_stone");
        TerraStoneEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                terraStoneId,
                EntityType.Builder.<TerraStoneEntity>create(TerraStoneEntity::new, SpawnGroup.MISC)
                        .dimensions(1.25F, 2.0F)
                        .makeFireImmune()
                        .maxTrackingRange(64)
                        .trackingTickInterval(20)
                        .build(terraStoneId.toString())
        );

        var earthGolemSpikeId = Identifier.of(MOD_ID, "earth_golem_spike");
        EarthGolemSpikeEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                earthGolemSpikeId,
                EntityType.Builder.<EarthGolemSpikeEntity>create(EarthGolemSpikeEntity::new, SpawnGroup.MISC)
                        .dimensions(1.25F, 1.5F)
                        .makeFireImmune()
                        .maxTrackingRange(64)
                        .trackingTickInterval(20)
                        .build(earthGolemSpikeId.toString())
        );

        var earthquakeId = Identifier.of(MOD_ID, "earthquake");
        EarthquakeEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                earthquakeId,
                EntityType.Builder.<EarthquakeEntity>create(EarthquakeEntity::new, SpawnGroup.MISC)
                        .dimensions(0.5F, 0.5F)
                        .makeFireImmune()
                        .maxTrackingRange(128)
                        .trackingTickInterval(1)
                        .build(earthquakeId.toString())
        );
    }

    private static void registerWindEntities() {
        var whirlwindId = Identifier.of(MOD_ID, "whirlwind");
        WhirlwindEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                whirlwindId,
                EntityType.Builder.<WhirlwindEntity>create(WhirlwindEntity::new, SpawnGroup.MISC)
                        .dimensions(1.5F, 2F)
                        .makeFireImmune()
                        .maxTrackingRange(128)
                        .trackingTickInterval(1)
                        .build(whirlwindId.toString())
        );
    }

    private static void registerWaterEntities() {
        var tidalWaveId = Identifier.of(MOD_ID, "tidal_wave");
        TidalWaveEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                tidalWaveId,
                EntityType.Builder.<TidalWaveEntity>create(TidalWaveEntity::new, SpawnGroup.MISC)
                        .dimensions(6.0F, 2.0F)
                        .makeFireImmune()
                        .maxTrackingRange(128)
                        .trackingTickInterval(1)
                        .build(tidalWaveId.toString())
        );

        var healingRainCloudId = Identifier.of(MOD_ID, "healing_rain_cloud");
        HealingRainCloudEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                healingRainCloudId,
                EntityType.Builder.<HealingRainCloudEntity>create(HealingRainCloudEntity::new, SpawnGroup.MISC)
                        .dimensions(4.0F, 2.0F)
                        .makeFireImmune()
                        .maxTrackingRange(128)
                        .trackingTickInterval(1)
                        .build(healingRainCloudId.toString())
        );
    }

    private static void registerEarthEntities() {
        EarthGolemEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                EARTH_GOLEM_ID,
                EntityType.Builder.<EarthGolemEntity>create(EarthGolemEntity::new, SpawnGroup.CREATURE)
                        .dimensions(2.6F, 3.5F)
                        .maxTrackingRange(64)
                        .trackingTickInterval(2)
                        .build(EARTH_GOLEM_ID.toString())
        );

        SummonedEntities.registerAttributes(EARTH_GOLEM_ID, EarthGolemEntity.ENTITY_TYPE, ElementalMod.summonConfig.value::entryFor);
    }

    private static void registerLivingEntities() {

    }
    public static void registerEntityAttributes(AttributeRegistrar registrar) {
        // Intentionally empty: SummonedEntities.registerAttributes (above) handles registration for all entities here.
    }

    @FunctionalInterface
    public interface AttributeRegistrar {
        void register(net.minecraft.entity.EntityType<? extends net.minecraft.entity.LivingEntity> entityType,
                     net.minecraft.entity.attribute.DefaultAttributeContainer.Builder builder);
    }
}
