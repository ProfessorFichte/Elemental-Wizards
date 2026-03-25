package net.elemental_wizards_rpg.entity.spell_spawned;

import net.elemental_wizards_rpg.particle.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.more_rpg_classes.util.CustomMethods;
import net.spell_power.api.SpellPower;

import java.util.List;
import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class HealingRainCloudEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<HealingRainCloudEntity> ENTITY_TYPE;

    private static final float FOLLOW_SPEED = 0.13F;
    private static final float BOB_AMPLITUDE = 0.3F;
    private static final float BOB_SPEED = 0.05F;
    private static final float FLOAT_HEIGHT = 7.5F;
    private static final int HEAL_INTERVAL = 20;
    private static final float SEARCH_RADIUS = 32.0F;
    private static final float RAIN_RADIUS = 4.0F;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(HealingRainCloudEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(HealingRainCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private LivingEntity targetPlayer = null;
    private int targetSearchCooldown = 0;
    private int currentTick = 0;

    public HealingRainCloudEntity(EntityType<? extends Entity> entityType, World world) {
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

        this.targetPlayer = owner;

        adjustHeightToGround();

        this.calculateDimensions();
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
        this.currentTick = nbt.getInt("CurrentTick");
        this.targetSearchCooldown = nbt.getInt("TargetSearchCooldown");

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
        nbt.putInt("CurrentTick", this.currentTick);
        nbt.putInt("TargetSearchCooldown", this.targetSearchCooldown);
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

            spawnRainParticles();

            if (targetSearchCooldown <= 0) {
                findLowestHealthTarget();
                targetSearchCooldown = 40;
            } else {
                targetSearchCooldown--;
            }

            if (targetPlayer != null && !targetPlayer.isRemoved()) {
                followTarget();
            } else {
                adjustHeightToGround();
            }

            if (currentTick % HEAL_INTERVAL == 0) {
                healEntities();
            }
        }
    }

    private void findLowestHealthTarget() {
        var owner = this.getOwner();
        if (owner == null) return;

        Vec3d cloudPos = this.getPos();
        Box searchBox = Box.of(cloudPos, SEARCH_RADIUS * 2, SEARCH_RADIUS * 2, SEARCH_RADIUS * 2);
        List<PlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
            PlayerEntity.class,
            searchBox,
            player -> player != null && !player.isRemoved()
        );

        PlayerEntity lowestHealthPlayer = null;
        float lowestHealthPercent = 1.0F;

        for (PlayerEntity player : nearbyPlayers) {
            if (player == owner || !CustomMethods.isEntityProtectedCheck(player, owner)) {
                float healthPercent = player.getHealth() / player.getMaxHealth();
                if (healthPercent < lowestHealthPercent) {
                    lowestHealthPercent = healthPercent;
                    lowestHealthPlayer = player;
                }
            }
        }

        if (lowestHealthPlayer == null && owner instanceof PlayerEntity) {
            lowestHealthPlayer = (PlayerEntity) owner;
        }

        this.targetPlayer = lowestHealthPlayer;
    }

    private void followTarget() {
        if (targetPlayer == null) return;

        double bobOffset = Math.sin(this.age * BOB_SPEED) * BOB_AMPLITUDE;
        Vec3d targetPos = targetPlayer.getPos().add(0, FLOAT_HEIGHT + bobOffset, 0);
        Vec3d currentPos = this.getPos();

        Vec3d direction = targetPos.subtract(currentPos);
        double distance = direction.length();

        if (distance > 0.05) {
            double speed = Math.min(FOLLOW_SPEED, distance);
            this.setPosition(currentPos.add(direction.normalize().multiply(speed)));
        }
    }

    private void adjustHeightToGround() {
        net.minecraft.util.math.BlockPos currentBlockPos = this.getBlockPos();
        net.minecraft.util.math.BlockPos groundPos = currentBlockPos;

        for (int i = 0; i < 20; i++) {
            net.minecraft.util.math.BlockPos checkPos = currentBlockPos.down(i);
            if (!this.getWorld().getBlockState(checkPos).isAir()) {
                groundPos = checkPos;
                break;
            }
        }

        double targetY = groundPos.getY() + FLOAT_HEIGHT;
        if (Math.abs(this.getY() - targetY) > 0.1) {
            this.setPosition(this.getX(), targetY, this.getZ());
        }
    }

    private void spawnRainParticles() {
        World world = this.getWorld();
        Vec3d pos = this.getPos();

        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 20; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * RAIN_RADIUS * 2;
                double offsetZ = (this.random.nextDouble() - 0.5) * RAIN_RADIUS * 2;

                serverWorld.spawnParticles(
                    ModParticles.HEALING_RAIN,
                    pos.x + offsetX,
                    pos.y - 0.3,
                    pos.z + offsetZ,
                    1,
                    0.05, -0.1, 0.05,
                    0.02
                );

                if (i % 2 == 0) {
                    serverWorld.spawnParticles(
                        ModParticles.HEALING_RAIN,
                        pos.x + offsetX,
                        pos.y - 1.0,
                        pos.z + offsetZ,
                        1,
                        0.03, -0.15, 0.03,
                        0.01
                    );
                }

                if (i % 4 == 0) {
                    serverWorld.spawnParticles(
                        ParticleTypes.SPLASH,
                        pos.x + offsetX,
                        pos.y - 5.5,
                        pos.z + offsetZ,
                        2,
                        0.2, 0, 0.2,
                        0.0
                    );
                }
            }
        }
    }

    private void healEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        Vec3d cloudPos = this.getPos();
        Box effectBox = Box.of(cloudPos.add(0, -3, 0), RAIN_RADIUS * 2, 6, RAIN_RADIUS * 2);
        var entities = this.getWorld().getOtherEntities(this, effectBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "helper/aqua_healing_rain_impact")).get();
            SpellHelper.performImpacts(owner.getWorld(), owner, entity, owner, spellImpact,
                    spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(entity.getPos()));
        }
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
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        return net.minecraft.entity.EntityDimensions.changing(RAIN_RADIUS * 2, 2.0F);
    }
}
