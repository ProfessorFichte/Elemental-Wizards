package net.elemental_wizards_rpg.mixin;

import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.client.render.ModelEffectOperations;
import net.elemental_wizards_rpg.client.CloudRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;

@Mixin(value = ModelEffectOperations.class, remap = false)
public abstract class ModelEffectOperationsMixin {
    @ModifyVariable(method = "renderEffect", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false, require = 0)
    private static ModelEffect shiftEndAnimations(ModelEffect effect) {
        var cloud = CloudRenderContext.current();
        if (cloud == null || effect.duration <= 0 || effect.model_id == null
                || !effect.model_id.startsWith("elemental_wizards_rpg:") || cloud.isSpawning()) {
            return effect;
        }
        var cloudData = cloud.getCloudData();
        if (cloudData == null) {
            return effect;
        }
        int phaseEnd = cloud.getDataTracker().get(SpellCloudAccessor.endOfPhaseAge());
        int realTotal = cloud.isDespawning() ? phaseEnd : phaseEnd + cloudData.despawn_ticks;
        int delta = realTotal - effect.duration;
        if (delta == 0) {
            return effect;
        }
        var shifted = effect.copy();
        var animations = new ArrayList<ModelEffect.Animation>(effect.animations.size());
        for (var animation : effect.animations) {
            if (animation.end != effect.duration) {
                animations.add(animation);
                continue;
            }
            var moved = new ModelEffect.Animation();
            moved.operation = animation.operation;
            moved.x = animation.x;
            moved.y = animation.y;
            moved.z = animation.z;
            moved.easing = animation.easing;
            moved.start = animation.start + delta;
            moved.end = animation.end + delta;
            animations.add(moved);
        }
        shifted.animations = animations;
        return shifted;
    }
}
