package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.entity.goals.SpikeAttackGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_power.api.SpellPower;

import java.util.EnumSet;

public class EarthGolemEntity extends PathAwareEntity implements SpellEntity.Spawned {
    public static EntityType<EarthGolemEntity> ENTITY_TYPE;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> OWNER_ID_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> ATTACKING_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private Identifier spellId;
    private int ownerId;
    private int timeToLive;
    private LivingEntity cachedOwner = null;

    public EarthGolemEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createEarthGolemAttributes() {
        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SpikeAttackGoal(this));
        this.goalSelector.add(2, new MoveTowardsTargetGoal(this));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(0, new DefendOwnerGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new AttackHostilesGoal(this));
    }

    @Override
    public void onSpawnedBySpell(Args args) {
        var owner = args.owner();
        var spellId = args.spell().getKey().get().getValue();
        var spawn = args.spawnData();

        this.spellId = spellId;
        this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        this.ownerId = owner.getId();
        this.cachedOwner = owner;
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.timeToLive = spawn.time_to_live_seconds * 20;
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);

        var earthPower = SpellPower.getSpellPower(MoreSpellSchools.EARTH, owner);
        double spellPowerValue = earthPower.randomValue();

        double scaledHealth = 40.0 + (spellPowerValue * 2.0);

        scaledHealth = Math.max(20.0, Math.min(200.0, scaledHealth));

        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(scaledHealth);
        this.setHealth((float) scaledHealth);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(OWNER_ID_TRACKER, 0);
        builder.add(TIME_TO_LIVE_TRACKER, 0);
        builder.add(ATTACKING_TRACKER, false);
    }

    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING_TRACKER);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING_TRACKER, attacking);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        var rawSpellId = this.getDataTracker().get(SPELL_ID_TRACKER);
        if (rawSpellId != null && !rawSpellId.isEmpty()) {
            this.spellId = Identifier.of(rawSpellId);
        }
        this.ownerId = this.getDataTracker().get(OWNER_ID_TRACKER);
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.spellId = Identifier.of(nbt.getString("SpellId"));
        this.ownerId = nbt.getInt("OwnerId");
        this.timeToLive = nbt.getInt("TimeToLive");

        this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("SpellId", this.spellId.toString());
        nbt.putInt("OwnerId", this.ownerId);
        nbt.putInt("TimeToLive", this.timeToLive);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.getLookControl().lookAt(target, 30.0F, 30.0F);
            }
        }

        var owner = this.getOwner();
        if (owner == null || owner.isRemoved() || !owner.isAlive()) {
            this.discard();
            return;
        }

        if (this.age > this.timeToLive) {
            this.discard();
        }

        if (this.getWorld().isClient) {
            setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.isAttacking()) {
            this.attackAnimationState.startIfNotRunning(this.age);
            this.idleAnimationState.stop();
        } else {
            this.attackAnimationState.stop();
            this.idleAnimationState.startIfNotRunning(this.age);
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

    private class MoveTowardsTargetGoal extends Goal {
        private final EarthGolemEntity golem;
        private LivingEntity target;

        public MoveTowardsTargetGoal(EarthGolemEntity golem) {
            this.golem = golem;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.target = golem.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            return true;
        }

        @Override
        public boolean shouldContinue() {
            this.target = golem.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            return !golem.getNavigation().isIdle();
        }

        @Override
        public void start() {
            golem.getNavigation().startMovingTo(target, 1.0);
        }

        @Override
        public void tick() {
            if (target != null && target.isAlive()) {
                golem.getLookControl().lookAt(target, 30.0F, 30.0F);

                double distance = golem.squaredDistanceTo(target);
                if (distance > 3.0 * 3.0) {
                    golem.getNavigation().startMovingTo(target, 1.0);
                }
            }
        }

        @Override
        public void stop() {
            this.target = null;
            golem.getNavigation().stop();
        }
    }

    private class FollowOwnerGoal extends Goal {
        private final EarthGolemEntity golem;
        private final double speed;
        private final float maxDistance;
        private final float minDistance;
        private LivingEntity owner;
        private int updateCountdown;

        public FollowOwnerGoal(EarthGolemEntity golem, double speed, float maxDistance, float minDistance) {
            this.golem = golem;
            this.speed = speed;
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (golem.getTarget() != null && golem.getTarget().isAlive()) {
                return false;
            }

            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;
            if (owner.isSpectator()) return false;

            double distance = golem.squaredDistanceTo(owner);
            if (distance < (minDistance * minDistance)) return false;

            this.owner = owner;
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (golem.getTarget() != null && golem.getTarget().isAlive()) {
                return false;
            }

            if (owner == null || !owner.isAlive()) return false;
            if (owner.isSpectator()) return false;
            if (golem.getNavigation().isIdle()) return false;

            return golem.squaredDistanceTo(owner) > (minDistance * minDistance);
        }

        @Override
        public void start() {
            this.updateCountdown = 0;
            golem.getNavigation().startMovingTo(owner, speed);
        }

        @Override
        public void stop() {
            this.owner = null;
            golem.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (owner == null) return;

            golem.getLookControl().lookAt(owner, 10.0F, (float) golem.getMaxLookPitchChange());

            if (--this.updateCountdown <= 0) {
                this.updateCountdown = 10;

                double distance = golem.squaredDistanceTo(owner);

                if (distance > (maxDistance * maxDistance)) {
                    golem.getNavigation().startMovingTo(owner, speed * 1.5);
                } else {
                    golem.getNavigation().startMovingTo(owner, speed);
                }
            }
        }
    }

    private class DefendOwnerGoal extends Goal {
        private final EarthGolemEntity golem;
        private LivingEntity attacker;
        private int checkTimer;

        public DefendOwnerGoal(EarthGolemEntity golem) {
            this.golem = golem;
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;

            LivingEntity ownerAttacker = owner.getAttacker();
            if (ownerAttacker == null || !ownerAttacker.isAlive()) {
                return false;
            }
            if (ownerAttacker == golem || ownerAttacker == owner) {
                return false;
            }
            if (isProtected(owner, ownerAttacker)) {
                return false;
            }

            this.attacker = ownerAttacker;
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (attacker == null || !attacker.isAlive()) {
                return false;
            }
            LivingEntity owner = golem.getOwner();
            if (owner == null) {
                return false;
            }
            if (golem.squaredDistanceTo(owner) > 1024.0) {
                return false;
            }
            if (golem.squaredDistanceTo(attacker) > 400.0) {
                return false;
            }
            return true;
        }

        @Override
        public void start() {
            golem.setTarget(attacker);
            this.checkTimer = 0;
        }

        @Override
        public void tick() {
            if (--this.checkTimer <= 0) {
                this.checkTimer = 10;
                LivingEntity owner = golem.getOwner();
                if (owner != null) {
                    LivingEntity newAttacker = owner.getAttacker();
                    if (newAttacker != null && newAttacker.isAlive()
                            && newAttacker != golem && newAttacker != owner
                            && !isProtected(owner, newAttacker)) {
                        this.attacker = newAttacker;
                        golem.setTarget(attacker);
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.attacker = null;
        }
    }

    private class AttackHostilesGoal extends Goal {
        private final EarthGolemEntity golem;
        private LivingEntity targetEntity;
        private int scanCountdown;

        public AttackHostilesGoal(EarthGolemEntity golem) {
            this.golem = golem;
            this.scanCountdown = 0;
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;

            if (--this.scanCountdown > 0) {
                return false;
            }
            this.scanCountdown = 10;

            targetEntity = findNearestHostile();
            return targetEntity != null;
        }

        @Override
        public boolean shouldContinue() {
            if (targetEntity == null || !targetEntity.isAlive()) {
                return false;
            }

            LivingEntity owner = golem.getOwner();
            if (owner == null) {
                return false;
            }

            if (golem.squaredDistanceTo(owner) > 1024.0) {
                return false;
            }

            if (isProtected(owner, targetEntity)) {
                return false;
            }

            if (golem.squaredDistanceTo(targetEntity) > 400.0) {
                return false;
            }

            return true;
        }

        @Override
        public void start() {
            golem.setTarget(targetEntity);
        }

        @Override
        public void stop() {
            targetEntity = null;
        }

        @Override
        public void tick() {
            if (targetEntity != null && targetEntity.isAlive()) {
                golem.setTarget(targetEntity);
            }
        }

        private LivingEntity findNearestHostile() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return null;

            var nearbyEntities = golem.getWorld().getOtherEntities(
                golem,
                golem.getBoundingBox().expand(16.0),
                entity -> entity instanceof LivingEntity
            );

            LivingEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof LivingEntity livingEntity)) continue;
                if (entity == owner || entity == golem) continue;

                boolean isValidTarget = false;
                if (entity instanceof HostileEntity) {
                    isValidTarget = true;
                } else if (entity instanceof PlayerEntity otherPlayer) {
                    if(isProtected(golem.getOwner(),otherPlayer)){
                        isValidTarget = false;
                    } else{
                        isValidTarget = true;
                    }
                }

                if (isValidTarget) {
                    double distance = golem.squaredDistanceTo(entity);
                    if (distance < nearestDistance) {
                        nearest = livingEntity;
                        nearestDistance = distance;
                    }
                }
            }

            return nearest;
        }
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
