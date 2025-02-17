package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SpellCloud;


public class DripstoneBigEntity extends SpellCloud {
    public static EntityType<DripstoneBigEntity> ENTITY_TYPE;

    public DripstoneBigEntity(EntityType<? extends SpellCloud> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

}
