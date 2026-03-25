package net.elemental_wizards_rpg.client.entity.earth_golem;

import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemSpikeEntity;
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

public class EarthGolemSpikeEntityRenderer extends EntityRenderer<EarthGolemSpikeEntity> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID, "spell_effect/sharp_dripstone.png");
    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/dripstone_straight");

    private static final float MODEL_HEIGHT = 2.75F;

    public EarthGolemSpikeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EarthGolemSpikeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float animationProgress = entity.getAnimationProgress(tickDelta);

        if (animationProgress == 0.0F) {
            return;
        }

        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));

        float emergeProgress;
        if (animationProgress < 0.5F) {
            emergeProgress = animationProgress * 2.0F;
        } else {
            emergeProgress = 2.0F - (animationProgress * 2.0F);
        }

        float yOffset = -MODEL_HEIGHT + (MODEL_HEIGHT * emergeProgress);
        matrices.translate(0.0, yOffset, 0.0);

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);

        if (model != null) {
            matrices.scale(1.2F, 1.2F, 1.2F);
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
    public Identifier getTexture(EarthGolemSpikeEntity entity) {
        return TEXTURE;
    }
}
