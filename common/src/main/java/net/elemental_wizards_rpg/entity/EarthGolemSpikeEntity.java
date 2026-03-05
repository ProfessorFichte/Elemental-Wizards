package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.ElementalMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.target.EntityRelations;
import net.spell_power.api.SpellPower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class EarthGolemSpikeEntity extends Entity implements Ownable {
    public static EntityType<EarthGolemSpikeEntity> ENTITY_TYPE;

    public static final int WARMUP_TICKS = 5;
    public static final int ATTACK_TICK = 10;
    public static final int LIFETIME_TICKS = 20;
    public static final int DAMAGE_COOLDOWN = 10;

    private int warmup;
    private boolean startedAttack;
    private int ticksLeft;
    private boolean playingAnimation;
    private LivingEntity owner;
    private UUID ownerUuid;
    private int currentTick = 0;
    private Map<UUID, Integer> lastDamageTick = new HashMap<>();
    private Identifier spellId;

    public EarthGolemSpikeEntity(EntityType<? extends EarthGolemSpikeEntity> entityType, World world) {
        super(entityType, world);
        this.ticksLeft = LIFETIME_TICKS;
    }

    public EarthGolemSpikeEntity(World world, double x, double y, double z, float yaw, int warmup, LivingEntity owner, Identifier spellId) {
        this(ENTITY_TYPE, world);
        this.warmup = warmup;
        this.setOwner(owner);
        this.spellId = spellId;
        this.setYaw(yaw * 57.295776F);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(this.ownerUuid);
            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.warmup = nbt.getInt("Warmup");
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        if (nbt.contains("SpellId")) {
            this.spellId = Identifier.of(nbt.getString("SpellId"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        if (this.spellId != null) {
            nbt.putString("SpellId", this.spellId.toString());
        }
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
            if (this.playingAnimation) {
                --this.ticksLeft;
            }
        } else {
            currentTick++;

            if (--this.warmup < 0) {
                if (this.warmup >= -(ATTACK_TICK + 3) && this.warmup <= -(ATTACK_TICK - 3)) {
                    this.dealDamage();
                }

                if (!this.startedAttack) {
                    this.getWorld().sendEntityStatus(this, (byte)4);
                    this.startedAttack = true;
                }

                if (--this.ticksLeft < 0) {
                    this.discard();
                }
            }
        }
    }

    private void dealDamage() {
        List<LivingEntity> targets = this.getWorld().getNonSpectatingEntities(
            LivingEntity.class,
            this.getBoundingBox().expand(1.2, 0.5, 1.2)
        );

        for (LivingEntity target : targets) {
            if (target == owner) {
                continue;
            }
            if (owner != null && isProtected(owner, target)) {
                continue;
            }
            if (target instanceof EarthGolemEntity golemTarget) {
                LivingEntity golemOwner = golemTarget.getOwner();
                if (golemOwner != null && golemOwner == owner) {
                    continue;
                } else if (golemOwner != owner && isProtected(owner, golemOwner)){
                    continue;
                }
            }
            if (target instanceof TameableEntity tameable && tameable.isTamed()) {
                LivingEntity tameableOwner = tameable.getOwner();
                if (tameableOwner != null && tameableOwner == owner) {
                    continue;
                } else if (tameableOwner != owner && isProtected(owner, tameableOwner)){
                    continue;
                }
            }

            UUID targetId = target.getUuid();
            Integer lastTick = lastDamageTick.get(targetId);
            RegistryEntry<Spell> spellImpact = SpellRegistry.from(owner.getWorld()).getEntry(Identifier.of(MOD_ID, "terra_earth_golem_spike_impact")).get();
            if (lastTick == null || currentTick - lastTick >= DAMAGE_COOLDOWN) {
                SpellHelper.performImpacts(owner.getWorld(), owner, target, target, spellImpact,
                        spellImpact.value().impacts, new SpellHelper.ImpactContext().power(SpellPower.getSpellPower(spellImpact.value().school, owner)).position(this.getPos()));
                lastDamageTick.put(targetId, currentTick);
            }
        }
    }



    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == 4) {
            this.playingAnimation = true;
        }
    }

    public float getAnimationProgress(float tickDelta) {
        if (!this.playingAnimation) {
            return 0.0F;
        }

        int elapsed = LIFETIME_TICKS - this.ticksLeft;
        return Math.min(1.0F, ((float)elapsed - tickDelta) / (float)LIFETIME_TICKS);
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
