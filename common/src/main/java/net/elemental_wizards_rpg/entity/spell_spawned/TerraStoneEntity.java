package net.elemental_wizards_rpg.entity.spell_spawned;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.SpellHelper;
import net.more_rpg_classes.util.CustomMethods;
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
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(TerraStoneEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier spellId;
    private UUID ownerUuid;
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
        this.ownerUuid = owner.getUuid();
        this.cachedOwner = owner;
        this.timeToLive = spawn.time_to_live_seconds * 20;
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
        this.ticksLeft = this.timeToLive;
        this.warmup = 0;
        this.startedAttack = false;
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
                this.getDataTracker().set(SPELL_ID_TRACKER, spellIdStr);
            }
        }
        if (nbt.containsUuid("Owner")) this.ownerUuid = nbt.getUuid("Owner");
        this.timeToLive = nbt.getInt("TimeToLive");
        this.warmup = nbt.getInt("Warmup");
        this.currentTick = nbt.getInt("CurrentTick");
        this.getDataTracker().set(TIME_TO_LIVE_TRACKER, this.timeToLive);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("SpellId", this.spellId.toString());
        if (this.ownerUuid != null) nbt.putUuid("Owner", this.ownerUuid);
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
            this.getBoundingBox().expand(0.5)
        );

        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        RegistryEntry<Spell> spellImpact = isFullyEmerged()
                ? SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "helper/terra_drip_circle_impact")).orElse(null)
                : null;

        for (LivingEntity entity : nearbyEntities) {
            if (entity == owner) continue;
            if (CustomMethods.isEntityProtectedCheck(entity, owner)) continue;

            double deltaX = entity.getX() - this.getX();
            double deltaZ = entity.getZ() - this.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance < 0.01) {
                deltaX = (Math.random() - 0.5) * 2;
                deltaZ = (Math.random() - 0.5) * 2;
                distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            }

            double pushStrength = 0.3;
            entity.setVelocity(entity.getVelocity().add((deltaX / distance) * pushStrength, 0.0, (deltaZ / distance) * pushStrength));
            entity.velocityModified = true;
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer));
            }

            if (spellImpact != null) {
                UUID targetId = entity.getUuid();
                Integer lastTick = lastDamageTick.get(targetId);
                if (lastTick == null || currentTick - lastTick >= DAMAGE_INTERVAL) {
                    SpellHelper.performImpacts(owner.getWorld(), owner, entity, owner, spellImpact,
                            spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()), false, null);
                    lastDamageTick.put(targetId, currentTick);
                    if (!spellImpact.value().impacts.isEmpty()) {
                        ParticleHelper.sendBatches(entity, spellImpact.value().impacts.get(0).particles);
                    }
                }
            }
        }
    }

    private void dealDamage() {
        if (!isFullyEmerged()) return;

        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "helper/terra_drip_circle_impact")).orElse(null);
        if (spellImpact == null) return;

        List<LivingEntity> targets = this.getWorld().getNonSpectatingEntities(
            LivingEntity.class,
            this.getBoundingBox().expand(0.5)
        );

        for (LivingEntity target : targets) {
            UUID targetId = target.getUuid();
            Integer lastTick = lastDamageTick.get(targetId);
            if (lastTick == null || currentTick - lastTick >= DAMAGE_INTERVAL) {
                SpellHelper.performImpacts(owner.getWorld(), owner, target, owner, spellImpact,
                        spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()), false, null);
                lastDamageTick.put(targetId, currentTick);
                if (!spellImpact.value().impacts.isEmpty()) {
                    ParticleHelper.sendBatches(target, spellImpact.value().impacts.get(0).particles);
                }
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
        return true;
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
        if (other == owner) {
            return false;
        }
        if (other instanceof LivingEntity) {
            return !CustomMethods.isEntityProtectedCheck(other, owner);
        }
        return super.collidesWith(other);
    }

}
