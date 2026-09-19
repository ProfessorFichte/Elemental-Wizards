package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.ElementalMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.summon.SummonedEntities;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ModEntitiesRegistry {
    public static final Identifier EARTH_GOLEM_ID = new Identifier(MOD_ID, "earth_golem");

    public static void registerEntities() {
        entitiesToRegister().forEach((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
        registerSummonAttributes();
    }

    /// Trailing side effect of {@link #registerEntities}, kept out of {@link #entitiesToRegister} so a
    /// failure here cannot abort entity registration. Not a registry write: Spell Engine buffers it until
    /// the platform's attribute registration point (Fabric: immediate; Forge: `EntityAttributeCreationEvent`).
    /// Run it right after the registration loop on whichever loader registered the types.
    public static void registerSummonAttributes() {
        SummonedEntities.registerAttributes(EARTH_GOLEM_ID, EarthGolemEntity.ENTITY_TYPE, ElementalMod.summonConfig.value::entryFor);
    }

    /// Creation only — builds every entity type, stores it in its `ENTITY_TYPE` field and returns the types
    /// keyed by the id they register under. A loader that registers entity types itself (Forge) iterates
    /// this instead of calling {@link #registerEntities}.
    ///
    /// **Must run inside the registration phase**: Forge-patched `EntityType.<init>` asks the ENTITY_TYPE
    /// registry for an intrusive holder, so merely *building* a type outside the `RegisterEvent` sequence
    /// throws `Registry is already frozen`.
    public static Map<Identifier, EntityType<?>> entitiesToRegister() {
        var types = new LinkedHashMap<Identifier, EntityType<?>>();
        buildDripstoneEntities(types);
        buildWindEntities(types);
        buildWaterEntities(types);
        buildEarthEntities(types);
        buildLivingEntities(types);
        return types;
    }

    private static void buildDripstoneEntities(Map<Identifier, EntityType<?>> types) {
        var terraStoneId = new Identifier(MOD_ID, "terra_stone");
        TerraStoneEntity.ENTITY_TYPE = EntityType.Builder.<TerraStoneEntity>create(TerraStoneEntity::new, SpawnGroup.MISC)
                .setDimensions(1.25F, 2.0F)
                .makeFireImmune()
                .maxTrackingRange(64)
                .trackingTickInterval(20)
                .build(terraStoneId.toString());
        types.put(terraStoneId, TerraStoneEntity.ENTITY_TYPE);

        var earthGolemSpikeId = new Identifier(MOD_ID, "earth_golem_spike");
        EarthGolemSpikeEntity.ENTITY_TYPE = EntityType.Builder.<EarthGolemSpikeEntity>create(EarthGolemSpikeEntity::new, SpawnGroup.MISC)
                .setDimensions(1.25F, 1.5F)
                .makeFireImmune()
                .maxTrackingRange(64)
                .trackingTickInterval(20)
                .build(earthGolemSpikeId.toString());
        types.put(earthGolemSpikeId, EarthGolemSpikeEntity.ENTITY_TYPE);

        var earthquakeId = new Identifier(MOD_ID, "earthquake");
        EarthquakeEntity.ENTITY_TYPE = EntityType.Builder.<EarthquakeEntity>create(EarthquakeEntity::new, SpawnGroup.MISC)
                .setDimensions(0.5F, 0.5F)
                .makeFireImmune()
                .maxTrackingRange(128)
                .trackingTickInterval(1)
                .build(earthquakeId.toString());
        types.put(earthquakeId, EarthquakeEntity.ENTITY_TYPE);
    }

    private static void buildWindEntities(Map<Identifier, EntityType<?>> types) {
        var whirlwindId = new Identifier(MOD_ID, "whirlwind");
        WhirlwindEntity.ENTITY_TYPE = EntityType.Builder.<WhirlwindEntity>create(WhirlwindEntity::new, SpawnGroup.MISC)
                .setDimensions(1.5F, 2F)
                .makeFireImmune()
                .maxTrackingRange(128)
                .trackingTickInterval(1)
                .build(whirlwindId.toString());
        types.put(whirlwindId, WhirlwindEntity.ENTITY_TYPE);
    }

    private static void buildWaterEntities(Map<Identifier, EntityType<?>> types) {
        var tidalWaveId = new Identifier(MOD_ID, "tidal_wave");
        TidalWaveEntity.ENTITY_TYPE = EntityType.Builder.<TidalWaveEntity>create(TidalWaveEntity::new, SpawnGroup.MISC)
                .setDimensions(6.0F, 2.0F)
                .makeFireImmune()
                .maxTrackingRange(128)
                .trackingTickInterval(1)
                .build(tidalWaveId.toString());
        types.put(tidalWaveId, TidalWaveEntity.ENTITY_TYPE);

        var healingRainCloudId = new Identifier(MOD_ID, "healing_rain_cloud");
        HealingRainCloudEntity.ENTITY_TYPE = EntityType.Builder.<HealingRainCloudEntity>create(HealingRainCloudEntity::new, SpawnGroup.MISC)
                .setDimensions(4.0F, 2.0F)
                .makeFireImmune()
                .maxTrackingRange(128)
                .trackingTickInterval(1)
                .build(healingRainCloudId.toString());
        types.put(healingRainCloudId, HealingRainCloudEntity.ENTITY_TYPE);
    }

    private static void buildEarthEntities(Map<Identifier, EntityType<?>> types) {
        EarthGolemEntity.ENTITY_TYPE = EntityType.Builder.<EarthGolemEntity>create(EarthGolemEntity::new, SpawnGroup.CREATURE)
                .setDimensions(2.6F, 3.5F)
                .maxTrackingRange(64)
                .trackingTickInterval(2)
                .build(EARTH_GOLEM_ID.toString());
        types.put(EARTH_GOLEM_ID, EarthGolemEntity.ENTITY_TYPE);
    }

    private static void buildLivingEntities(Map<Identifier, EntityType<?>> types) {

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
