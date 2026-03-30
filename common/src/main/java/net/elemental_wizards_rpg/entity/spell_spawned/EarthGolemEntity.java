package net.elemental_wizards_rpg.entity.spell_spawned;

import net.elemental_wizards_rpg.entity.goals.SpikeAttackGoal;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.entity.SpellEntity;
import net.more_rpg_classes.util.CustomMethods;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_power.api.SpellPower;

import java.util.EnumSet;

public class EarthGolemEntity extends PathAwareEntity implements SpellEntity.Spawned {
    public static EntityType<EarthGolemEntity> ENTITY_TYPE;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> ATTACKING_TRACKER = DataTracker.registerData(EarthGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private int idleGrowlTimer = -1;

    public EarthGolemEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public float getStepHeight() {
        return 1.1f;
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
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() == getOwner()) return false;
        return super.damage(source, amount);
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
        this.targetSelector.add(1, new AttackOwnerTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new AttackHostilesGoal(this));
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
        this.timeToLive = this.getDataTracker().get(TIME_TO_LIVE_TRACKER);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SpellId")) {
            this.spellId = Identifier.of(nbt.getString("SpellId"));
            this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        }
        if (nbt.containsUuid("Owner")) this.ownerUuid = nbt.getUuid("Owner");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.spellId != null) nbt.putString("SpellId", this.spellId.toString());
        if (this.ownerUuid != null) nbt.putUuid("Owner", this.ownerUuid);
        nbt.putInt("TimeToLive", this.timeToLive);
    }


    private static ParticleBatch[] despawnParticle() {
        return new ParticleBatch[] {
                new ParticleBatch("more_rpg_classes:stone_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        50, 0.2F, 0.5F).extent(0.2F),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10F, 0.2F, 0.3F)
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ElementalSounds.STONE_FLESH.soundEvent(), SoundCategory.NEUTRAL,
                        0.5F, 0.9F + this.getRandom().nextFloat() * 0.2F);
                this.discard();
                return;
            }

            if (this.age > this.timeToLive) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ElementalSounds.STONE_FLESH.soundEvent(), SoundCategory.NEUTRAL,
                        0.5F, 0.9F + this.getRandom().nextFloat() * 0.2F);
                this.discard();
                return;
            }

            if (idleGrowlTimer < 0) {
                idleGrowlTimer = 50 + this.getRandom().nextInt(51);
            } else if (--idleGrowlTimer <= 0) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ElementalSounds.GOLEM_GROWL.soundEvent(), SoundCategory.NEUTRAL,
                        1.0F, 0.9F + this.getRandom().nextFloat() * 0.2F);
                idleGrowlTimer = 50 + this.getRandom().nextInt(51);
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.getLookControl().lookAt(target, 30.0F, 30.0F);
            }
        }

        if (this.getWorld().isClient) {
            setupAnimationStates();
        }

        if(this.getWorld().isClient && this.age > this.timeToLive){
            ParticleHelper.play(this.getWorld(), this, despawnParticle());
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
            if (golem.getTarget() != null && golem.getTarget().isAlive()) return false;
            if (owner == null || !owner.isAlive() || owner.isSpectator()) return false;
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
            double distSq = golem.squaredDistanceTo(owner);
            if (distSq <= (minDistance * minDistance)) {
                golem.getNavigation().stop();
                return;
            }
            golem.getLookControl().lookAt(owner, 10.0F, (float) golem.getMaxLookPitchChange());
            if (--this.updateCountdown <= 0) {
                this.updateCountdown = 10;
                double moveSpeed = distSq > (maxDistance * maxDistance) ? speed * 1.5 : speed;
                golem.getNavigation().startMovingTo(owner, moveSpeed);
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
            if (CustomMethods.isEntityProtectedCheck(ownerAttacker, owner)) {
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
                            && !CustomMethods.isEntityProtectedCheck(newAttacker, owner)) {
                        this.attacker = newAttacker;
                        golem.setTarget(attacker);
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.attacker = null;
            golem.setTarget(null);
        }
    }

    private class AttackOwnerTargetGoal extends Goal {
        private final EarthGolemEntity golem;
        private LivingEntity ownerTarget;

        public AttackOwnerTargetGoal(EarthGolemEntity golem) {
            this.golem = golem;
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;
            LivingEntity target = getOwnerAttacking(owner);
            if (target == null || !target.isAlive() || target == golem) return false;
            if (CustomMethods.isEntityProtectedCheck(target, owner)) return false;
            this.ownerTarget = target;
            return true;
        }

        @Override
        public boolean shouldContinue() {
            if (ownerTarget == null || !ownerTarget.isAlive()) return false;
            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;
            LivingEntity currentOwnerTarget = getOwnerAttacking(owner);
            return (currentOwnerTarget != null && currentOwnerTarget.isAlive())
                    || golem.squaredDistanceTo(ownerTarget) <= 400.0;
        }

        @Override
        public void start() {
            golem.setTarget(ownerTarget);
        }

        @Override
        public void tick() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return;
            LivingEntity currentTarget = getOwnerAttacking(owner);
            if (currentTarget != null && currentTarget.isAlive()
                    && currentTarget != golem
                    && !CustomMethods.isEntityProtectedCheck(currentTarget, owner)) {
                this.ownerTarget = currentTarget;
                golem.setTarget(ownerTarget);
            }
        }

        private LivingEntity getOwnerAttacking(LivingEntity owner) {
            if (owner instanceof MobEntity mob) return mob.getTarget();
            return owner.getAttacking();
        }

        @Override
        public void stop() {
            this.ownerTarget = null;
            golem.setTarget(null);
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
                targetEntity = findNearestHostile();
                if (targetEntity != null) {
                    golem.setTarget(targetEntity);
                    return true;
                }
                return false;
            }

            LivingEntity owner = golem.getOwner();
            if (owner == null) return false;
            if (golem.squaredDistanceTo(owner) > 1024.0) return false;
            if (CustomMethods.isEntityProtectedCheck(targetEntity, owner)) return false;
            if (golem.squaredDistanceTo(targetEntity) > 400.0) return false;

            return true;
        }

        @Override
        public void start() {
            golem.setTarget(targetEntity);
        }

        @Override
        public void stop() {
            targetEntity = null;
            golem.setTarget(null);
            scanCountdown = 0;
        }

        @Override
        public void tick() {
            LivingEntity currentGolemTarget = golem.getTarget();

            if (currentGolemTarget == null && targetEntity != null && targetEntity.isAlive()) {
                golem.setTarget(targetEntity);
                return;
            }

            if (currentGolemTarget == targetEntity) {
                LivingEntity owner = golem.getOwner();
                if (owner != null) {
                    LivingEntity selfAttacker = golem.getAttacker();
                    if (selfAttacker != null && selfAttacker.isAlive() && selfAttacker != owner
                            && !CustomMethods.isEntityProtectedCheck(selfAttacker, owner)) {
                        targetEntity = selfAttacker;
                        golem.setTarget(targetEntity);
                    }
                }
            }
        }

        private LivingEntity findNearestHostile() {
            LivingEntity owner = golem.getOwner();
            if (owner == null) return null;

            LivingEntity directAttacker = golem.getAttacker();
            if (directAttacker != null && directAttacker.isAlive()
                    && directAttacker != owner
                    && !CustomMethods.isEntityProtectedCheck(directAttacker, owner)) {
                return directAttacker;
            }

            var nearbyEntities = golem.getWorld().getOtherEntities(
                golem,
                golem.getBoundingBox().expand(16.0),
                entity -> entity instanceof LivingEntity
            );

            LivingEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof LivingEntity livingEntity)) continue;
                if (!livingEntity.isAlive()) continue;
                if (entity == owner || entity == golem) continue;

                boolean isValidTarget = false;
                if (entity instanceof HostileEntity) {
                    isValidTarget = true;
                } else if (entity instanceof PlayerEntity otherPlayer) {
                    isValidTarget = !CustomMethods.isEntityProtectedCheck(otherPlayer, golem.getOwner());
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

}
