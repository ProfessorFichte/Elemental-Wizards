package net.elemental_wizards_rpg.entity;

import net.elemental_wizards_rpg.entity.util.PeriodicAreaImpact;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.entity.SpellEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.SpellHelper;
import net.more_rpg_classes.util.CustomMethods;

import java.util.UUID;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class TornadoEntity extends Entity implements SpellEntity.Spawned {
    public static EntityType<TornadoEntity> ENTITY_TYPE;
    private static final Identifier TORNADO_SPELL_ID = Identifier.of(MOD_ID, "wind_tornado");
    private static final float BASE_RANGE = 20.0F;

    private static final float PULL_RADIUS = 5.0F;
    private static final float PULL_STRENGTH = 0.15F;
    private static final float LEVITATE_STRENGTH = 0.1F;

    private static final int DAMAGE_INTERVAL = 20;

    private static final TagKey<EntityType<?>> BOSSES_TAG = TagKey.of(
        net.minecraft.registry.RegistryKeys.ENTITY_TYPE,
        Identifier.of("c", "bosses")
    );

    private static final TrackedData<String> SPELL_ID_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TIME_TO_LIVE_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE_TRACKER = DataTracker.registerData(TornadoEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private Identifier spellId;
    private UUID ownerUuid;
    private int timeToLive;
    private LivingEntity cachedOwner = null;
    private RegistryEntry<Spell> spellEntry = null;

    public TornadoEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
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

        SpellRegistry.from(owner.getWorld()).getEntry(TORNADO_SPELL_ID).ifPresent(e -> this.spellEntry = e);
        float scale = this.spellEntry != null ? SpellHelper.getRange(owner, this.spellEntry) / BASE_RANGE : 1.0F;
        this.getDataTracker().set(SCALE_TRACKER, scale);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SPELL_ID_TRACKER, "");
        builder.add(TIME_TO_LIVE_TRACKER, 0);
        builder.add(SCALE_TRACKER, 1.0F);
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

    public float getScale() {
        return this.getDataTracker().get(SCALE_TRACKER);
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
    }

    private boolean idleSoundFired = false;
    @Override
    public void tick() {
        super.tick();

        var world = this.getWorld();
        if (world.isClient) return;
        if (!idleSoundFired) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    ElementalSounds.WIND_ENTITY_LOOP.soundEvent(), SoundCategory.PLAYERS, 0.3F, 0.85F);
            idleSoundFired = true;
        }

        if (!this.getWorld().isClient()) {
            var owner = this.getOwner();
            if (owner == null || owner.isRemoved() || !owner.isAlive()) {
                this.discard();
                return;
            }

            if (this.age > this.timeToLive) {
                this.discard();
                return;
            }
            pullAndLevitateEntities();

            if (this.age % DAMAGE_INTERVAL == 0) {
                damageEntities();
            }
        }
    }

    private void pullAndLevitateEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        float pullRadius = PULL_RADIUS * getScale();
        Vec3d tornadoCenter = this.getPos().add(0, 1.0, 0);
        Box searchBox = Box.of(tornadoCenter, pullRadius * 2, pullRadius * 2, pullRadius * 2);

        var entities = this.getWorld().getOtherEntities(this, searchBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (entity == owner) continue;

            if (CustomMethods.isEntityProtectedCheck(entity, owner)) continue;

            if (!canLevitate(entity)) {
                continue;
            }

            Vec3d entityPos = entity.getPos();
            Vec3d direction = tornadoCenter.subtract(entityPos).normalize();

            double horizontalPull = PULL_STRENGTH;
            entity.addVelocity(
                direction.x * horizontalPull,
                LEVITATE_STRENGTH,
                direction.z * horizontalPull
            );
            entity.velocityModified = true;
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer));
            }
        }
    }

    private void damageEntities() {
        var owner = this.getOwner();
        if (owner == null) return;

        float pullRadius = PULL_RADIUS * getScale();
        Vec3d tornadoCenter = this.getPos().add(0, 1.0, 0);
        Box damageBox = Box.of(tornadoCenter, pullRadius * 2, pullRadius * 2, pullRadius * 2);

        PeriodicAreaImpact.apply(this.getWorld(), owner, this, damageBox,
                Identifier.of(MOD_ID, "helper/wind_tornado_impact"),
                entity -> entity != owner && !CustomMethods.isEntityProtectedCheck(entity, owner),
                this.getPos(), false, null);
    }

    private boolean canLevitate(Entity entity) {
        return !entity.getType().isIn(BOSSES_TAG);
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
        return super.getDimensions(pose).scaled(3.0F * getScale());
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
