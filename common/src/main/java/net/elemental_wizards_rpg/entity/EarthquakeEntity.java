package net.elemental_wizards_rpg.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.elemental_wizards_rpg.entity.util.PeriodicAreaImpact;
import net.more_rpg_classes.sounds.MRPGLibSounds;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellParameters;
import net.more_rpg_classes.util.CustomMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class EarthquakeEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<EarthquakeEntity> ENTITY_TYPE;
    private static final Identifier EARTHQUAKE_SPELL_ID = Identifier.of(MOD_ID, "terra_earthquake");

    // Ratio (5/16) must stay in sync with onSpawnedBySpell, which scales radius via SpellParameters.getRangeCurved
    private static final float BASE_EARTHQUAKE_RADIUS = 16.0F;
    private static final float BASE_VERTICAL_RANGE = 5.0F;
    private static final int DAMAGE_INTERVAL = 10;
    private static final int MAX_SHAKING_BLOCKS = 300;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(EarthquakeEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(EarthquakeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> RADIUS_TRACKER = DataTracker.registerData(EarthquakeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> VERTICAL_RANGE_TRACKER = DataTracker.registerData(EarthquakeEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private int currentTick = 0;
    private float earthquakeRadius = BASE_EARTHQUAKE_RADIUS;
    private float verticalRange = BASE_VERTICAL_RANGE;
    private RegistryEntry<Spell> spellEntry = null;

    private List<ShakingBlockData> shakingBlocks = new ArrayList<>();
    private boolean blocksInitialized = false;
    private Random random = new Random();

    public EarthquakeEntity(EntityType<? extends Entity> entityType, World world) {
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

        SpellRegistry.from(owner.getWorld()).getEntry(EARTHQUAKE_SPELL_ID).ifPresent(e -> this.spellEntry = e);
        float effectiveRange = this.spellEntry != null
                ? SpellParameters.getRangeCurved(owner, this.spellEntry, args.context().charge())
                : BASE_EARTHQUAKE_RADIUS;
        this.earthquakeRadius = effectiveRange;
        this.verticalRange = effectiveRange * (BASE_VERTICAL_RANGE / BASE_EARTHQUAKE_RADIUS);
        this.getDataTracker().set(RADIUS_TRACKER, this.earthquakeRadius);
        this.getDataTracker().set(VERTICAL_RANGE_TRACKER, this.verticalRange);

        this.setNoGravity(true);

        this.calculateDimensions();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(TIME_TO_LIVE_TRACKER, 0);
        builder.add(RADIUS_TRACKER, BASE_EARTHQUAKE_RADIUS);
        builder.add(VERTICAL_RANGE_TRACKER, BASE_VERTICAL_RANGE);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        var rawSpellId = this.getDataTracker().get(SPELL_ID_TRACKER);
        if (rawSpellId != null && !rawSpellId.isEmpty()) {
            this.spellId = Identifier.of(rawSpellId);
        }
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);
        this.earthquakeRadius = this.getDataTracker().get(RADIUS_TRACKER);
        this.verticalRange = this.getDataTracker().get(VERTICAL_RANGE_TRACKER);
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
        this.earthquakeRadius = nbt.contains("Radius") ? nbt.getFloat("Radius") : BASE_EARTHQUAKE_RADIUS;
        this.verticalRange = nbt.contains("VerticalRange") ? nbt.getFloat("VerticalRange") : BASE_VERTICAL_RANGE;

        if (this.spellId != null) {
            this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        }
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
        this.getDataTracker().set(RADIUS_TRACKER, this.earthquakeRadius);
        this.getDataTracker().set(VERTICAL_RANGE_TRACKER, this.verticalRange);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }

        if (this.ownerUuid != null) nbt.putUuid("Owner", this.ownerUuid);
        nbt.putInt("TimeToLive", this.timeToLive);
        nbt.putInt("CurrentTick", this.currentTick);
        nbt.putFloat("Radius", this.earthquakeRadius);
        nbt.putFloat("VerticalRange", this.verticalRange);
    }
    private int idleSoundTick = 0;
    @Override
    public void tick() {
        super.tick();

        var world = this.getWorld();

        if (!world.isClient()) {
            if (idleSoundTick-- <= 0) {
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        MRPGLibSounds.EARTH_MAGIC_CAST_1.soundEvent(), SoundCategory.PLAYERS, 0.5F, 0.85F);
                idleSoundTick = 60;
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
        }

        if (world.isClient()) {
            if (!blocksInitialized && this.age >= 1) {
                initializeShakingBlocks();
                blocksInitialized = true;
            }
        } else {
            if (this.age % 5 == 0 && world instanceof ServerWorld serverWorld) {
                Vec3d pos = this.getPos();
                for (int i = 0; i < 8; i++) {
                    double offsetX = (random.nextDouble() - 0.5) * earthquakeRadius * 2;
                    double offsetZ = (random.nextDouble() - 0.5) * earthquakeRadius * 2;
                    serverWorld.spawnParticles(ParticleTypes.ASH, pos.x + offsetX, pos.y, pos.z + offsetZ, 3, 0.2, 0.2, 0.2, 0.01);
                }
            }

            if (currentTick % DAMAGE_INTERVAL == 0) {
                damageEntities();
            }
        }
    }

    private void initializeShakingBlocks() {
        shakingBlocks.clear();

        BlockPos centerPos = this.getBlockPos();

        BlockPos groundPos = centerPos;
        for (int dy = 0; dy < 10; dy++) {
            BlockPos checkDown = centerPos.down(dy);
            BlockState state = this.getWorld().getBlockState(checkDown);
            if (!state.isAir() && state.isSolidBlock(this.getWorld(), checkDown)) {
                groundPos = checkDown;
                break;
            }
        }

        List<BlockPos> candidateBlocks = new ArrayList<>();

        int radiusInt = (int) Math.ceil(earthquakeRadius);
        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int z = -radiusInt; z <= radiusInt; z++) {
                for (int y = -5; y <= 5; y++) {
                    BlockPos checkPos = groundPos.add(x, y, z);

                    double dx = checkPos.getX() - centerPos.getX();
                    double dz = checkPos.getZ() - centerPos.getZ();
                    double distSq = dx * dx + dz * dz;

                    if (distSq > earthquakeRadius * earthquakeRadius) {
                        continue;
                    }

                    BlockState blockState = this.getWorld().getBlockState(checkPos);
                    BlockState aboveState = this.getWorld().getBlockState(checkPos.up());

                    if (!blockState.isAir() && blockState.isSolidBlock(this.getWorld(), checkPos) && aboveState.isAir()) {
                        candidateBlocks.add(checkPos);
                    }
                }
            }
        }

        int maxBlocks = Math.min(MAX_SHAKING_BLOCKS, candidateBlocks.size());

        if (candidateBlocks.size() > MAX_SHAKING_BLOCKS) {
            Collections.shuffle(candidateBlocks, random);
        }

        for (int i = 0; i < maxBlocks; i++) {
            BlockPos blockPos = candidateBlocks.get(i);
            BlockState blockState = this.getWorld().getBlockState(blockPos);

            double dx = blockPos.getX() - centerPos.getX();
            double dz = blockPos.getZ() - centerPos.getZ();
            double distFromCenter = Math.sqrt(dx * dx + dz * dz);
            float distanceFactor = (float) (1.0 - (distFromCenter / earthquakeRadius) * 0.3);

            float baseHeight = random.nextFloat();
            float shakeHeight;
            if (baseHeight < 0.3f) {
                shakeHeight = 0.05F + random.nextFloat() * 0.10F;
            } else if (baseHeight < 0.7f) {
                shakeHeight = 0.15F + random.nextFloat() * 0.20F;
            } else {
                shakeHeight = 0.35F + random.nextFloat() * 0.25F;
            }
            shakeHeight *= distanceFactor;

            float shakePhase = random.nextFloat() * (float) Math.PI * 2;

            float speedRoll = random.nextFloat();
            float shakeSpeed;
            if (speedRoll < 0.25f) {
                shakeSpeed = 0.05F + random.nextFloat() * 0.05F;
            } else if (speedRoll < 0.5f) {
                shakeSpeed = 0.10F + random.nextFloat() * 0.05F;
            } else if (speedRoll < 0.75f) {
                shakeSpeed = 0.15F + random.nextFloat() * 0.10F;
            } else {
                shakeSpeed = 0.25F + random.nextFloat() * 0.15F;
            }

            shakingBlocks.add(new ShakingBlockData(blockPos, blockState, shakeHeight, shakePhase, shakeSpeed));
        }
    }

    private void damageEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        Vec3d earthquakeCenter = this.getPos();
        Box damageBox = Box.of(earthquakeCenter, earthquakeRadius * 2, verticalRange * 2, earthquakeRadius * 2);

        PeriodicAreaImpact.apply(this.getWorld(), owner, this, damageBox,
                Identifier.of(MOD_ID, "helper/terra_earthquake_impact"),
                entity -> entity != owner && !CustomMethods.isEntityProtectedCheck(entity, owner),
                null, false, null);
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

    @Override
    public boolean shouldRender(double distance) {
        return distance < 128.0 * 128.0;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        return net.minecraft.entity.EntityDimensions.changing(earthquakeRadius * 2, verticalRange * 2);
    }

    public List<ShakingBlockData> getShakingBlocks() {
        return shakingBlocks;
    }

    public static class ShakingBlockData {
        public final BlockPos pos;
        public final BlockState state;
        public final float shakeHeight;
        public final float shakePhase;
        public final float shakeSpeed;

        public ShakingBlockData(BlockPos pos, BlockState state, float shakeHeight, float shakePhase, float shakeSpeed) {
            this.pos = pos;
            this.state = state;
            this.shakeHeight = shakeHeight;
            this.shakePhase = shakePhase;
            this.shakeSpeed = shakeSpeed;
        }
    }
}
