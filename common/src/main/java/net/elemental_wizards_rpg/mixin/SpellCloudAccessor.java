package net.elemental_wizards_rpg.mixin;

import net.minecraft.entity.data.TrackedData;
import net.spell_engine.entity.SpellCloud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SpellCloud.class, remap = false)
public interface SpellCloudAccessor {
    @Accessor(value = "END_OF_PHASE_AGE", remap = false)
    static TrackedData<Integer> endOfPhaseAge() {
        throw new AssertionError();
    }
}
