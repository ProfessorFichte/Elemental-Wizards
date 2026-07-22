package net.elemental_wizards_rpg.entity;

import net.spell_engine.internals.target.EntityRelations;

public class EWizardsTeamMatcher {
    public static void register(){
        EntityRelations.registerTeamMatcher("elemental_wizards_golem", (entity1, entity2) -> {
            if (entity2 instanceof EarthGolemEntity golem) {
                var owner = golem.getOwner();
                if (owner != null && owner == entity1) {
                    return new EntityRelations.TeamRelation(true, false);
                }
            }
            return null;
        });
    }
}
