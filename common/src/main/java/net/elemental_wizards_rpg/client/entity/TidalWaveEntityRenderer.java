package net.elemental_wizards_rpg.client.entity;

import net.elemental_wizards_rpg.entity.TidalWaveEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.LightEmission;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TidalWaveEntityRenderer<T extends TidalWaveEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public static final Identifier modelId = Identifier.of(MOD_ID, "entity/tidal_wave");

    private static final RenderLayer RENDER_LAYER = net.spell_engine.api.render.CustomLayers.spellEffect(LightEmission.GLOW, true);

    public TidalWaveEntityRenderer(EntityRendererFactory.Context context) {
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

        float renderYaw = entity.getYaw();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-renderYaw));

        float waveAnimation = (float) Math.sin((entity.age + tickDelta) * 0.2F) * 0.1F;
        matrixStack.translate(0, waveAnimation, 0);

        matrixStack.translate(0, 1.0F, 0);

        matrixStack.scale(6.0F, 2.0F, 1.5F);

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
