package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.entity.DripstoneSmallEntity;
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

public class DripstoneSmallRenderer <T extends DripstoneSmallEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public DripstoneSmallRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }

    public static final Identifier modelId = Identifier.of(ElementalMod.MOD_ID, "spell_effect/dripstone_small");

    private static final RenderLayer layer =
            RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);


    public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-1F * entity.getYaw() + 180F));
        matrixStack.translate(0, 0.5, 0);
        CustomModels.render(layer, itemRenderer, modelId, matrixStack, vertexConsumers, light, entity.getId());
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.pop();
    }
}
