package net.elemental_wizards_rpg.item;

import net.elemental_wizards_rpg.item.armor.Armors;
import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static final Identifier ELEMENTAL_ESSENCE_ID = new Identifier(MOD_ID, "elemental_essence");

    /// Constructed, not registered. `Item`'s constructor creates an intrusive registry holder, so this
    /// class must not be initialized before the registration phase has begun (on Forge: inside the
    /// `RegisterEvent` sequence).
    public static final Item ELEMENTAL_ESSENCE = new Item(new Item.Settings());

    /// Creation only — the items this mod owns outright, keyed by the id they register under. A loader
    /// that registers items itself (Forge) iterates this instead of calling {@link #registerModItems()}.
    public static Map<Identifier, Item> itemsToRegister() {
        var items = new LinkedHashMap<Identifier, Item>();
        if (!Registries.ITEM.containsId(ELEMENTAL_ESSENCE_ID)) {
            items.put(ELEMENTAL_ESSENCE_ID, ELEMENTAL_ESSENCE);
        }
        return items;
    }

    public static void registerModItems(){
        itemsToRegister().forEach((id, item) -> Registry.register(Registries.ITEM, id, item));
    }

}
