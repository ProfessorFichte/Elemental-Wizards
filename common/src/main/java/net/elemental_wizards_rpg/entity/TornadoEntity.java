package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_power.api.SpellPower;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TornadoEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<TornadoEntity> ENTITY_TYPE;

    private static final float PULL_RADIUS = 5.0F;
    private static final float PULL_STRENGTH = 0.15F;
    private static final float LEVITATE_STRENGTH = 0.1F;

    private static final int DAMAGE_INTERVAL = 20;

    private static final TagKey<EntityType<?>> BOSSES_TAG = TagKey.of(
        net.minecraft.registry.RegistryKeys.ENTITY_TYPE,
        Identifier.of("c", "bosses")
    );

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> OWNER_ID_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private int ownerId;
    private int timeToLive;
    private LivingEntity cachedOwner = null;

    public TornadoEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onSpawnedBySpell(Args args) {
        var owner = args.owner();
        var spellId = args.spell().getKey().get().getValue();
        var spawn = args.spawnData();

        this.spellId = spellId;
        this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        this.ownerId = owner.getId();
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.timeToLive = spawn.time_to_live_seconds * 20;
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(OWNER_ID_TRACKER, 0);
        builder.add(TIME_TO_LIVE_TRACKER, 0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        var rawSpellId = this.getDataTracker().get(SPELL_ID_TRACKER);
        if (rawSpellId != null && !rawSpellId.isEmpty()) {
            this.spellId = Identifier.of(rawSpellId);
        }
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Safety check for spellId
        if (nbt.contains("SpellId")) {
            String spellIdStr = nbt.getString("SpellId");
            if (!spellIdStr.isEmpty()) {
                this.spellId = Identifier.of(spellIdStr);
            }
        }

        this.ownerId = nbt.getInt("OwnerId");
        this.timeToLive = nbt.getInt("TimeToLive");

        // Only set spell ID tracker if we have a valid spellId
        if (this.spellId != null) {
            this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        }
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Safety check for spellId
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }

        nbt.putInt("OwnerId", this.ownerId);
        nbt.putInt("TimeToLive", this.timeToLive);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.discard();
                return;
            }

            if (this.age > this.timeToLive) {
                this.discard();
                return;
            }
            pullAndLevitateEntities();

            if (this.age % DAMAGE_INTERVAL == 0) {
                damageEntities();
            }
        }
    }

    private void pullAndLevitateEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        Vec3d tornadoCenter = this.getPos().add(0, 1.0, 0); // Center at tornado height
        Box searchBox = Box.of(tornadoCenter, PULL_RADIUS * 2, PULL_RADIUS * 2, PULL_RADIUS * 2);

        var entities = this.getWorld().getOtherEntities(this, searchBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (entity == owner) continue;

            if (isProtected(owner, entity)) {
                continue;
            }

            if (!canLevitate(entity)) {
                continue;
            }

            Vec3d entityPos = entity.getPos();
            Vec3d direction = tornadoCenter.subtract(entityPos).normalize();

            double horizontalPull = PULL_STRENGTH;
            entity.addVelocity(
                direction.x * horizontalPull,
                LEVITATE_STRENGTH, // Upward levitation
                direction.z * horizontalPull
            );

            entity.velocityModified = true;
        }
    }

    private void damageEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        Vec3d tornadoCenter = this.getPos().add(0, 1.0, 0);
        Box damageBox = Box.of(tornadoCenter, PULL_RADIUS * 2, PULL_RADIUS * 2, PULL_RADIUS * 2);

        var entities = this.getWorld().getOtherEntities(this, damageBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (entity == owner) continue;

            RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "wind_tornado_impact")).get();
            if (!isProtected(owner, entity)) {
                SpellHelper.performImpacts(owner.getWorld(), owner, entity, entity, spellImpact,
                        spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()));
            }
        }
    }

    private boolean canLevitate(Entity entity) {
        return !entity.getType().isIn(BOSSES_TAG);
    }

    public LivingEntity getOwner() {
        if (cachedOwner != null) {
            return cachedOwner;
        }
        var owner = this.getWorld().getEntityById(this.ownerId);
        if (owner instanceof LivingEntity livingOwner) {
            cachedOwner = livingOwner;
            return livingOwner;
        }
        return null;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(3.0F);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    public boolean isProtected(LivingEntity owner, Entity other) {
        if (owner == null) {
            return false;
        }
        var relation = EntityRelations.getRelation(owner, other);
        switch (relation) {
            case ALLY, FRIENDLY -> {
                return true;
            }
            case MIXED, HOSTILE, NEUTRAL -> {
                return false;
            }
        }
        return false;
    }
}