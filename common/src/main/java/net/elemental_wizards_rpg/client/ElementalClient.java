package net.elemental_wizards_rpg.client;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.elemental_wizards_rpg.client.armor.ElementalRobeRenderer;
import net.elemental_wizards_rpg.client.effect.*;
import net.elemental_wizards_rpg.client.entity.WhirlwindRenderer;
import net.elemental_wizards_rpg.client.entity.TerraStoneRenderer;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemEntityRenderer;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemSpikeEntityRenderer;
import net.elemental_wizards_rpg.client.entity.TidalWaveEntityRenderer;
import net.elemental_wizards_rpg.client.entity.HealingRainCloudEntityRenderer;
import net.elemental_wizards_rpg.client.entity.EarthquakeEntityRenderer;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.entity.WhirlwindEntity;
import net.elemental_wizards_rpg.entity.TerraStoneEntity;
import net.elemental_wizards_rpg.entity.EarthGolemEntity;
import net.elemental_wizards_rpg.entity.EarthGolemSpikeEntity;
import net.elemental_wizards_rpg.entity.TidalWaveEntity;
import net.elemental_wizards_rpg.entity.HealingRainCloudEntity;
import net.elemental_wizards_rpg.entity.EarthquakeEntity;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.particle.ModParticles;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.elemental_wizards_rpg.client.entity.earth_golem.EarthGolemEntityModel;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.client.particle.SpellParticle;
import net.spell_engine.rpg_series.item.Armor;

import java.util.function.Supplier;

import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;


public class ElementalClient{

    public static void init(){
        // Description values that aren't expressible as declarative `{token}`s (sub-spell estimates).
        // `TooltipTokens` is server-safe, but the handlers reach for client-only render helpers, so
        // they are registered from here. `ElementalWizardSpells` is already runtime-reachable via
        // `WeaponsRegister`/`ElementalSummons`; this call is explicit rather than relying on that.
        ElementalWizardSpells.registerTooltipTokens();

        registerArmorRenderer(Armors.elementalArmor.armorSet(), ElementalRobeRenderer::elemental);
        registerArmorRenderer(Armors.kelpArmor.armorSet(), ElementalRobeRenderer::kelp);
        registerArmorRenderer(Armors.dripstoneArmor.armorSet(), ElementalRobeRenderer::dripstone);
        registerArmorRenderer(Armors.windArmor.armorSet(), ElementalRobeRenderer::wind);
        registerArmorRenderer(Armors.netheriteKelpNetheriteArmor.armorSet(), ElementalRobeRenderer::netherite_kelp);
        registerArmorRenderer(Armors.netheriteDripstoneArmor.armorSet(), ElementalRobeRenderer::netherite_dripstone);
        registerArmorRenderer(Armors.netheriteWindArmor.armorSet(), ElementalRobeRenderer::netherite_wind);

        if (armoryLoadCheck()) {
            registerArmorRenderer(Armors.hurricaneArmorSet.armorSet(), ElementalRobeRenderer::hurricane);
            registerArmorRenderer(Armors.mountainArmorSet.armorSet(), ElementalRobeRenderer::mountain);
            registerArmorRenderer(Armors.oceanArmorSet.armorSet(), ElementalRobeRenderer::ocean);
        }

        CustomModelStatusEffect.register(ElementalEffects.BUBBLE_FOAM.effect, new BubbleFoamEffectRenderer());
        CustomModelStatusEffect.register(ElementalEffects.STONE_FLESH.effect, new StoneFleshEffectRenderer());
        CustomParticleStatusEffect.register(ElementalEffects.CLEANSING_WATER.effect, new CleansingWaterParticleSpawner());
        CustomParticleStatusEffect.register(ElementalEffects.BUBBLE_FOAM.effect, new BubbleFoamParticleSpawner());
        CustomParticleStatusEffect.register(ElementalEffects.STONE_FLESH.effect, new StoneFleshParticleSpawner());

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                registrationHelper.register(new StoneFleshPlayerRenderLayer(playerRenderer));
            }
        });

        CustomModelStatusEffect.register(ElementalEffects.IMPALED.effect, new ImpaledRenderer());

        EntityModelLayerRegistry.registerModelLayer(EarthGolemEntityModel.LAYER_LOCATION, EarthGolemEntityModel::createBodyLayer);

        EntityRendererRegistry.register(WhirlwindEntity.ENTITY_TYPE, WhirlwindRenderer::new);
        EntityRendererRegistry.register(TerraStoneEntity.ENTITY_TYPE, TerraStoneRenderer::new);
        EntityRendererRegistry.register(EarthGolemEntity.ENTITY_TYPE, EarthGolemEntityRenderer::new);
        EntityRendererRegistry.register(EarthGolemSpikeEntity.ENTITY_TYPE, EarthGolemSpikeEntityRenderer::new);
        EntityRendererRegistry.register(TidalWaveEntity.ENTITY_TYPE, TidalWaveEntityRenderer::new);
        EntityRendererRegistry.register(HealingRainCloudEntity.ENTITY_TYPE, HealingRainCloudEntityRenderer::new);
        EntityRendererRegistry.register(EarthquakeEntity.ENTITY_TYPE, EarthquakeEntityRenderer::new);

    }
    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
    public static void registerParticleAppearances() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

        // One generic factory for every entry this mod owns: SpellParticle resolves the
        // entry's defaults against the per-spawn ParticleGroup.Appearance payload.
        for (var entry: ModParticles.entries()) {
            registry.register(entry.type(), provider -> new SpellParticle.Factory(provider, entry));
        }
    }
}
