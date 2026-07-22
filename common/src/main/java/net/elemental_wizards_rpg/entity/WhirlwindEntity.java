package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.entity.util.PeriodicAreaImpact;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.more_rpg_classes.util.CustomMethods;

import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class WhirlwindEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<WhirlwindEntity> ENTITY_TYPE;
    private static final Identifier TWISTER_SPELL_ID = Identifier.of(MOD_ID, "wind_twister");

    private static final float FORWARD_SPEED = 0.25F;
    private static final float MIN_RADIUS = 1.1F;
    private static final float MAX_RADIUS = 4.0F;
    private static final float RADIUS_GROWTH = 0.15F;
    private static final float ANGULAR_SPEED = 0.28F;
    private static final int DAMAGE_INTERVAL = 20;
    private static final float DEFAULT_MAX_TRAVEL_DISTANCE = 16.0F;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> START_X_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> START_Y_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> START_Z_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DIRECTION_YAW_TRACKER = DataTracker.registerData(WhirlwindEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private RegistryEntry<Spell> spellEntry = null;
    private float maxTravelDistance = DEFAULT_MAX_TRAVEL_DISTANCE;
    private Vec3d movementDirection;
    private Vec3d rightDirection;
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
        this.ownerUuid = owner.getUuid();
        this.cachedOwner = owner;
        this.timeToLive = spawn.time_to_live_seconds * 20;
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);

        this.setNoGravity(true);

        this.startPosition = this.getPos();

        Vec3d lookVec = owner.getRotationVec(1.0F);
        this.movementDirection = new Vec3d(lookVec.x, 0, lookVec.z).normalize();
        this.rightDirection = new Vec3d(-movementDirection.z, 0, movementDirection.x);

        Vec3d circleStart = startPosition.add(rightDirection.multiply(MIN_RADIUS));
        this.setPosition(circleStart.x, startPosition.y, circleStart.z);

        this.getDataTracker().set(START_X_TRACKER, (float) startPosition.x);
        this.getDataTracker().set(START_Y_TRACKER, (float) startPosition.y);
        this.getDataTracker().set(START_Z_TRACKER, (float) startPosition.z);
        float directionYaw = (float) Math.toDegrees(Math.atan2(-movementDirection.x, movementDirection.z));
        this.getDataTracker().set(DIRECTION_YAW_TRACKER, directionYaw);

        SpellRegistry.from(owner.getWorld()).getEntry(TWISTER_SPELL_ID).ifPresent(e -> this.spellEntry = e);
        if (this.spellEntry != null) {
            this.maxTravelDistance = this.spellEntry.value().range;
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(TIME_TO_LIVE_TRACKER, 0);
        builder.add(START_X_TRACKER, 0.0F);
        builder.add(START_Y_TRACKER, 0.0F);
        builder.add(START_Z_TRACKER, 0.0F);
        builder.add(DIRECTION_YAW_TRACKER, 0.0F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        var rawSpellId = this.getDataTracker().get(SPELL_ID_TRACKER);
        if (rawSpellId != null && !rawSpellId.isEmpty()) {
            this.spellId = Identifier.of(rawSpellId);
        }
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);

        if (this.movementDirection == null) {
            this.startPosition = new Vec3d(
                this.getDataTracker().get(START_X_TRACKER),
                this.getDataTracker().get(START_Y_TRACKER),
                this.getDataTracker().get(START_Z_TRACKER)
            );
            float directionYaw = this.getDataTracker().get(DIRECTION_YAW_TRACKER);
            float yawRadians = (float) Math.toRadians(directionYaw);
            this.movementDirection = new Vec3d(-Math.sin(yawRadians), 0, Math.cos(yawRadians));
            this.rightDirection = new Vec3d(-movementDirection.z, 0, movementDirection.x);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("SpellId")) {
            String spellIdStr = nbt.getString("SpellId");
            if (!spellIdStr.isEmpty()) {
                this.spellId = Identifier.of(spellIdStr);
            }
        }

        if (nbt.containsUuid("Owner")) this.ownerUuid = nbt.getUuid("Owner");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.currentTick = nbt.getInt("CurrentTick");

        if (nbt.contains("DirectionX")) {
            this.movementDirection = new Vec3d(
                nbt.getDouble("DirectionX"),
                nbt.getDouble("DirectionY"),
                nbt.getDouble("DirectionZ")
            );
            this.rightDirection = new Vec3d(-movementDirection.z, 0, movementDirection.x);
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
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);

        if (this.startPosition != null) {
            this.getDataTracker().set(START_X_TRACKER, (float) this.startPosition.x);
            this.getDataTracker().set(START_Y_TRACKER, (float) this.startPosition.y);
            this.getDataTracker().set(START_Z_TRACKER, (float) this.startPosition.z);
        }
        if (this.movementDirection != null) {
            float directionYaw = (float) Math.toDegrees(Math.atan2(-this.movementDirection.x, this.movementDirection.z));
            this.getDataTracker().set(DIRECTION_YAW_TRACKER, directionYaw);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }

        if (this.ownerUuid != null) nbt.putUuid("Owner", this.ownerUuid);
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

    private boolean idleSoundFired = false;
    @Override
    public void tick() {
        super.tick();

        var world = this.getWorld();
        if (world.isClient()) return;

        if (!idleSoundFired) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    ElementalSounds.WIND_ENTITY_LOOP.soundEvent(), SoundCategory.PLAYERS, 0.25F, 0.85F);
            idleSoundFired = true;
        }

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

        if (spellEntry == null) {
            SpellRegistry.from(world).getEntry(TWISTER_SPELL_ID).ifPresent(e -> this.spellEntry = e);
        }
        if (spellEntry != null) {
            maxTravelDistance = spellEntry.value().range;
        }

        if (movementDirection != null && startPosition != null) {
            float forwardDistance = FORWARD_SPEED * this.age;
            float circleAngle = ANGULAR_SPEED * this.age;

            if (forwardDistance > maxTravelDistance) {
                this.discard();
                return;
            }

            Vec3d center = startPosition.add(movementDirection.multiply(forwardDistance));

            BlockPos centerBlock = BlockPos.ofFloored(center);
            var blockState = world.getBlockState(centerBlock);
            if (!blockState.isAir() && blockState.isSolidBlock(world, centerBlock)) {
                this.discard();
                return;
            }

            float currentRadius = Math.min(MAX_RADIUS, MIN_RADIUS + forwardDistance * RADIUS_GROWTH);
            Vec3d offset = rightDirection.multiply(Math.cos(circleAngle) * currentRadius)
                    .add(movementDirection.multiply(Math.sin(circleAngle) * currentRadius));

            this.setPosition(center.x + offset.x, startPosition.y, center.z + offset.z);

            checkCollision();
        }
    }

    private void checkCollision() {
        var owner = this.getOwner();
        if (owner == null) return;

        Box searchBox = this.getBoundingBox().expand(1.0);
        PeriodicAreaImpact.apply(this.getWorld(), owner, this, searchBox,
                Identifier.of(MOD_ID, "helper/wind_twister_impact"),
                entity -> {
                    if (entity == owner) return false;
                    if (CustomMethods.isEntityProtectedCheck(entity, owner)) return false;
                    int entityId = entity.getId();
                    Integer lastTick = lastDamageTick.get(entityId);
                    if (lastTick != null && currentTick - lastTick < DAMAGE_INTERVAL) return false;
                    lastDamageTick.put(entityId, currentTick);
                    return true;
                },
                this.getPos(), true, null);
    }

    public LivingEntity getOwner() {
        if (cachedOwner != null && cachedOwner.isAlive()) {
            return cachedOwner;
        }
        if (ownerUuid != null && getWorld() instanceof ServerWorld sw) {
            Entity e = sw.getEntity(ownerUuid);
            if (e instanceof LivingEntity living) {
                cachedOwner = living;
                return living;
            }
        }
        return null;
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
