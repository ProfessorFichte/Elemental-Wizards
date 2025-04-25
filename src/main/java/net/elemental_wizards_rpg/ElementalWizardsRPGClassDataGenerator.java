package net.elemental_wizards_rpg;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ElementalWizardsRPGClassDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(UnsmeltGenerator::new);
	}
	public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
		public ItemTagGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			generateWeaponTags(WeaponsRegister.entries);
			generateArmorTags(Armors.entries);
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
