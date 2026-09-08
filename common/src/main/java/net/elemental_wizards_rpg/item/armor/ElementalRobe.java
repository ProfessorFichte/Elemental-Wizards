package net.elemental_wizards_rpg.item.armor;

import net.minecraft.item.ArmorMaterial;
import net.spell_engine.rpg_series.item.Armor;

public class ElementalRobe extends Armor.CustomItem {
    /// 1.20.1: `ArmorMaterial` is a plain interface, not a `RegistryEntry<ArmorMaterial>`.
    public ElementalRobe(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
    }
}
