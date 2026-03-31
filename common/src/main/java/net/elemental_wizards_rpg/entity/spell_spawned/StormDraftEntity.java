package net.elemental_wizards_rpg.entity.spell_spawned;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.more_rpg_classes.util.CustomMethods;
import net.spell_power.api.SpellPower;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class StormDraftEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<StormDraftEntity> ENTITY_TYPE;

    private static final float MOVEMENT_SPEED = 1.5F;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(StormDraftEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(StormDraftEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private boolean hasHit = false;
    private final Set<UUID> hitEntities = new HashSet<>();

    private Vec3d movementDirection;
    private Vec3d startPosition;

    public StormDraftEntity(EntityType<?> type, World world) {
        super(type, world);
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

        this.setPosition(this.getX(), owner.getEyeY(), this.getZ());

        double yComponent = lookVec.y;
        if (Math.abs(yComponent) < 0.3) {
            yComponent = 0.02;
        }

        this.movementDirection = new Vec3d(lookVec.x, yComponent, lookVec.z).normalize().multiply(MOVEMENT_SPEED);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
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

        if (nbt.containsUuid("Owner")) this.ownerUuid = nbt.getUuid("Owner");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.hasHit = nbt.getBoolean("HasHit");

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
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }

        if (this.ownerUuid != null) nbt.putUuid("Owner", this.ownerUuid);
        nbt.putInt("TimeToLive", this.timeToLive);
        nbt.putBoolean("HasHit", this.hasHit);

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

        var world = this.getWorld();

        if (world.isClient()) {
            spawnParticles();
        } else {
            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.discard();
                return;
            }

            if (this.age > this.timeToLive || this.hasHit) {
                this.discard();
                return;
            }

            if (this.startPosition != null && this.getPos().distanceTo(this.startPosition) > 32.0) {
                this.discard();
                return;
            }
            if (movementDirection != null) {
                Vec3d nextPos = this.getPos().add(movementDirection);
                if (!world.getBlockState(this.getBlockPos().add(
                    (int) Math.signum(movementDirection.x),
                    (int) Math.signum(movementDirection.y),
                    (int) Math.signum(movementDirection.z)
                )).isAir()) {
                    this.discard();
                    return;
                }

                this.setPosition(nextPos);
                checkCollision();
            }
        }
    }

    private void spawnParticles() {
        World world = this.getWorld();
        Vec3d pos = this.getPos();

        for (int i = 0; i < 3; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
            double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

            world.addParticle(
                    MoreParticles.WIND_VACUUM,
                pos.x + offsetX,
                pos.y + offsetY,
                pos.z + offsetZ,
                0, 0, 0
            );
        }

        for (int i = 0; i < 2; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.3;
            double offsetY = (this.random.nextDouble() - 0.5) * 0.3;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.3;

            world.addParticle(
                ParticleTypes.SMALL_GUST,
                pos.x + offsetX,
                  pos.y + offsetY,
                pos.z + offsetZ,
                0, 0, 0
            );
        }
    }

    private void checkCollision() {
        var owner = this.getOwner();
        if (owner == null) return;

        Box searchBox = this.getBoundingBox().expand(1.0);
        var entities = this.getWorld().getOtherEntities(this, searchBox);

        RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "helper/wind_stormdraft_impact")).orElse(null);
        if (spellImpact == null) return;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == owner) continue;
            if (hitEntities.contains(entity.getUuid())) continue;
            if (CustomMethods.isEntityProtectedCheck(entity, owner)) continue;
            boolean damaged = SpellHelper.performImpacts(owner.getWorld(), owner, entity, this, spellImpact,
                    spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()),
                    false, null);
            if (damaged) hitEntities.add(entity.getUuid());
        }
    }

    private LivingEntity getOwner() {
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
