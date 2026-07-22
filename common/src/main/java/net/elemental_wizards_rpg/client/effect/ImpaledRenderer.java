package net.elemental_wizards_rpg.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomLayers;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.render.LightEmission;

import java.util.HashMap;
import java.util.Map;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ImpaledRenderer implements CustomModelStatusEffect.Renderer {

    private static final RenderLayer RENDER_LAYER = CustomLayers.spellEffect(LightEmission.RADIATE, false);
    public static final Identifier modelId = Identifier.of(MOD_ID, "spell_effect/dripstone_straight");

    private static final int SPIKE_COUNT = 3;
    private static final float EMERGE_TICKS = 10.0f;
    private static final float EMERGE_START_Y = -1.5f;
    private static final float[] SPIKE_ANGLES  = { 0f,    120f,  240f  };
    private static final float[] SPIKE_RADII   = { 0.20f, 0.25f, 0.15f };
    private static final float[] SPIKE_FINAL_Y = { 0.5f,  0.3f,  0.7f  };

    private final Map<Integer, Float> startAgeMap = new HashMap<>();
    private final Map<Integer, Float> lastRenderAgeMap = new HashMap<>();

    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity entity, float delta, MatrixStack matrixStack,
                              VertexConsumerProvider vertexConsumers, int light) {
        int entityId = entity.getId();
        float currentAge = entity.age + delta;

        float startAge = startAgeMap.computeIfAbsent(entityId, k -> currentAge);

        Float lastRenderAge = lastRenderAgeMap.get(entityId);
        if (lastRenderAge != null && currentAge - lastRenderAge > 3.0f) {
            startAgeMap.put(entityId, currentAge);
            startAge = currentAge;
        }
        lastRenderAgeMap.put(entityId, currentAge);

        float elapsed = currentAge - startAge;
        float progress = Math.min(1.0f, elapsed / EMERGE_TICKS);
        float t = 1.0f - (1.0f - progress) * (1.0f - progress) * (1.0f - progress);

        float scale = entity.getScale();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        for (int i = 0; i < SPIKE_COUNT; i++) {
            float currentY = EMERGE_START_Y + (SPIKE_FINAL_Y[i] - EMERGE_START_Y) * t;

            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(SPIKE_ANGLES[i]));
            matrixStack.translate(SPIKE_RADII[i], currentY, 0);
            matrixStack.scale(scale + 0.25f, scale, scale + 0.25f);
            CustomModels.render(RENDER_LAYER, itemRenderer, modelId, matrixStack, vertexConsumers, light, entityId + i);
            matrixStack.pop();
        }
    }
}
