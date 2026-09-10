package net.elemental_wizards_rpg.compat.wizards;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class WizardMerchantTrades {
    private WizardMerchantTrades() {
    }

    /// 1.20.1's `TradeOffers.SellItemFactory` and `SellEnchantedToolFactory` are **package-private** and stay
    /// so after Forge's access transformer; they only compile because some mod on `common`'s classpath
    /// contributes an access widener the production runtime lacks, so calling them throws
    /// `IllegalAccessError` at runtime. Both are rebuilt here on the public `TradeOffer` constructor,
    /// reproducing vanilla's argument order and price multipliers exactly.
    private static TradeOffers.Factory sell(Supplier<Item> item, int price, int count,
                                            int maxUses, int experience, float multiplier) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, price),
                new ItemStack(item.get(), count),
                maxUses, experience, multiplier);
    }

    /// Vanilla's default multiplier for `SellItemFactory` is `0.05F`.
    private static TradeOffers.Factory sell(Supplier<Item> item, int price, int count,
                                            int maxUses, int experience) {
        return sell(item, price, count, maxUses, experience, 0.05F);
    }

    /// Reproduces `SellEnchantedToolFactory#create`: a 5-19 level enchant, price capped at 64 emeralds.
    private static TradeOffers.Factory sellEnchanted(Supplier<Item> item, int basePrice,
                                                     int maxUses, int experience, float multiplier) {
        return (entity, random) -> {
            int level = 5 + random.nextInt(15);
            var tool = EnchantmentHelper.enchant(random, new ItemStack(item.get()), level, false);
            int price = Math.min(basePrice + level, 64);
            return new TradeOffer(new ItemStack(Items.EMERALD, price), tool, maxUses, experience, multiplier);
        };
    }

    public static Map<Integer, List<TradeOffers.Factory>> build() {
        Map<Integer, List<TradeOffers.Factory>> trades = new LinkedHashMap<>();
        trades.put(1, List.of(
                sell(() -> net.more_rpg_classes.item.MRPGCItems.AQUA_STONE.asItem(), 2, 8, 128, 3, 0.1F),
                sell(() -> net.more_rpg_classes.item.MRPGCItems.TERRA_STONE.asItem(), 2, 8, 128, 3, 0.1F),
                sell(() -> net.more_rpg_classes.item.MRPGCItems.STORM_STONE.asItem(), 2, 8, 128, 3, 0.1F)
        ));
        trades.put(2, List.of(
                sell(() -> WeaponsRegister.elementalStaff.item(), 4, 1, 12, 18),
                sell(() -> WeaponsRegister.clayWand.item(), 4, 1, 12, 18),
                sell(() -> WeaponsRegister.kelpWand.item(), 4, 1, 12, 18),
                sell(() -> WeaponsRegister.featherWand.item(), 4, 1, 12, 18),
                sell(() -> WeaponsRegister.aquaWand.item(), 18, 1, 12, 18),
                sell(() -> WeaponsRegister.terraWand.item(), 18, 1, 12, 18),
                sell(() -> WeaponsRegister.windWand.item(), 18, 1, 12, 18)
        ));
        trades.put(3, List.of(
                sell(() -> Armors.elementalArmor.armorSet().head, 15, 1, 12, 16, 0.1F),
                sell(() -> Armors.elementalArmor.armorSet().feet, 15, 1, 12, 16, 0.1F)
        ));
        trades.put(4, List.of(
                sell(() -> Armors.elementalArmor.armorSet().chest, 20, 1, 12, 16, 0.1F),
                sell(() -> Armors.elementalArmor.armorSet().legs, 20, 1, 12, 16, 0.1F)
        ));
        trades.put(5, List.of(
                sellEnchanted(() -> WeaponsRegister.aquaStaff.item(), 40, 3, 30, 0F),
                sellEnchanted(() -> WeaponsRegister.terraStaff.item(), 40, 3, 30, 0F),
                sellEnchanted(() -> WeaponsRegister.windStaff.item(), 40, 3, 30, 0F)
        ));
        return trades;
    }
}
