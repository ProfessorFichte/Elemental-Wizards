package net.elemental_wizards_rpg.entity;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.elemental_wizards_rpg.ElementalMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_power.api.SpellPower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TerraStoneEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<TerraStoneEntity> ENTITY_TYPE;
    public static final int DAMAGE_INTERVAL = 20;

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(TerraStoneEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> OWNER_ID_TRACKER = DataTracker.registerData(TerraStoneEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(TerraStoneEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private int ownerId;
    private int timeToLive;
    private LivingEntity cachedOwner = null;

    private int warmup;
    private boolean startedAttack;
    private int ticksLeft;
    private boolean playingAnimation;
    private int currentTick = 0;

    private Map<UUID, Integer> lastDamageTick = new HashMap<>();

    public TerraStoneEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setBoundingBox(this.getDimensions(this.getPose()).getBoxAt(this.getPos()));
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
        this.ticksLeft = this.timeToLive;
        this.warmup = 0;
        this.startedAttack = false;
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
        this.spellId = Identifier.of(nbt.getString("SpellId"));
        this.ownerId = nbt.getInt("OwnerId");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.warmup = nbt.getInt("Warmup");
        this.currentTick = nbt.getInt("CurrentTick");

        this.getDataTracker().set(SPELL_ID_TRACKER, this.spellId.toString());
        this.getDataTracker().set(OWNER_ID_TRACKER, this.ownerId);
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("SpellId", this.spellId.toString());
        nbt.putInt("OwnerId", this.ownerId);
        nbt.putInt("TimeToLive", this.timeToLive);
        nbt.putInt("Warmup", this.warmup);
        nbt.putInt("CurrentTick", this.currentTick);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.discard();
                return;
            }
        }

        if (this.getWorld().isClient) {
            if (!this.playingAnimation && this.timeToLive > 0) {
                this.playingAnimation = true;
                this.ticksLeft = this.timeToLive;
            }

            if (this.playingAnimation) {
                --this.ticksLeft;
            }
        } else {
            currentTick++;

            if (this.warmup > 0) {
                this.warmup--;
                return;
            }

            if (!this.startedAttack) {
                this.getWorld().sendEntityStatus(this, (byte) 4);
                this.startedAttack = true;
            }

            if (currentTick % DAMAGE_INTERVAL == 0) {
                dealDamage();
            }

            handleManualCollision();
            if (this.age > this.timeToLive) {
                this.discard();
            }
        }
    }

    private void handleManualCollision() {

        List<LivingEntity> nearbyEntities = this.getWorld().getNonSpectatingEntities(
            LivingEntity.class,
            this.getBoundingBox()
        );

        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return;
        }

        for (LivingEntity entity : nearbyEntities) {
            if (entity.getId() == owner.getId()) {
                continue;
            }

            if (isProtected(entity)) {
                continue;
            }

            double deltaX = entity.getX() - this.getX();
            double deltaZ = entity.getZ() - this.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance < 0.01) {
                deltaX = (Math.random() - 0.5) * 2;
                deltaZ = (Math.random() - 0.5) * 2;
                distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            }

            double pushStrength = 0.3;
            double pushX = (deltaX / distance) * pushStrength;
            double pushZ = (deltaZ / distance) * pushStrength;

            entity.setVelocity(entity.getVelocity().add(pushX, 0.0, pushZ));
            entity.velocityModified = true;

            if (isFullyEmerged()) {
                UUID targetId = entity.getUuid();
                Integer lastTick = lastDamageTick.get(targetId);
                if (lastTick == null || currentTick - lastTick >= DAMAGE_INTERVAL) {
                    RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "terra_drip_circle_impact")).get();
                    SpellHelper.performImpacts(owner.getWorld(), owner, entity, entity, spellImpact,
                            spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()));
                    lastDamageTick.put(targetId, currentTick);
                    ParticleHelper.sendBatches(entity, spellImpact.value().impacts.get(0).particles);
                }
            }
        }
    }

    private void dealDamage() {
        if (!isFullyEmerged()) {
            return;
        }

        List<LivingEntity> targets = this.getWorld().getNonSpectatingEntities(
            LivingEntity.class,
            this.getBoundingBox()
        );
        LivingEntity owner = this.getOwner();

        for (LivingEntity target : targets) {
            RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "terra_drip_circle_impact")).get();
            UUID targetId = target.getUuid();
            Integer lastTick = lastDamageTick.get(targetId);
            if (lastTick == null || currentTick - lastTick >= DAMAGE_INTERVAL) {
                SpellHelper.performImpacts(owner.getWorld(), owner, target, target, spellImpact,
                        spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()));
                lastDamageTick.put(targetId, currentTick);
                ParticleHelper.sendBatches(target, spellImpact.value().impacts.get(0).particles);
            }
        }
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == 4) {
            this.playingAnimation = true;
            this.ticksLeft = this.timeToLive;
        }
    }

    public float getAnimationProgress(float tickDelta) {
        if (!this.playingAnimation) {
            return 0.0F;
        }

        int elapsed = this.timeToLive - this.ticksLeft;
        float totalProgress = ((float) elapsed - tickDelta) / (float) this.timeToLive;

        if (totalProgress < 0.1F) {
            return totalProgress * 10.0F;
        } else if (totalProgress > 0.9F) {
            return 1.0F - ((totalProgress - 0.9F) * 10.0F);
        } else {
            return 1.0F;
        }
    }

    private boolean isFullyEmerged() {
        float progress = (float) this.age / (float) this.timeToLive;
        return progress >= 0.1F && progress <= 0.9F;
    }

    @Nullable
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

    public boolean isProtected(Entity other) {
        var owner = this.getOwner();
        if (owner == null) {
            return false;
        }
        if (other.getId() == owner.getId()) {
            return true;
        }
        var relation = EntityRelations.getRelation(owner, other);
        switch (relation) {
            case ALLY, FRIENDLY -> {
                return true;
            }
            case NEUTRAL, MIXED, HOSTILE -> {
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

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive();
    }

    @Override
    public boolean collidesWith(Entity other) {
        var owner = this.getOwner();
        if (owner == null) {
            return super.collidesWith(other);
        }
        if (other.getId() == owner.getId()) {
            return false;
        }
        if (other instanceof LivingEntity otherLiving) {
            return !isProtected(otherLiving);
        }
        return super.collidesWith(other);
    }

}
