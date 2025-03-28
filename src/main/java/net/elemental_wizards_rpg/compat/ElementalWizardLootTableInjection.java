package net.elemental_wizards_rpg.compat;

import net.elemental_wizards_rpg.item.ElementalItems;
import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.item.MRPGCItems;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class ElementalWizardLootTableInjection {
    private static final Identifier WIZARDS_VILLAGER =
            Identifier.of("wizards", "chests/village_wizard");

    public static void modifyLootTables(){
        LootTableEvents.MODIFY.register(new LootTableEvents.Modify() {
            @Override
            public void modifyLootTable(RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
                if (source.isBuiltin() && WIZARDS_VILLAGER.equals(key)) {

                    LootPool.Builder poolBuilder0 = LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(2))
                            .conditionally(RandomChanceLootCondition.builder(0.45F))
                            .with(ItemEntry.builder(MRPGCItems.AQUA_STONE))
                            .with(ItemEntry.builder(MRPGCItems.TERRA_STONE))
                            .with(ItemEntry.builder(MRPGCItems.STORM_STONE))
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0f, 16.0f)).build());
                    tableBuilder.pool(poolBuilder0.build());
                    LootPool.Builder poolBuilder1 = LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(2))
                            .conditionally(RandomChanceLootCondition.builder(0.25F))
                            .with(ItemEntry.builder(WeaponsRegister.elementalStaff.item()))
                            .with(ItemEntry.builder(WeaponsRegister.kelpWand.item()))
                            .with(ItemEntry.builder(WeaponsRegister.clayWand.item()))
                            .with(ItemEntry.builder(WeaponsRegister.featherWand.item()))
                            .with(ItemEntry.builder(Armors.elementalArmor.chest.asItem()))
                            .with(ItemEntry.builder(Armors.elementalArmor.head.asItem()))
                            .with(ItemEntry.builder(Armors.elementalArmor.feet.asItem()))
                            .with(ItemEntry.builder(Armors.elementalArmor.legs.asItem()))
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                    tableBuilder.pool(poolBuilder1.build());
                    LootPool.Builder poolBuilder2 = LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .conditionally(RandomChanceLootCondition.builder(0.2F))
                            .with(ItemEntry.builder(ElementalItems.ELEMENTAL_ESSENCE))
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)).build());
                    tableBuilder.pool(poolBuilder2.build());
                }
                }
        });
    }
}
