package net.elemental_wizards_rpg;

import net.spell_engine.Platform;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.entity.ElementalSummons;
import net.elemental_wizards_rpg.entity.ModEntitiesRegistry;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.item.ElementalItems;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.config.Default;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.config.TweaksConfig;
import net.elemental_wizards_rpg.particle.ModParticles;
import net.elemental_wizards_rpg.spell.CustomSpellImpacts;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;
import net.tiny_config.ConfigManager;

public class ElementalMod {
	public static final String MOD_ID = "elemental_wizards_rpg";

	public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
			("equipment_v1", Default.itemConfig)
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static ConfigManager<ConfigFile.Effects> effectsConfig = new ConfigManager<>
			("effects_v2", new ConfigFile.Effects())
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
			("tweaks", new TweaksConfig())
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static ConfigManager<SummonedEntityConfig> summonConfig = new ConfigManager<>
			("summoned_entities", seededSummonDefaults())
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();

	private static SummonedEntityConfig seededSummonDefaults() {
		var config = new SummonedEntityConfig();
		config.entries.put(ModEntitiesRegistry.EARTH_GOLEM_ID.toString(), ElementalSummons.earthGolemDefaults());
		return config;
	}

	public static void init() {
		itemConfig.refresh();
		effectsConfig.refresh();
		tweaksConfig.refresh();
		summonConfig.refresh();
		if (Platform.util().isDevelopmentEnvironment()) {
			tweaksConfig.value.ignore_items_required_mods = true;
		}
		CustomSpellImpacts.registerCustomImpacts();
	}
	public static void registerItems() {
		ElementalItems.registerModItems();
		WeaponsRegister.register(itemConfig.value.weapons);
		Armors.register(itemConfig.value.armor_sets);
		itemConfig.save();
	}
	public static void registerEffects() {
		ElementalEffects.register(effectsConfig.value);
		effectsConfig.save();
	}
	public static void registerEntities() {
		ModEntitiesRegistry.registerEntities();
	}
	public static void registerParticles() {
		ModParticles.register();
	}
	public static void registerSounds() {
		ElementalSounds.register();
	}
}