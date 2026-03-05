package net.elemental_wizards_rpg;

import net.elemental_wizards_rpg.datagen.ElementalAdvancementDataGen;
import net.elemental_wizards_rpg.datagen.ElementalVanillaAdvancementProvider;
import net.elemental_wizards_rpg.datagen.ModModelProvider;
import net.elemental_wizards_rpg.datagen.WeaponAttributesGenerator;
import net.elemental_wizards_rpg.effect.ElementalEffects;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.tags.SpellTags;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;


public class ElementalWizardsRPGClassDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		ElementalVanillaAdvancementProvider.init();
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(SpellTagGenerator::new);
		pack.addProvider(UnsmeltGenerator::new);
		pack.addProvider(SpellGen::new);
		pack.addProvider(LangGenerator::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(WeaponAttributesGenerator::new);
		pack.addProvider(ElementalVanillaAdvancementProvider::new);
		pack.addProvider(ElementalAdvancementDataGen::new);
		// Recipe Providers
		pack.addProvider(net.elemental_wizards_rpg.datagen.ElementalRecipeProvider::new);
		pack.addProvider(net.elemental_wizards_rpg.datagen.ElementalSmithingRecipeProvider::new);
	}
	public static class SpellGen extends SpellGenerator {
		public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateSpells(Builder builder) {
			for (var entry: ElementalWizardSpells.entries) {
				builder.add(entry.id(), entry.spell());
			}
		}
	}

	public static class LangGenerator extends FabricLanguageProvider {
		protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
			// Item Group
			translationBuilder.add("itemGroup.elemental_wizards_rpg.general", "Elemental Wizards");

			// Misc Items
			translationBuilder.add("item.elemental_wizards_rpg.elemental_essence", "Elemental Essence");

			// Spell Books and Scrolls
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/aqua", "Tome of Water");
			translationBuilder.add("item.elemental_wizards_rpg.spell_scroll/aqua", "Water Spell Scroll");
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/aqua.spell_binding.description",
					"Spell Book of Water Wizards, using water magic to damage foes and protect allies.\n- Strengths: Balanced healing and damage.\n- Weaknesses: Low defense and mobility\n- Equipment: Light Armor");
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/terra", "Tome of Earth");
			translationBuilder.add("item.elemental_wizards_rpg.spell_scroll/terra", "Earth Spell Scroll");
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/terra.spell_binding.description",
					"Spell Book of Earth Wizards, using earth magic to form dangerous and harming terrain.\n- Strengths: Crowd Control and protection.\n- Weaknesses: Limited mobility and range\n- Equipment: Light Armor");
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/wind", "Tome of Wind");
			translationBuilder.add("item.elemental_wizards_rpg.spell_scroll/wind", "Wind Spell Scroll");
			translationBuilder.add("item.elemental_wizards_rpg.spell_book/wind.spell_binding.description",
					"Spell Book of Wind Wizards, using wind magic to perform dangerous combos on enemies.\n- Strengths: Strong CC and damage on single targets.\n- Weaknesses: Low defense and no mobility\n- Equipment: Light Armor");


			// Weapons (from Entry translatedName)
			WeaponsRegister.entries.forEach(entry ->
				translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
			);

			// Armors (from Entry translatedName)
			Armors.entries.forEach(entry -> {
				var translations = new LinkedHashMap<String, String>();
				translations.put(((Item)entry.armorSet().head).getTranslationKey(), entry.armorSet().headTranslation);
				translations.put(((Item)entry.armorSet().chest).getTranslationKey(), entry.armorSet().chestTranslation);
				translations.put(((Item)entry.armorSet().legs).getTranslationKey(), entry.armorSet().legsTranslation);
				translations.put(((Item)entry.armorSet().feet).getTranslationKey(), entry.armorSet().feetTranslation);
				for (var armorEntry: translations.entrySet()) {
					translationBuilder.add(armorEntry.getKey(), armorEntry.getValue());
				}
			});

			// Effects
			ElementalEffects.entries.forEach(entry -> {
				translationBuilder.add(entry.effect.getTranslationKey(), entry.title);
				if (!entry.description.isEmpty()) {
					translationBuilder.add(entry.effect.getTranslationKey() + ".description", entry.description);
				}
			});

			// Spells
			ElementalWizardSpells.entries.forEach(entry -> {
				var id = entry.id();
				translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".name", entry.title());
				translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".description", entry.description());
			});

			// Entities
			translationBuilder.add("entity.elemental_wizards_rpg.dripstone_big", "Dripstone");
			translationBuilder.add("entity.elemental_wizards_rpg.dripstone_small", "Small Dripstone");
			translationBuilder.add("entity.elemental_wizards_rpg.terra_stone", "Terra Stone");
			translationBuilder.add("entity.elemental_wizards_rpg.earth_golem_spike", "Earth Golem Spike");
			translationBuilder.add("entity.elemental_wizards_rpg.earthquake", "Earthquake");
			translationBuilder.add("entity.elemental_wizards_rpg.tornado", "Tornado");
			translationBuilder.add("entity.elemental_wizards_rpg.whirlwind", "Whirlwind");
			translationBuilder.add("entity.elemental_wizards_rpg.storm_draft", "Storm Draft");
			translationBuilder.add("entity.elemental_wizards_rpg.tidal_wave", "Tidal Wave");
			translationBuilder.add("entity.elemental_wizards_rpg.healing_rain_cloud", "Healing Rain Cloud");
			translationBuilder.add("entity.elemental_wizards_rpg.earth_golem", "Earth Golem");

			// Equipment Sets
			translationBuilder.add("equipment_set.elemental_wizards_rpg.hurricane", "Eye of the Hurricane");
			translationBuilder.add("equipment_set.elemental_wizards_rpg.mountain", "Mountains Wrath");
			translationBuilder.add("equipment_set.elemental_wizards_rpg.ocean", "Oceans Grace");
			// Advancements
			for (var entry : ElementalAdvancementDataGen.getEntries()) {
				translationBuilder.add(entry.titleKey(), entry.title());
				translationBuilder.add(entry.descriptionKey(), entry.description());
			}
			for (var entry : ElementalVanillaAdvancementProvider.getEntries()) {
				translationBuilder.add(entry.titleKey(), entry.title());
				translationBuilder.add(entry.descriptionKey(), entry.description());
			}
		}
	}

	public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
		public ItemTagGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		public void armoryTags(List<Armor.Entry> armors) {
			this.armoryTags(armors, EnumSet.noneOf(RPGSeriesItemTags.ArmorMetaType.class));
		}

		public void armoryTags(List<Armor.Entry> armors, RPGSeriesItemTags.ArmorMetaType metaType) {
			this.armoryTags(armors, EnumSet.of(metaType));
		}

		public void armoryTags(List<Armor.Entry> armors, EnumSet<RPGSeriesItemTags.ArmorMetaType> metaTypes) {
			Iterator var3 = armors.iterator();

			while(var3.hasNext()) {
				Armor.Entry armor = (Armor.Entry)var3.next();
				Armor.Set set = armor.armorSet();
				FabricTagProvider<Item>.FabricTagBuilder headTag = this.getOrCreateTagBuilder(ItemTags.HEAD_ARMOR);
				headTag.addOptional(set.idOf(set.head));
				FabricTagProvider<Item>.FabricTagBuilder chestTag = this.getOrCreateTagBuilder(ItemTags.CHEST_ARMOR);
				chestTag.addOptional(set.idOf(set.chest));
				FabricTagProvider<Item>.FabricTagBuilder legsTag = this.getOrCreateTagBuilder(ItemTags.LEG_ARMOR);
				legsTag.addOptional(set.idOf(set.legs));
				FabricTagProvider<Item>.FabricTagBuilder feetTag = this.getOrCreateTagBuilder(ItemTags.FOOT_ARMOR);
				feetTag.addOptional(set.idOf(set.feet));
				Iterator var12;

				String lootTheme = armor.lootProperties().theme();
				if (lootTheme != null && !lootTheme.isEmpty()) {
					FabricTagProvider<Item>.FabricTagBuilder themeTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootThemes.get(lootTheme));
					Iterator var19 = armor.armorSet().pieceIds().iterator();

					while(var19.hasNext()) {
						Object id = var19.next();
						themeTag.addOptional((Identifier)id);
					}
				}

				var12 = metaTypes.iterator();

				while(var12.hasNext()) {
					RPGSeriesItemTags.ArmorMetaType metaType = (RPGSeriesItemTags.ArmorMetaType)var12.next();
					FabricTagProvider<Item>.FabricTagBuilder metaTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.ArmorType.get(metaType));
					Iterator var15 = armor.armorSet().pieceIds().iterator();

					while(var15.hasNext()) {
						Object id = var15.next();
						metaTag.addOptional((Identifier)id);
					}
				}
			}

		}

		List<String> armoryKeywords = List.of("hurricane", "ocean","mountain");
		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			generateWeaponTags(WeaponsRegister.entries);
			var armorTagOptions1 = new ArmorOptions(false, true);
			var armorTagOptions2 = new ArmorOptions(true, true);
			armoryTags(
					Armors.entries.stream().filter(entry -> armoryKeywords.stream().anyMatch(entry.name()::contains)).toList(),
					RPGSeriesItemTags.ArmorMetaType.MAGIC
			);
			generateArmorTags(
					Armors.entries.stream().filter(entry -> armoryKeywords.stream().noneMatch(entry.name()::contains)).toList(),
					RPGSeriesItemTags.ArmorMetaType.MAGIC,
					armorTagOptions2
			);
		}
	}

	public static class UnsmeltGenerator extends FabricRecipeProvider {
		public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		public static int UNSMELT_TIME = 300;

		@Override
		public void generate(RecipeExporter exporter) {
			disassembleArmor(exporter, Armors.elementalArmor.armorSet(), Items.LAPIS_LAZULI);
			disassembleArmor(exporter, Armors.kelpArmor.armorSet(), Items.PRISMARINE_CRYSTALS);
			disassembleArmor(exporter, Armors.netheriteKelpNetheriteArmor.armorSet(), Items.NETHERITE_SCRAP);
			disassembleArmor(exporter, Armors.dripstoneArmor.armorSet(), Items.POINTED_DRIPSTONE);
			disassembleArmor(exporter, Armors.netheriteDripstoneArmor.armorSet(), Items.NETHERITE_SCRAP);
			disassembleArmor(exporter, Armors.windArmor.armorSet(), Items.WIND_CHARGE);
			disassembleArmor(exporter, Armors.netheriteWindArmor.armorSet(), Items.NETHERITE_SCRAP);

			disassemble(exporter,
					List.of(WeaponsRegister.aquaWand.item(), WeaponsRegister.windWand.item()),
					Items.GOLD_NUGGET);
			disassemble(exporter,
					List.of(WeaponsRegister.terraWand.item()),
					Items.IRON_NUGGET);

			disassemble(exporter,
					List.of(WeaponsRegister.aquaStaff.item()),
					Items.PRISMARINE_CRYSTALS);
			disassemble(exporter,
					List.of(WeaponsRegister.windStaff.item()),
					Items.WIND_CHARGE);
			disassemble(exporter,
					List.of(WeaponsRegister.terraStaff.item()),
					Items.DIAMOND);

			disassemble(exporter,
					WeaponsRegister.entries.stream()
							.filter(entry -> entry.id().getPath().contains("netherite"))
							.map(entry -> (ItemConvertible) entry.item()).toList(),
					Items.NETHERITE_SCRAP);
		}

		private static void disassembleArmor(RecipeExporter exporter, Armor.Set armorSet, Item output) {
			FabricRecipeProvider.offerSmelting(exporter,
					armorSet.pieces(),
					RecipeCategory.MISC,
					output,
					0.1f,
					UNSMELT_TIME,
					"disassemble"
			);
			FabricRecipeProvider.offerBlasting(exporter,
					armorSet.pieces(),
					RecipeCategory.MISC,
					output,
					0.1f,
					UNSMELT_TIME / 2,
					"disassemble"
			);
		}

		private static void disassemble(RecipeExporter exporter, List<ItemConvertible> items, Item output) {
			FabricRecipeProvider.offerSmelting(exporter,
					items,
					RecipeCategory.MISC,
					output,
					0.1f,
					UNSMELT_TIME,
					"disassemble"
			);
			FabricRecipeProvider.offerBlasting(exporter,
					items,
					RecipeCategory.MISC,
					output,
					0.1f,
					UNSMELT_TIME / 2,
					"disassemble"
			);
		}
	}

	public static class SpellTagGenerator extends FabricTagProvider<Spell> {
		public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, SpellRegistry.KEY, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			var namespace = MOD_ID;
			var treasureTagBuilder = getOrCreateTagBuilder(SpellTags.TREASURE);
			var processedBooks = new HashSet<ElementalWizardSpells.Book>();
			ElementalWizardSpells.entries.forEach(entry -> {
				if (entry.book() != null) {
					var bookTagKey = SpellTags.spellBook(namespace, entry.book().toString().toLowerCase());
					var bookTag = getOrCreateTagBuilder(bookTagKey);
					bookTag.addOptional(entry.id());
					var scrollTagKey = SpellTags.spellScroll(namespace, entry.book().toString().toLowerCase());
					var scrollTag = getOrCreateTagBuilder(scrollTagKey);
					scrollTag.addOptional(entry.id());
					if (processedBooks.add(entry.book())) {
						treasureTagBuilder.addOptionalTag(scrollTagKey);
					}
				}
				for (var group : entry.weaponGroups()) {
					var weaponGroupTagKey = SpellTags.weapon(namespace, group.toString().toLowerCase());
					var weaponGroupTag = getOrCreateTagBuilder(weaponGroupTagKey);
					weaponGroupTag.addOptional(entry.id());
				}
			});
		}
	}
}
