package net.elemental_wizards_rpg.client.entity.earth_golem;

import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemSpikeEntity;
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

public class EarthGolemSpikeEntityRenderer extends EntityRenderer<EarthGolemSpikeEntity> {
    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/dripstone_straight");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

    private static final float MODEL_HEIGHT = 2.75F;
    private final ItemRenderer itemRenderer;

    public EarthGolemSpikeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
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

        matrices.scale(1.2F, 1.2F, 1.2F);
        CustomModels.render(RENDER_LAYER, itemRenderer, modelId, matrices, vertexConsumers, light, entity.getId());

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(EarthGolemSpikeEntity entity) {
        return null;
    }
}
