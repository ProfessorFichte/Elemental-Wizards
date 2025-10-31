package net.elemental_wizards_rpg.item;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.SpellBooks;

import java.util.HashMap;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class ElementalItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: WeaponsRegister.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var entry: Armors.entries) {
            var set = entry.armorSet();
            for (var piece: set.pieces()) {
                var armorItem = (ArmorItem) piece;
                entries.put(set.idOf(armorItem).toString(), armorItem);
            }
        }
    }



    public static final Item ELEMENTAL_ESSENCE = registerItem("elemental_essence", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name), item);
    }

    public static void registerModItems(){
        SpellBooks.createAndRegister(Identifier.of(MOD_ID,"aqua"), ElementalGroup.ELEMENTAL_WIZARD_KEY);
        SpellBooks.createAndRegister(Identifier.of(MOD_ID,"terra"), ElementalGroup.ELEMENTAL_WIZARD_KEY);
        SpellBooks.createAndRegister(Identifier.of(MOD_ID,"wind"), ElementalGroup.ELEMENTAL_WIZARD_KEY);

    }

}
