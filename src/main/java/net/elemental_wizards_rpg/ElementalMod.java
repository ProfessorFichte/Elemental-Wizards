package net.elemental_wizards_rpg;

import net.elemental_wizards_rpg.compat.ElementalWizardLootTableInjection;
import net.elemental_wizards_rpg.config.EffectsConfig;
import net.elemental_wizards_rpg.effect.Effects;
import net.elemental_wizards_rpg.entity.DripstoneBigEntity;
import net.elemental_wizards_rpg.entity.DripstoneSmallEntity;
import net.elemental_wizards_rpg.entity.TornadoEntity;
import net.elemental_wizards_rpg.item.ElementalGroup;
import net.elemental_wizards_rpg.item.ElementalItems;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.config.Default;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.config.TweaksConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ConfigFile;
import net.tinyconfig.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementalMod implements ModInitializer {
	public static final String MOD_ID = "elemental_wizards_rpg";
    public static final Logger LOGGER = LoggerFactory.getLogger("elemental_wizards_rpg");

	public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
			("equipment", Default.itemConfig)
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static ConfigManager<EffectsConfig> effectsConfig = new ConfigManager<EffectsConfig>
			("effects_v1", new EffectsConfig())
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

	private void registerItemGroup() {
		ElementalGroup.ELEMENTAL_WIZARD = FabricItemGroup.builder()
				.icon(() -> new ItemStack(Armors.kelpArmor.head))
				.displayName(Text.translatable("itemGroup." + MOD_ID + ".general"))
				.build();
		Registry.register(Registries.ITEM_GROUP, ElementalGroup.ELEMENTAL_WIZARD_KEY, ElementalGroup.ELEMENTAL_WIZARD);
	}

	@Override
	public void onInitialize() {
		itemConfig.refresh();
		effectsConfig.refresh();
		tweaksConfig.refresh();
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			tweaksConfig.value.ignore_items_required_mods = true;
		}
		ElementalItems.registerModItems();
		ElementalWizardLootTableInjection.modifyLootTables();
		ElementalGroup.registerItemGroups();
		WeaponsRegister.register(itemConfig.value.weapons);
		Armors.register(itemConfig.value.armor_sets);
		itemConfig.save();
		registerItemGroup();
		Effects.register();
	}
	static {
		DripstoneBigEntity.ENTITY_TYPE = Registry.register(
				Registries.ENTITY_TYPE,
				Identifier.of(MOD_ID, "dripstone_big"),
				FabricEntityTypeBuilder.<DripstoneBigEntity>create(SpawnGroup.MISC, DripstoneBigEntity::new)
						.dimensions(EntityDimensions.changing(6F, 0.5F)) // dimensions in Minecraft units of the render
						.fireImmune()
						.trackRangeBlocks(128)
						.trackedUpdateRate(20)
						.build()
		);
		DripstoneSmallEntity.ENTITY_TYPE = Registry.register(
				Registries.ENTITY_TYPE,
				Identifier.of(MOD_ID, "dripstone_small"),
				FabricEntityTypeBuilder.<DripstoneSmallEntity>create(SpawnGroup.MISC, DripstoneSmallEntity::new)
						.dimensions(EntityDimensions.changing(6F, 0.5F)) // dimensions in Minecraft units of the render
						.fireImmune()
						.trackRangeBlocks(128)
						.trackedUpdateRate(20)
						.build()
		);
		TornadoEntity.ENTITY_TYPE = Registry.register(
				Registries.ENTITY_TYPE,
				Identifier.of(MOD_ID, "tornado"),
				FabricEntityTypeBuilder.<TornadoEntity>create(SpawnGroup.MISC, TornadoEntity::new)
						.dimensions(EntityDimensions.changing(6F, 0.5F))
						.fireImmune()
						.trackRangeBlocks(128)
						.trackedUpdateRate(20)
						.build()
		);
	}
}