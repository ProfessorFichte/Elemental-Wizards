package net.elemental_wizards_rpg.client.effect;

import net.elemental_wizards_rpg.ElementalMod;
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

import java.util.Random;

public class BubbleFoamEffectRenderer implements CustomModelStatusEffect.Renderer {

    public static final Identifier MODEL_ID = Identifier.of(ElementalMod.MOD_ID, "spell_effect/bubble_foam");
    private static final RenderLayer RENDER_LAYER =
            RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

    private static final int BUBBLE_COUNT = 45;
    private static final float TWO_PI = (float) (2.0 * Math.PI);

    private final float[] orbitRadius       = new float[BUBBLE_COUNT];
    private final float[] orbitHeightFactor = new float[BUBBLE_COUNT];
    private final float[] orbitSpeed        = new float[BUBBLE_COUNT];
    private final float[] orbitPhase        = new float[BUBBLE_COUNT];
    private final float[] selfRotSpeedY     = new float[BUBBLE_COUNT];
    private final float[] selfRotSpeedX     = new float[BUBBLE_COUNT];
    private final float[] pulseFreq         = new float[BUBBLE_COUNT];
    private final float[] pulsePhase        = new float[BUBBLE_COUNT];
    private final float[] baseScale         = new float[BUBBLE_COUNT];
    private final float[] pulseAmplitude    = new float[BUBBLE_COUNT];
    private final float[] bobAmplitude      = new float[BUBBLE_COUNT];
    private final float[] bobFreq           = new float[BUBBLE_COUNT];
    private final float[] bobPhase          = new float[BUBBLE_COUNT];

    public BubbleFoamEffectRenderer() {
        Random rng = new Random(7331L);
        for (int i = 0; i < BUBBLE_COUNT; i++) {
            orbitRadius[i]       = 0.5f + rng.nextFloat() * 0.9f;
            orbitHeightFactor[i] = rng.nextFloat() * 1.1f;
            float ospeed         = 0.5f + rng.nextFloat() * 1.5f;
            orbitSpeed[i]        = rng.nextBoolean() ? ospeed : -ospeed;
            orbitPhase[i]        = rng.nextFloat() * 360.0f;
            float sy             = 1.0f + rng.nextFloat() * 4.0f;
            selfRotSpeedY[i]     = rng.nextBoolean() ? sy : -sy;
            float sx             = 0.5f + rng.nextFloat() * 3.0f;
            selfRotSpeedX[i]     = rng.nextBoolean() ? sx : -sx;
            pulseFreq[i]         = TWO_PI / (10.0f + rng.nextFloat() * 20.0f);
            pulsePhase[i]        = rng.nextFloat() * TWO_PI;
            baseScale[i]         = 1.0f + rng.nextFloat() * 1.5f;
            pulseAmplitude[i]    = 0.15f + rng.nextFloat() * 0.2f;
            bobAmplitude[i]      = 0.05f + rng.nextFloat() * 0.15f;
            bobFreq[i]           = TWO_PI / (15.0f + rng.nextFloat() * 25.0f);
            bobPhase[i]          = rng.nextFloat() * TWO_PI;
        }
    }

    @Override
    public void renderEffect(long appliedAtWorldTime, int amplifier, LivingEntity entity, float delta, MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers, int light) {
        float currentAge = entity.age + delta;
        float entityHeight = entity.getHeight();
        int entityId = entity.getId();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        for (int i = 0; i < BUBBLE_COUNT; i++) {
            float orbitAngle = orbitPhase[i] + currentAge * orbitSpeed[i];
            float height = orbitHeightFactor[i] * entityHeight
                    + bobAmplitude[i] * (float) Math.sin(currentAge * bobFreq[i] + bobPhase[i]);
            float pulseFactor = 1.0f + pulseAmplitude[i]
                    * (float) Math.sin(currentAge * pulseFreq[i] + pulsePhase[i]);
            float scale = baseScale[i] * pulseFactor;

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(orbitAngle));
            matrices.translate(0.0, height, -orbitRadius[i]);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentAge * selfRotSpeedY[i]));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentAge * selfRotSpeedX[i]));
            matrices.scale(scale, scale, scale);
            CustomModels.render(RENDER_LAYER, itemRenderer, MODEL_ID, matrices, vertexConsumers, light, entityId + i + 100);
            matrices.pop();
        }
    }
}
