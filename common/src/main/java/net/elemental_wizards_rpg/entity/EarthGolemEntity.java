package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spell_engine.entity.SummonedEntity;

public class EarthGolemEntity extends SummonedEntity {
    public static EntityType<EarthGolemEntity> ENTITY_TYPE;

    public EarthGolemEntity(EntityType<? extends EarthGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    // Wider than vanilla default: this golem is 2.6 blocks wide and stutter-steps on single-block terrain otherwise.
    @Override
    public float getStepHeight() {
        return 1.1f;
    }
}
