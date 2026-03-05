package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.TerraStoneEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TerraStoneRenderer extends EntityRenderer<TerraStoneEntity> {
    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/dripstone_straight");

    private static final Identifier TEXTURE = Identifier.of(MOD_ID, "textures/spell_effect/sharp_dripstone.png");

    private static final float MODEL_HEIGHT = 3.0F;

    public TerraStoneRenderer(EntityRendererFactory.Context context) {
        super(context);
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

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

        if (model != null) {
            matrices.scale(1.0F, 1.0F, 1.0F);
            matrices.translate(-0.5, -0.5, -0.5);

            var particleSprite = model.getParticleSprite();
            var renderLayer = RenderLayer.getEntityCutoutNoCull(particleSprite.getAtlasId());
            VertexConsumer buffer = vertexConsumers.getBuffer(renderLayer);

            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                matrices.peek(),
                buffer,
                null,
                model,
                1.0F, 1.0F, 1.0F,
                light,
                OverlayTexture.DEFAULT_UV
            );
        }

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(TerraStoneEntity entity) {
        return TEXTURE;
    }
}
