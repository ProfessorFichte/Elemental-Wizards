package net.elemental_wizards_rpg.spell;

import net.elemental_wizards_rpg.spell.custom_spell_impacts.AvatarImpact;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.event.SpellHandlers;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class CustomSpellImpacts {

    public static void registerCustomImpacts(){
        SpellHandlers.registerCustomImpact(
                Identifier.of(MOD_ID, "avatar_impact"),
                new AvatarImpact()
        );
    }
}
