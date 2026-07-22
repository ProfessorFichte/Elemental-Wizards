package net.elemental_wizards_rpg;

import net.elemental_wizards_rpg.compat.WizardsCompat;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.entity.EWizardsTeamMatcher;
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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;
import static net.elemental_wizards_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

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

	public static void init() {
		itemConfig.refresh();
		effectsConfig.refresh();
		tweaksConfig.refresh();
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			tweaksConfig.value.ignore_items_required_mods = true;
		}
		CustomSpellImpacts.registerCustomImpacts();
		WizardsCompat.registerCompat();
		EWizardsTeamMatcher.register();
	}
	public static void registerItems() {
		ElementalGroup.ELEMENTAL_WIZARD = FabricItemGroup.builder()
				.icon(() -> new ItemStack(Armors.kelpArmor.armorSet().head))
				.displayName(Text.translatable("itemGroup." + MOD_ID + ".general"))
				.build();
		Registry.register(Registries.ITEM_GROUP, ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
		ElementalItems.registerModItems();
		ElementalGroup.registerItemGroups();
		WeaponsRegister.register(itemConfig.value.weapons);
		Armors.register(itemConfig.value.armor_sets);
		if (armoryLoadCheck()) {
			FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
				ResourceManagerHelper.registerBuiltinResourcePack(
						Identifier.of(MOD_ID, "elemental_wizards_armory_compat"),
						modContainer,
						ResourcePackActivationType.ALWAYS_ENABLED
				);
			});
		}
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