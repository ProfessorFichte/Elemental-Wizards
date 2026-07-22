package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.entity.util.PeriodicAreaImpact;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.more_rpg_classes.util.CustomMethods;

import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class EarthGolemSpikeEntity extends Entity implements Ownable, SpellEntity.Spawned {
    public static EntityType<EarthGolemSpikeEntity> ENTITY_TYPE;

    public static final int ATTACK_TICK = 10;
    public static final int LIFETIME_TICKS = 20;

    private boolean startedAttack;
    private int ticksLeft;
    private boolean playingAnimation;
    private LivingEntity owner;
    private UUID ownerUuid;
    private EarthGolemEntity golem;
    private Identifier spellId;

    public EarthGolemSpikeEntity(EntityType<? extends EarthGolemSpikeEntity> entityType, World world) {
        super(entityType, world);
        this.ticksLeft = LIFETIME_TICKS;
    }

    @Override
    public void onSpawnedBySpell(Args args) {
        this.spellId = args.spell().getKey().get().getValue();

        if (args.owner() instanceof EarthGolemEntity g) {
            this.golem = g;
            this.setOwner(g.getOwner());
        } else {
            this.setOwner(args.owner());
        }
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
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        if (nbt.contains("SpellId")) {
            this.spellId = Identifier.of(nbt.getString("SpellId"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
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
            if (!this.startedAttack) {
                this.getWorld().sendEntityStatus(this, (byte) 4);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ElementalSounds.GOLEM_SPIKE_SUMMON.soundEvent(), SoundCategory.NEUTRAL,
                        1.0F, 0.85F + this.getRandom().nextFloat() * 0.3F);
                this.startedAttack = true;
            }

            if (this.age == ATTACK_TICK) {
                this.dealDamage();
            }

            if (this.age > LIFETIME_TICKS) {
                this.discard();
            }
        }
    }

    private void dealDamage() {
        if (golem == null) return;
        LivingEntity player = golem.getOwner();
        if (player == null) return;

        Box box = this.getBoundingBox().expand(1.2, 0.5, 1.2);
        PeriodicAreaImpact.apply(this.getWorld(), golem, this, box,
                Identifier.of(MOD_ID, "helper/terra_earth_golem_spike_impact"),
                target -> {
                    if (target == player) return false;
                    if (target == golem) return false;
                    if (CustomMethods.isEntityProtectedCheck(target, player)) return false;
                    if (target instanceof EarthGolemEntity golemTarget) {
                        LivingEntity golemOwner = golemTarget.getOwner();
                        if (golemOwner != null && golemOwner == player) return false;
                        if (golemOwner != null && golemOwner != player && CustomMethods.isEntityProtectedCheck(golemOwner, player)) return false;
                    }
                    if (target instanceof TameableEntity tameable && tameable.isTamed()) {
                        LivingEntity tameableOwner = tameable.getOwner();
                        if (tameableOwner != null && tameableOwner == player) return false;
                        if (tameableOwner != null && tameableOwner != player && CustomMethods.isEntityProtectedCheck(tameableOwner, player)) return false;
                    }
                    return true;
                },
                this.getPos(), true,
                target -> {
                    if (target instanceof MobEntity mobTarget) {
                        mobTarget.setTarget(golem);
                    }
                });
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

}
