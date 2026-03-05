package net.elemental_wizards_rpg.client.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.elemental_wizards_rpg.entity.EarthquakeEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.RotationAxis;

public class EarthquakeEntityRenderer extends EntityRenderer<EarthquakeEntity> {
    private final BlockRenderManager blockRenderManager;

    public EarthquakeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(EarthquakeEntity entity) {
        return null;
    }

    @Override
    public void render(EarthquakeEntity entity, float yaw, float tickDelta, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);

        Vec3d entityPos = entity.getPos();
        MinecraftClient client = MinecraftClient.getInstance();

        var shakingBlocks = entity.getShakingBlocks();

        if (shakingBlocks.isEmpty()) {
            return;
        }

        float time = entity.age + tickDelta;

        for (EarthquakeEntity.ShakingBlockData blockData : shakingBlocks) {
            BlockPos blockPos = blockData.pos;
            BlockState blockState = blockData.state;

            if (blockState.getRenderType() != BlockRenderType.MODEL) {
                continue;
            }

            matrixStack.push();

            double relativeX = blockPos.getX() - entityPos.x;
            double relativeY = blockPos.getY() - entityPos.y;
            double relativeZ = blockPos.getZ() - entityPos.z;

            matrixStack.translate(relativeX, relativeY, relativeZ);

            float primaryTime = time * blockData.shakeSpeed;
            float shakeOffset = (float) Math.sin(primaryTime + blockData.shakePhase) * blockData.shakeHeight;

            float secondaryFreq = 0.5F + (blockData.shakePhase / (float) Math.PI) * 0.3F;
            float secondaryShake = (float) Math.sin(time * secondaryFreq + blockData.shakePhase * 2.7F) * blockData.shakeHeight * 0.15F;

            float joltTime = primaryTime * 0.3F + blockData.shakePhase;
            float jolt = 0;
            float joltSin = (float) Math.sin(joltTime);
            if (joltSin > 0.85F) {
                jolt = (joltSin - 0.85F) * blockData.shakeHeight * 2.0F;
            }

            float totalVertical = shakeOffset + secondaryShake + jolt;
            float minOffset = 0.03F;
            totalVertical = Math.max(minOffset, totalVertical);
            matrixStack.translate(0, totalVertical, 0);

            float wobbleTime = time * blockData.shakeSpeed * 0.6F;
            float wobbleX = (float) Math.sin(wobbleTime + blockData.shakePhase) * 0.04F * blockData.shakeHeight;
            float wobbleZ = (float) Math.cos(wobbleTime * 1.3F + blockData.shakePhase + 1.0F) * 0.04F * blockData.shakeHeight;
            float microTremor = (float) Math.sin(time * 0.8F + blockData.shakePhase * 3.0F) * 0.01F;
            matrixStack.translate(wobbleX + microTremor, 0, wobbleZ + microTremor * 0.7F);

            if (blockData.shakeHeight > 0.25F) {
                float tiltAngle = (float) Math.sin(wobbleTime + blockData.shakePhase) * 2.0F;
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tiltAngle));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tiltAngle * 0.7F));
            }

            int fullBright = LightmapTextureManager.MAX_LIGHT_COORDINATE;

            blockRenderManager.renderBlockAsEntity(
                blockState,
                matrixStack,
                vertexConsumers,
                fullBright,
                OverlayTexture.DEFAULT_UV
            );

            matrixStack.pop();
        }
    }

    @Override
    protected boolean hasLabel(EarthquakeEntity entity) {
        return false;
    }
}
