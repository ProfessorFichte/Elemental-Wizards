package net.elemental_wizards_rpg.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.client.armor.ElementalRobeRenderer;
import net.elemental_wizards_rpg.client.effect.*;
import net.elemental_wizards_rpg.client.entity.DripstoneSmallRenderer;
import net.elemental_wizards_rpg.client.entity.DripstoneStraightRenderer;
import net.elemental_wizards_rpg.client.entity.TornadoRenderer;
import net.elemental_wizards_rpg.effect.Effects;
import net.elemental_wizards_rpg.entity.DripstoneSmallEntity;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.armor.ArmoryCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.elemental_wizards_rpg.client.entity.DripstoneBigRenderer;
import net.elemental_wizards_rpg.entity.DripstoneBigEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.CustomModels;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ElementalClient implements ClientModInitializer {

    public void  onInitializeClient(){
        CustomModels.registerModelIds(List.of(
                DripstoneBigRenderer.modelId,
                DripstoneSmallRenderer.modelId,
                DripstoneStraightRenderer.modelId,
                BubbleFoamRenderer.modelId_base,
                TornadoRenderer.modelId,
                Identifier.of(ElementalMod.MOD_ID, "projectile/stone_spear"),
                Identifier.of(ElementalMod.MOD_ID, "projectile/spell_stone"),
                Identifier.of(ElementalMod.MOD_ID, "projectile/stone_shard"),
                Identifier.of(ElementalMod.MOD_ID, "projectile/big_bubble")
        ));

        registerArmorRenderer(Armors.elementalArmor, ElementalRobeRenderer::elemental);
        registerArmorRenderer(Armors.kelpArmor, ElementalRobeRenderer::kelp);
        registerArmorRenderer(Armors.dripstoneArmor, ElementalRobeRenderer::dripstone);
        registerArmorRenderer(Armors.windArmor, ElementalRobeRenderer::wind);
        registerArmorRenderer(Armors.netheriteKelpNetheriteArmor, ElementalRobeRenderer::netherite_kelp);
        registerArmorRenderer(Armors.netheriteDripstoneArmor, ElementalRobeRenderer::netherite_dripstone);
        registerArmorRenderer(Armors.netheriteWindArmor, ElementalRobeRenderer::netherite_wind);
        /*
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || ElementalMod.tweaksConfig.value.ignore_items_required_mods) {
            registerArmorRenderer(ArmoryCompat.hurricane.armorSet(), ElementalRobeRenderer::hurricane);
            registerArmorRenderer(ArmoryCompat.mountain.armorSet(), ElementalRobeRenderer::mountain);
            registerArmorRenderer(ArmoryCompat.ocean.armorSet(), ElementalRobeRenderer::ocean);
        }
         */

        CustomModelStatusEffect.register(Effects.BUBBLE_FOAM.effect, new BubbleFoamRenderer());
        CustomParticleStatusEffect.register(Effects.CLEANSING_WATER.effect, new CleansingWaterParticleSpawner());
        CustomParticleStatusEffect.register(Effects.BUBBLE_FOAM.effect, new BubbleFoamParticleSpawner());
        CustomParticleStatusEffect.register(Effects.STONE_FLESH.effect, new StoneFleshParticleSpawner());

        EntityRendererRegistry.register(DripstoneBigEntity.ENTITY_TYPE, DripstoneBigRenderer::new);
        EntityRendererRegistry.register(DripstoneSmallEntity.ENTITY_TYPE, DripstoneSmallRenderer::new);
    }
    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
