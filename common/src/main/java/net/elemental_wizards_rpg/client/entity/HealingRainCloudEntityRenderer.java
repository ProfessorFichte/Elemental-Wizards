package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.spell_spawned.HealingRainCloudEntity;
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

public class HealingRainCloudEntityRenderer<T extends HealingRainCloudEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/healing_rain_cloud");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

    public HealingRainCloudEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);

        matrixStack.push();

        float rotation = (entity.age + tickDelta) * 0.5F;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        matrixStack.scale(4.0F, 2.0F, 4.0F);

        CustomModels.render(
            RENDER_LAYER,
            itemRenderer,
            modelId,
            matrixStack,
            vertexConsumers,
            light,
            entity.getId()
        );

        matrixStack.pop();
    }
}
