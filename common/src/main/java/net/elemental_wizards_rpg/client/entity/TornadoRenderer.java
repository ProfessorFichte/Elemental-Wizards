package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.TornadoEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.CustomModels;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TornadoRenderer<T extends TornadoEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public TornadoRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }

    public static final Identifier modelId = Identifier.of(MOD_ID, "effect/tornado");
    private static final RenderLayer layer =
            RenderLayer.getEntityTranslucentEmissive(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, true);

    public void render(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider
            vertexConsumers, int light) {
        float yOffset = 0.51F; // y + 0.01 to avoid Y fighting
        matrixStack.push();
        matrixStack.translate(0, yOffset, 0); // y + 0.01 to avoid Y fighting
        CustomModels.render(layer, MinecraftClient.getInstance().getItemRenderer(), modelId,
                matrixStack, vertexConsumers, light, entity.getId());
        matrixStack.pop();
    }
}
