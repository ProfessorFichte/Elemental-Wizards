package net.elemental_wizards_rpg.item.config;

import net.elemental_wizards_rpg.item.weapons.WeaponsRegister;
import net.spell_engine.rpg_series.config.ConfigFile;

public class Default {
    public static final ConfigFile.Equipment itemConfig;

    static{
        itemConfig = new ConfigFile.Equipment();
        for (var weapon: WeaponsRegister.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
    }
}
