package net.elemental_wizards_rpg.compat.wizards;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import net.more_rpg_classes.item.MRPGCItems;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WizardMerchantTrades {
    private WizardMerchantTrades() {
    }

    public static Map<Integer, List<TradeOffers.Factory>> build() {
        Map<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();
        trades.put(1, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(MRPGCItems.AQUA_STONE.asItem()), 2, 8, 128, 3, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(MRPGCItems.TERRA_STONE.asItem()), 2, 8, 128, 3, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(MRPGCItems.STORM_STONE.asItem()), 2, 8, 128, 3, 0.1F)
        ));
        trades.put(2, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.elementalStaff.item()), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.clayWand.item()), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.kelpWand.item()), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.featherWand.item()), 4, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.aquaWand.item()), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.terraWand.item()), 18, 1, 12, 18),
                new TradeOffers.SellItemFactory(new ItemStack(WeaponsRegister.windWand.item()), 18, 1, 12, 18)
        ));
        trades.put(3, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(Armors.elementalArmor.armorSet().head), 15, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(Armors.elementalArmor.armorSet().feet), 15, 1, 12, 16, 0.1F)
        ));
        trades.put(4, List.of(
                new TradeOffers.SellItemFactory(new ItemStack(Armors.elementalArmor.armorSet().chest), 20, 1, 12, 16, 0.1F),
                new TradeOffers.SellItemFactory(new ItemStack(Armors.elementalArmor.armorSet().legs), 20, 1, 12, 16, 0.1F)
        ));
        trades.put(5, List.of(
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WeaponsRegister.aquaStaff.item(), 40, 3, 30, 0F).create(entity, random),
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WeaponsRegister.terraStaff.item(), 40, 3, 30, 0F).create(entity, random),
                (entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                        WeaponsRegister.windStaff.item(), 40, 3, 30, 0F).create(entity, random)
        ));
        return trades;
    }
}
