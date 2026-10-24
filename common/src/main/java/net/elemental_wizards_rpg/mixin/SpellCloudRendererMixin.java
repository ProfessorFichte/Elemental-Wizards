package net.elemental_wizards_rpg.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.render.SpellCloudRenderer;
import net.spell_engine.entity.SpellCloud;
import net.elemental_wizards_rpg.client.CloudRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpellCloudRenderer.class, remap = false)
public abstract class SpellCloudRendererMixin {
    @Inject(method = "renderModelFx", at = @At("HEAD"), remap = false, require = 0)
    private void captureCloud(SpellCloud cloud, Spell.Delivery.Cloud.ClientData data, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        CloudRenderContext.set(cloud);
    }

    @Inject(method = "renderModelFx", at = @At("RETURN"), remap = false, require = 0)
    private void releaseCloud(SpellCloud cloud, Spell.Delivery.Cloud.ClientData data, float tickDelta,
                              MatrixStack matrices, VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        CloudRenderContext.clear();
    }
}
