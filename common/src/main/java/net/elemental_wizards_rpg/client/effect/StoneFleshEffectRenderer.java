package net.elemental_wizards_rpg.client.effect;

import net.elemental_wizards_rpg.ElementalMod;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomModels;

import java.util.HashMap;
import java.util.Map;

public class StoneFleshEffectRenderer implements CustomModelStatusEffect.Renderer {

    public static final Identifier MODEL_ID = Identifier.of(ElementalMod.MOD_ID, "spell_effect/stone_flesh");
    private static final RenderLayer RENDER_LAYER =
            RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

    private static final int STONE_COUNT = 8;
    private static final float GATHER_TICKS = 10.0f;
    private static final float FAR_RADIUS = 3.0f;
    private static final float CLOSE_RADIUS = 0.7f;
    private static final float STONE_SCALE = 0.45f;

    private static final float[] CLOSE_HEIGHT_FACTORS = { 0.08f, 0.50f, 0.17f, 0.72f, 0.08f, 0.56f, 0.22f, 0.67f };
    private static final float[] FAR_HEIGHTS          = { 0.4f,  2.5f,  1.2f,  3.0f,  0.3f,  2.2f,  1.5f,  2.8f };

    private static final float ORBIT_DEGREES_PER_TICK = 0.9f;

    private final Map<Integer, Float> startAgeMap = new HashMap<>();
    private final Map<Integer, Integer> lastDurationMap = new HashMap<>();

    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity entity, float delta, MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers, int light) {
        int entityId = entity.getId();
        float currentAge = entity.age + delta;
        float startAge = startAgeMap.computeIfAbsent(entityId, k -> currentAge);

        var effectEntry = ElementalEffects.STONE_FLESH.entry;
        if (effectEntry != null) {
            var instance = entity.getStatusEffect(effectEntry);
            if (instance != null) {
                int currentDuration = instance.getDuration();
                Integer lastDuration = lastDurationMap.get(entityId);
                if (lastDuration != null && currentDuration > lastDuration + 5) {
                    startAgeMap.put(entityId, currentAge);
                    startAge = currentAge;
                }
                lastDurationMap.put(entityId, currentDuration);
            }
        }

        float elapsed = currentAge - startAge;
        float progress = Math.min(1.0f, elapsed / GATHER_TICKS);
        float t = 1.0f - (1.0f - progress) * (1.0f - progress) * (1.0f - progress);

        float radius = FAR_RADIUS + (CLOSE_RADIUS - FAR_RADIUS) * t;
        float entityHeight = entity.getHeight();
        float orbitAngle = currentAge * ORBIT_DEGREES_PER_TICK;
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        for (int i = 0; i < STONE_COUNT; i++) {
            float angle = (360.0f / STONE_COUNT) * i + orbitAngle;
            float closeHeight = CLOSE_HEIGHT_FACTORS[i] * entityHeight;
            float height = FAR_HEIGHTS[i] + (closeHeight - FAR_HEIGHTS[i]) * t;

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
            matrices.translate(0.0, height, -radius);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f * i));
            matrices.scale(STONE_SCALE, STONE_SCALE, STONE_SCALE);
            CustomModels.render(RENDER_LAYER, itemRenderer, MODEL_ID, matrices, vertexConsumers, light, entityId + i);
            matrices.pop();
        }
    }
}
