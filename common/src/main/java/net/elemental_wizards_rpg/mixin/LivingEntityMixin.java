package net.elemental_wizards_rpg.mixin;

import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static net.elemental_wizards_rpg.ElementalMod.tweaksConfig;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract float getHealth();

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    public float modifyDamageTakenStoneFlesh(float amount) {
            if(this.hasStatusEffect(ElementalEffects.getEntry(ElementalEffects.STONE_FLESH)) && (this.getHealth() == this.getMaxHealth())){
                return amount * tweaksConfig.value.stone_flesh_full_hp_damage_reduction;
            }

        return amount;
    }
}
