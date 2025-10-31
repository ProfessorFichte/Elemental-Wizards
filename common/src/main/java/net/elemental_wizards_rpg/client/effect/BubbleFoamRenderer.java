package net.elemental_wizards_rpg.client.effect;

import net.elemental_wizards_rpg.ElementalMod;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.OrbitingEffectRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;


import java.util.List;

public class BubbleFoamRenderer extends OrbitingEffectRenderer {
    public static final Identifier modelId_base = Identifier.of(ElementalMod.MOD_ID, "effect/bubble_foam");

    private static final RenderLayer BASE_RENDER_LAYER =
            RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);


    public BubbleFoamRenderer() {
        super(List.of(
                new Model(BASE_RENDER_LAYER, modelId_base)),
                2F,
                0F
        );
    }
}