package net.elemental_wizards_rpg.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.effect.Synchronized;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;
import static net.elemental_wizards_rpg.ElementalMod.effectsConfig;

public class Effects {
    private static final ArrayList<Entry> entries = new ArrayList<>();
    public static class Entry {
        public final Identifier id;
        public final StatusEffect effect;
        public RegistryEntry<StatusEffect> registryEntry;
        public Entry(String name, StatusEffect effect) {
            this.id = Identifier.of(MOD_ID, name);
            this.effect = effect;
            entries.add(this);
        }
        public void register() {
            registryEntry = Registry.registerReference(Registries.STATUS_EFFECT, id, effect);
        }
        public Identifier modifierId() {
            return Identifier.of(MOD_ID, "effect." + id.getPath());
        }
    }

    //WATER EFFECTS
    public static final Entry CLEANSING_WATER  =  new Entry("cleansing_water",new CleansingWaterEffect(StatusEffectCategory.BENEFICIAL, 0x01d9cf));
    public static final Entry BUBBLE_FOAM  = new Entry("bubble_foam",new BubbleFoamEffect(StatusEffectCategory.BENEFICIAL, 0x01d9cf));

    //EARTH EFFECTS
    public static final Entry STONE_FLESH   =  new Entry("stone_flesh",new StoneFleshEffect(StatusEffectCategory.BENEFICIAL, 0xbd8b00));

    //AIR EFFECTS
    public static final Entry UPDRAFT= new Entry("updraft",new UpdraftEffect(StatusEffectCategory.HARMFUL, 0xd5ebff)
            .setVulnerability(MoreSpellSchools.AIR, new SpellPower.Vulnerability(
                    effectsConfig.value.updraft_air_damage_vulnerability,
                    effectsConfig.value.updraft_air_spell_crit_chance_vulnerability,
                    effectsConfig.value.updraft_air_spell_crit_damage_vulnerability)));
    public static final Entry TORNADO = new Entry("tornado",new TornadoEffect(StatusEffectCategory.HARMFUL, 0xd5ebff)
            .setVulnerability(SpellSchools.FIRE, new SpellPower.Vulnerability(
                    effectsConfig.value.tornado_fire_spell_vulnerability, 0, 0)));



    public static void register(){
        STONE_FLESH.effect.addAttributeModifier(
                        EntityAttributes.GENERIC_ARMOR, STONE_FLESH.modifierId(),
                        effectsConfig.value.stone_flesh_armor_increase, EntityAttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, STONE_FLESH.modifierId(),
                        effectsConfig.value.stone_flesh_armor_toughness_increase, EntityAttributeModifier.Operation.ADD_VALUE);
        TORNADO.effect.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,TORNADO.modifierId(),
                -0.99F,EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        UPDRAFT.effect.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_SPEED,UPDRAFT.modifierId(),
                effectsConfig.value.updraft_attack_speed_reduction,EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);


        Synchronized.configure(CLEANSING_WATER.effect,true);
        Synchronized.configure(BUBBLE_FOAM.effect,true);
        Synchronized.configure(STONE_FLESH.effect,true);
        Synchronized.configure(UPDRAFT.effect,true);
        Synchronized.configure(TORNADO.effect,true);

        for (var entry: entries) {
            entry.register();
        }
    }
}
