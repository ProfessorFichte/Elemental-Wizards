package net.elemental_wizards_rpg.client;

import net.rpg_foundation.armor_api.client.ArmorRenderers;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;
import net.elemental_wizards_rpg.client.armor.ElementalRobeRenderer;
import net.elemental_wizards_rpg.client.effect.*;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.particle.ModParticles;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleType;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.client.particle.SpellParticle;
import net.spell_engine.rpg_series.item.Armor;

import java.util.function.Function;

import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;


public class ElementalClient{

    public static void init(){
        ElementalWizardSpells.registerTooltipTokens();

        registerArmorRenderer(Armors.elementalArmor.armorSet(), ElementalRobeRenderer.elemental());
        registerArmorRenderer(Armors.kelpArmor.armorSet(), ElementalRobeRenderer.kelp());
        registerArmorRenderer(Armors.dripstoneArmor.armorSet(), ElementalRobeRenderer.dripstone());
        registerArmorRenderer(Armors.windArmor.armorSet(), ElementalRobeRenderer.wind());
        registerArmorRenderer(Armors.netheriteKelpNetheriteArmor.armorSet(), ElementalRobeRenderer.netherite_kelp());
        registerArmorRenderer(Armors.netheriteDripstoneArmor.armorSet(), ElementalRobeRenderer.netherite_dripstone());
        registerArmorRenderer(Armors.netheriteWindArmor.armorSet(), ElementalRobeRenderer.netherite_wind());

        if (armoryLoadCheck()) {
            registerArmorRenderer(Armors.hurricaneArmorSet.armorSet(), ElementalRobeRenderer.hurricane());
            registerArmorRenderer(Armors.mountainArmorSet.armorSet(), ElementalRobeRenderer.mountain());
            registerArmorRenderer(Armors.oceanArmorSet.armorSet(), ElementalRobeRenderer.ocean());
        }

        CustomModelStatusEffect.register(ElementalEffects.BUBBLE_FOAM.effect, new BubbleFoamEffectRenderer());
        CustomModelStatusEffect.register(ElementalEffects.STONE_FLESH.effect, new StoneFleshEffectRenderer());
        CustomParticleStatusEffect.register(ElementalEffects.CLEANSING_WATER.effect, new CleansingWaterParticleSpawner());
        CustomParticleStatusEffect.register(ElementalEffects.BUBBLE_FOAM.effect, new BubbleFoamParticleSpawner());
        CustomParticleStatusEffect.register(ElementalEffects.STONE_FLESH.effect, new StoneFleshParticleSpawner());

        CustomModelStatusEffect.register(ElementalEffects.IMPALED.effect, new ImpaledRenderer());
    }
    private static void registerArmorRenderer(Armor.Set set, GeoArmorRenderer renderer) {
        ArmorRenderers.register(renderer, set.head, set.chest, set.legs, set.feet);
    }

    @FunctionalInterface
    public interface ParticleFactoryRegistrar {
        void register(ParticleType type, Function<SpriteProvider, ParticleFactory> factory);
    }

    public static void registerParticleAppearances(ParticleFactoryRegistrar registrar) {
        for (var entry: ModParticles.entries()) {
            registrar.register(entry.type(), provider -> new SpellParticle.Factory(provider, entry));
        }
    }
}
