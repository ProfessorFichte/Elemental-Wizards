package net.elemental_wizards_rpg.client.effect;

import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

public class StoneFleshPlayerRenderLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier DRIPSTONE_TEXTURE = Identifier.of("minecraft", "textures/block/dripstone_block.png");
    private static final float TILE_SCALE = 4.0f;

    public StoneFleshPlayerRenderLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!player.hasStatusEffect(ElementalEffects.STONE_FLESH.entry)) return;
        float maxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        if (player.getHealth() < maxHealth) return;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(DRIPSTONE_TEXTURE));
        getContextModel().render(matrices, new TilingVertexConsumer(vertexConsumer, TILE_SCALE), light, OverlayTexture.DEFAULT_UV);
    }

    private static class TilingVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float scale;

        TilingVertexConsumer(VertexConsumer delegate, float scale) {
            this.delegate = delegate;
            this.scale = scale;
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            delegate.color(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            delegate.texture(u * scale, v * scale);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            delegate.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            delegate.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            return this;
        }
    }
}
