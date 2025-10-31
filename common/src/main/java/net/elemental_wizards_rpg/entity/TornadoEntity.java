package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SpellCloud;

public class TornadoEntity extends SpellCloud {
    public static EntityType<TornadoEntity> ENTITY_TYPE;

    public TornadoEntity(EntityType<? extends SpellCloud> entityType, World world) {
        super(entityType, world);
    }
}