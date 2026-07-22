package net.elemental_wizards_rpg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.elemental_wizards_rpg.entity.util.PeriodicAreaImpact;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.more_rpg_classes.util.CustomMethods;

import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TidalWaveEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<TidalWaveEntity> ENTITY_TYPE;
    private static final Identifier WAVE_SPELL_ID = Identifier.of(MOD_ID, "aqua_tidal_wave");
    RegistryEntry<Spell> spellEntry = null;
    private static float MAX_DISTANCE = 16.0F;
    private static final float MOVEMENT_SPEED = 0.4F;
    private static final int DAMAGE_INTERVAL = 15;

    public static final int SUBMERGING_DURATION = 5;
    public static final int HIDDEN_DURATION = 10;
    public static final int EMERGING_DURATION = 5;

    private enum WaveState {
        MOVING_FORWARD,
        SUBMERGING,
        HIDDEN,
        EMERGING,
        MOVING_BACKWARD
    }

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(TidalWaveEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(TidalWaveEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> WAVE_STATE_TRACKER = DataTracker.registerData(TidalWaveEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DIRECTION_YAW_TRACKER = DataTracker.registerData(TidalWaveEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> STATE_TICK_TRACKER = DataTracker.registerData(TidalWaveEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private Vec3d movementDirection;
    private Vec3d startPosition;
    private WaveState waveState = WaveState.MOVING_FORWARD;
    private java.util.Map<Integer, Integer> lastDamageTick = new java.util.HashMap<>();
    private int currentTick = 0;
    private int stateTick = 0;

    public TidalWaveEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.calculateDimensions();
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

        float ownerYaw = owner.getYaw();
        float yawRadians = (float) Math.toRadians(ownerYaw);

        double dirX = -Math.sin(yawRadians);
        double dirZ = Math.cos(yawRadians);

        this.movementDirection = new Vec3d(dirX, 0, dirZ).normalize().multiply(MOVEMENT_SPEED);

        this.setYaw(ownerYaw);
        this.prevYaw = ownerYaw;
        this.getDataTracker().set(DIRECTION_YAW_TRACKER, ownerYaw);

        SpellRegistry.from(owner.getWorld()).getEntry(WAVE_SPELL_ID).ifPresent(e -> this.spellEntry = e);

        this.waveState = WaveState.MOVING_FORWARD;
        this.stateTick = 0;
        this.getDataTracker().set(WAVE_STATE_TRACKER, 0);
        this.getDataTracker().set(STATE_TICK_TRACKER, 0);

        this.calculateDimensions();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(TIME_TO_LIVE_TRACKER, 0);
        builder.add(WAVE_STATE_TRACKER, 0);
        builder.add(DIRECTION_YAW_TRACKER, 0.0F);
        builder.add(STATE_TICK_TRACKER, 0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        var rawSpellId = this.getDataTracker().get(SPELL_ID_TRACKER);
        if (rawSpellId != null && !rawSpellId.isEmpty()) {
            this.spellId = Identifier.of(rawSpellId);
        }
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);
        int stateOrdinal = this.getDataTracker().get(WAVE_STATE_TRACKER);
        this.waveState = WaveState.values()[stateOrdinal];
        this.stateTick = this.getDataTracker().get(STATE_TICK_TRACKER);

        float dirYaw = this.getDataTracker().get(DIRECTION_YAW_TRACKER);
        float yawRadians = (float) Math.toRadians(dirYaw);
        double dirX = -Math.sin(yawRadians);
        double dirZ = Math.cos(yawRadians);
        if (this.movementDirection == null) {
            this.movementDirection = new Vec3d(dirX, 0, dirZ).normalize().multiply(MOVEMENT_SPEED);
            if (this.waveState == WaveState.EMERGING || this.waveState == WaveState.MOVING_BACKWARD) {
                this.movementDirection = this.movementDirection.negate();
            }
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
        this.stateTick = nbt.getInt("StateTick");
        this.waveState = WaveState.values()[nbt.getInt("WaveState")];

        if (nbt.contains("DirectionYaw")) {
            float dirYaw = nbt.getFloat("DirectionYaw");
            this.setYaw(dirYaw);
            this.prevYaw = dirYaw;
        }

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
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
        this.getDataTracker().set(WAVE_STATE_TRACKER, this.waveState.ordinal());
        this.getDataTracker().set(STATE_TICK_TRACKER, this.stateTick);

        if (this.movementDirection != null) {
            float yaw = (float) Math.toDegrees(Math.atan2(-this.movementDirection.x, this.movementDirection.z));
            this.getDataTracker().set(DIRECTION_YAW_TRACKER, yaw);
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
        nbt.putInt("StateTick", this.stateTick);
        nbt.putInt("WaveState", this.waveState.ordinal());
        nbt.putFloat("DirectionYaw", this.getDataTracker().get(DIRECTION_YAW_TRACKER));

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
    private int idleSoundTick = 0;
    @Override
    public void tick() {
        super.tick();

        var world = this.getWorld();
        if (world.isClient) return;
        if (idleSoundTick-- <= 0) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundCategory.PLAYERS, 1.5F, 1F);
            idleSoundTick = 60;
        }


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
        }

        if (!this.getWorld().isClient() && movementDirection != null && startPosition != null) {
            if (spellEntry == null) {
                SpellRegistry.from(this.getWorld()).getEntry(WAVE_SPELL_ID).ifPresent(e -> this.spellEntry = e);
            }
            if (spellEntry != null) {
                MAX_DISTANCE = spellEntry.value().range;
            }

            double distanceFromStart = this.getPos().distanceTo(startPosition);

            switch (waveState) {
                case MOVING_FORWARD -> {
                    if (distanceFromStart >= MAX_DISTANCE) {
                        waveState = WaveState.SUBMERGING;
                        stateTick = 0;
                    } else {
                        this.setPosition(this.getPos().add(movementDirection));
                        if (currentTick % DAMAGE_INTERVAL == 0) damageEntities();
                    }
                }
                case SUBMERGING -> {
                    stateTick++;
                    if (stateTick >= SUBMERGING_DURATION) {
                        waveState = WaveState.HIDDEN;
                        stateTick = 0;
                    }
                }
                case HIDDEN -> {
                    stateTick++;
                    if (stateTick >= HIDDEN_DURATION) {
                        waveState = WaveState.EMERGING;
                        stateTick = 0;
                        movementDirection = movementDirection.negate();
                    }
                }
                case EMERGING -> {
                    stateTick++;
                    if (stateTick >= EMERGING_DURATION) {
                        waveState = WaveState.MOVING_BACKWARD;
                        stateTick = 0;
                    }
                }
                case MOVING_BACKWARD -> {
                    if (distanceFromStart <= 1.0) {
                        this.discard();
                        return;
                    }
                    this.setPosition(this.getPos().add(movementDirection));
                    if (currentTick % DAMAGE_INTERVAL == 0) damageEntities();
                }
            }

            this.getDataTracker().set(WAVE_STATE_TRACKER, waveState.ordinal());
            this.getDataTracker().set(STATE_TICK_TRACKER, stateTick);
        }
    }

    private void damageEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        Box damageBox = this.getBoundingBox().expand(3.0, 0, 3.0);
        PeriodicAreaImpact.apply(this.getWorld(), owner, this, damageBox,
                Identifier.of(MOD_ID, "helper/aqua_tidal_wave_impact"),
                entity -> {
                    if (entity == owner) return false;
                    if (CustomMethods.isEntityProtectedCheck(entity, owner)) return false;
                    int entityId = entity.getId();
                    Integer lastTick = lastDamageTick.get(entityId);
                    if (lastTick != null && currentTick - lastTick < 10) return false;
                    lastDamageTick.put(entityId, currentTick);
                    return true;
                },
                this.getPos(), true, null);
    }

    public int getWaveStateOrdinal() {
        return this.getDataTracker().get(WAVE_STATE_TRACKER);
    }

    public int getStateTick() {
        return this.getDataTracker().get(STATE_TICK_TRACKER);
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
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(5.5F, 1.5F);
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
