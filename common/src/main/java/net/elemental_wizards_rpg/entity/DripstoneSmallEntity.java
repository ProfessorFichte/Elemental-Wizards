package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SpellCloud;

public class DripstoneSmallEntity extends SpellCloud {
    public static EntityType<DripstoneSmallEntity> ENTITY_TYPE;

    public DripstoneSmallEntity(EntityType<? extends SpellCloud> entityType, World world) {
        super(entityType, world);
    }


}
