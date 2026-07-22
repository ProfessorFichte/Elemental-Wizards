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

    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/water_wave");

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

        int stateOrdinal = entity.getWaveStateOrdinal();
        // 2 = HIDDEN
        if (stateOrdinal == 2) return;

        float smoothStateTick = entity.getStateTick() + tickDelta;
        float yScaleMod = 1.0F;
        if (stateOrdinal == 1) {
            // SUBMERGING: scale from 1 → 0
            yScaleMod = Math.max(0.0F, 1.0F - (smoothStateTick / TidalWaveEntity.SUBMERGING_DURATION));
        } else if (stateOrdinal == 3) {
            // EMERGING: scale from 0 → 1
            yScaleMod = Math.min(1.0F, smoothStateTick / TidalWaveEntity.EMERGING_DURATION);
        }

        if (yScaleMod < 0.005F) return;

        matrixStack.push();

        float renderYaw = entity.getYaw();
        // stateOrdinal >= 3 means EMERGING or MOVING_BACKWARD
        boolean isReturning = stateOrdinal >= 3;
        float rotOffset = isReturning ? 0.0F : 180.0F;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-renderYaw + rotOffset));

        float waveAnimation = ((float) Math.sin((entity.age + tickDelta) * 0.2F) + 1.0F) * 0.05F;
        matrixStack.translate(0, waveAnimation, 0);
        matrixStack.translate(0, 1.0F, 0);

        matrixStack.translate(-0.5F, 0.0F, -0.625F);
        matrixStack.scale(2.0F, 2.0F * yScaleMod, 1.0F);

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
