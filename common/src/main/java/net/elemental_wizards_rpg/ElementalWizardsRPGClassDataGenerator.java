package net.elemental_wizards_rpg;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.elemental_wizards_rpg.spell.ElementalWizardSpells;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
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
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ElementalWizardsRPGClassDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(UnsmeltGenerator::new);
		pack.addProvider(SpellGen::new);
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
			disassembleArmor(exporter, Armors.elementalArmor, Items.LAPIS_LAZULI);
			disassembleArmor(exporter, Armors.kelpArmor, Items.PRISMARINE_CRYSTALS);
			disassembleArmor(exporter, Armors.netheriteKelpNetheriteArmor, Items.NETHERITE_SCRAP);
			disassembleArmor(exporter, Armors.dripstoneArmor, Items.POINTED_DRIPSTONE);
			disassembleArmor(exporter, Armors.netheriteDripstoneArmor, Items.NETHERITE_SCRAP);
			disassembleArmor(exporter, Armors.windArmor, Items.WIND_CHARGE);
			disassembleArmor(exporter, Armors.netheriteWindArmor, Items.NETHERITE_SCRAP);

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
}
