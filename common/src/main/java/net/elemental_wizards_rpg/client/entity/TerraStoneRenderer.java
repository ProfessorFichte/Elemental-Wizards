package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.TerraStoneEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.spell_engine.api.render.CustomModels;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TerraStoneRenderer extends EntityRenderer<TerraStoneEntity> {
    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/dripstone_straight");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
    private static final float MODEL_HEIGHT = 3.0F;
    private final ItemRenderer itemRenderer;

    public TerraStoneRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(TerraStoneEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        float animationProgress = entity.getAnimationProgress(tickDelta);

        if (animationProgress == 0.0F) {
            return;
        }

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));

        float yOffset = -MODEL_HEIGHT + (MODEL_HEIGHT * 1.2F * animationProgress);
        matrices.translate(0.0, yOffset, 0.0);

        CustomModels.render(
            RENDER_LAYER,
            itemRenderer,
            modelId,
            matrices,
            vertexConsumers,
            light,
            entity.getId()
        );

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(TerraStoneEntity entity) {
        return null;
    }
}
