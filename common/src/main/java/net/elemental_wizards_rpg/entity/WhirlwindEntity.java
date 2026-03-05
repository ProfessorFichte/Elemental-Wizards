package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
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

public class WhirlwindEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<WhirlwindEntity> ENTITY_TYPE;

    private static final float MOVEMENT_SPEED = 0.5F;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> OWNER_ID_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private int ownerId;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private Vec3d movementDirection;
    private Vec3d startPosition;
    private java.util.Map<Integer, Integer> lastDamageTick = new java.util.HashMap<>();
    private int currentTick = 0;

    public WhirlwindEntity(EntityType<? extends Entity> entityType, World world) {
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

        this.setNoGravity(true);

        this.startPosition = this.getPos();

        Vec3d lookVec = owner.getRotationVec(1.0F);
        this.movementDirection = new Vec3d(
            lookVec.x * MOVEMENT_SPEED,
            0,
            lookVec.z * MOVEMENT_SPEED
        ).normalize().multiply(MOVEMENT_SPEED);
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
        if (nbt.contains("SpellId")) {
            String spellIdStr = nbt.getString("SpellId");
            if (!spellIdStr.isEmpty()) {
                this.spellId = Identifier.of(spellIdStr);
            }
        }

        this.ownerId = nbt.getInt("OwnerId");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.currentTick = nbt.getInt("CurrentTick");

        if (nbt.contains("DirectionX")) {
            this.movementDirection = new Vec3d(
                nbt.getDouble("DirectionX"),
                nbt.getDouble("DirectionY"),
                nbt.getDouble("DirectionZ")
            );
        }

        if (nbt.contains("StartX")) {
            this.startPosition = new Vec3d(
                nbt.getDouble("StartX"),
                nbt.getDouble("StartY"),
                nbt.getDouble("StartZ")
            );
        }

        if (this.spellId != null) {
            this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        }
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }

        nbt.putInt("OwnerId", this.ownerId);
        nbt.putInt("TimeToLive", this.timeToLive);
        nbt.putInt("CurrentTick", this.currentTick);

        if (this.movementDirection != null) {
            nbt.putDouble("DirectionX", this.movementDirection.x);
            nbt.putDouble("DirectionY", this.movementDirection.y);
            nbt.putDouble("DirectionZ", this.movementDirection.z);
        }

        if (this.startPosition != null) {
            nbt.putDouble("StartX", this.startPosition.x);
            nbt.putDouble("StartY", this.startPosition.y);
            nbt.putDouble("StartZ", this.startPosition.z);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            currentTick++;

            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.discard();
                return;
            }

            if (this.age > this.timeToLive) {
                this.discard();
                return;
            }

            if (this.startPosition != null && this.getPos().distanceTo(this.startPosition) > 16.0) {
                this.discard();
                return;
            }
        }

        if (!this.getWorld().isClient() && movementDirection != null) {
            Vec3d nextPos = this.getPos().add(movementDirection);
            net.minecraft.util.math.BlockPos blockPos = net.minecraft.util.math.BlockPos.ofFloored(nextPos);
            var blockState = this.getWorld().getBlockState(blockPos);

            if (!blockState.isAir() && blockState.isSolidBlock(this.getWorld(), blockPos)) {
                this.discard();
                return;
            }

            this.setPosition(nextPos);

            checkCollision();
        }
    }

    private void checkCollision() {
        var owner = this.getOwner();
        if (owner == null) return;

        Box searchBox = this.getBoundingBox().expand(1.0);
        var entities = this.getWorld().getOtherEntities(this, searchBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (entity == owner) continue;

            if (!isProtected(owner, entity)) {
                int entityId = entity.getId();
                Integer lastTick = lastDamageTick.get(entityId);

                if (lastTick == null || currentTick - lastTick >= 10) {
                    RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "terra_drip_circle")).get();
                    SpellHelper.performImpacts(owner.getWorld(), owner, entity, entity, spellImpact,
                            spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()));
                    lastDamageTick.put(entityId, currentTick);
                }
            }
        }
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

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
