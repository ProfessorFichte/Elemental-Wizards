package net.elemental_wizards_rpg.client;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.client.effect.*;
import net.elemental_wizards_rpg.client.entity.DripstoneSmallRenderer;
import net.elemental_wizards_rpg.client.entity.TornadoRenderer;
import net.elemental_wizards_rpg.effect.Effects;
import net.elemental_wizards_rpg.entity.DripstoneSmallEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.elemental_wizards_rpg.client.entity.DripstoneBigRenderer;
import net.elemental_wizards_rpg.entity.DripstoneBigEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.CustomModels;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ElementalClient implements ClientModInitializer {

    public void  onInitializeClient(){
        CustomModels.registerModelIds(List.of(
                DripstoneBigRenderer.modelId,
                DripstoneSmallRenderer.modelId,
                BubbleFoamRenderer.modelId_base,
                TornadoRenderer.modelId,
                new Identifier(ElementalMod.MOD_ID, "projectile/stone_spear"),
                new Identifier(ElementalMod.MOD_ID, "projectile/stone_shard"),
                new Identifier(ElementalMod.MOD_ID, "projectile/spell_stone"),
                new Identifier(ElementalMod.MOD_ID, "projectile/big_bubble")
        ));

        CustomModelStatusEffect.register(Effects.BUBBLE_FOAM, new BubbleFoamRenderer());
        CustomParticleStatusEffect.register(Effects.SOAKED, new SoakedParticles(5));
        CustomParticleStatusEffect.register(Effects.CLEANSING_WATER, new CleansingWaterParticleSpawner());
        CustomParticleStatusEffect.register(Effects.BUBBLE_FOAM, new BubbleFoamParticleSpawner());
        CustomParticleStatusEffect.register(Effects.STONE_FLESH, new StoneFleshParticleSpawner());

        EntityRendererRegistry.register(DripstoneBigEntity.ENTITY_TYPE, DripstoneBigRenderer::new);
        EntityRendererRegistry.register(DripstoneSmallEntity.ENTITY_TYPE, DripstoneSmallRenderer::new);
    }
}
