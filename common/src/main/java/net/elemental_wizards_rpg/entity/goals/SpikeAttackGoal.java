package net.elemental_wizards_rpg.entity.goals;

import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemEntity;
import net.elemental_wizards_rpg.entity.spell_spawned.EarthGolemSpikeEntity;
import net.elemental_wizards_rpg.spell.ElementalSounds;
import net.minecraft.sound.SoundCategory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.event.GameEvent;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;

import java.util.EnumSet;

import static net.elemental_wizards_rpg.ElementalMod.MOD_ID;

public class SpikeAttackGoal extends Goal {
    private final EarthGolemEntity golem;
    private int attackCooldown;
    private LivingEntity cachedTarget;
    private int animationTicksRemaining;
    private boolean spikesFired;
    private static final int ATTACK_ANIMATION_LENGTH = 33;
    private static final int SPIKE_FIRE_TICK = 8;
    private static final int COOLDOWN_TICKS = 5;
    private static final double SPIKE_SPACING = 1.0;
    private static final double MIN_ATTACK_RANGE = 8.0;
    private static final Identifier SPIKE_SPELL_ID = Identifier.of(MOD_ID, "helper/terra_earth_golem_spike_impact");
    private final int spikeCount;

    public SpikeAttackGoal(EarthGolemEntity golem) {
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));

        int count = 8;
        try {
            RegistryEntry<Spell> spellEntry = SpellRegistry.from(golem.getWorld()).getEntry(SPIKE_SPELL_ID).get();
            count = (int) spellEntry.value().range;
        } catch (Exception ignored) {}
        this.spikeCount = count;
    }

    @Override
    public boolean canStart() {
        if (attackCooldown > 0) {
            attackCooldown--;
            return false;
        }

        LivingEntity target = golem.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distance = golem.squaredDistanceTo(target);
        if (distance <= MIN_ATTACK_RANGE * MIN_ATTACK_RANGE) {
            this.cachedTarget = target;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.animationTicksRemaining = ATTACK_ANIMATION_LENGTH;
        this.spikesFired = false;
        golem.getNavigation().stop();
        golem.setAttacking(true);
        golem.getWorld().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                ElementalSounds.GOLEM_GROWL.soundEvent(), SoundCategory.NEUTRAL,
                1.2F, 0.85F + golem.getRandom().nextFloat() * 0.15F);
    }

    @Override
    public void tick() {
        if (cachedTarget == null || !cachedTarget.isAlive()) {
            return;
        }

        golem.getLookControl().lookAt(cachedTarget, 30.0F, 30.0F);
        double dx = cachedTarget.getX() - golem.getX();
        double dz = cachedTarget.getZ() - golem.getZ();
        float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        golem.setBodyYaw(targetYaw);
        golem.setHeadYaw(targetYaw);

        int ticksElapsed = ATTACK_ANIMATION_LENGTH - animationTicksRemaining;

        if (!spikesFired && ticksElapsed >= SPIKE_FIRE_TICK) {
            golem.getWorld().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                    ElementalSounds.GOLEM_GROUND_SLAM.soundEvent(), SoundCategory.NEUTRAL,
                    1.5F, 0.9F + golem.getRandom().nextFloat() * 0.2F);
            performSpikeAttack();
            spikesFired = true;
            this.attackCooldown = COOLDOWN_TICKS;
        }

        animationTicksRemaining--;
    }

    @Override
    public boolean shouldContinue() {
        return animationTicksRemaining > 0;
    }

    @Override
    public void stop() {
        this.cachedTarget = null;
        this.animationTicksRemaining = 0;
        this.spikesFired = false;
        golem.setAttacking(false);
    }

    private void performSpikeAttack() {
        if (golem.getWorld().isClient()) return;

        LivingEntity target = golem.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        float angleToTarget = (float) MathHelper.atan2(
            target.getZ() - golem.getZ(),
            target.getX() - golem.getX()
        );

        spawnSpikeLine(angleToTarget, spikeCount);
    }

    private void spawnSpikeLine(float angle, int length) {
        for (int i = 0; i < length; i++) {
            double distance = SPIKE_SPACING * (double)(i + 1);
            int warmup = i * 2;

            double x = golem.getX() + (double)MathHelper.cos(angle) * distance;
            double z = golem.getZ() + (double)MathHelper.sin(angle) * distance;

            spawnSpike(x, z, angle, warmup);
        }
    }

    private void spawnSpike(double x, double z, float yaw, int warmup) {
        double minY = golem.getY() - 1.0;
        double maxY = golem.getY() + 2.0;

        BlockPos blockPos = BlockPos.ofFloored(x, maxY, z);
        boolean foundGround = false;
        double yOffset = 0.0;

        do {
            BlockPos belowPos = blockPos.down();
            BlockState blockState = golem.getWorld().getBlockState(belowPos);

            if (blockState.isSideSolidFullSquare(golem.getWorld(), belowPos, Direction.UP)) {
                if (!golem.getWorld().isAir(blockPos)) {
                    BlockState aboveState = golem.getWorld().getBlockState(blockPos);
                    VoxelShape voxelShape = aboveState.getCollisionShape(golem.getWorld(), blockPos);
                    if (!voxelShape.isEmpty()) {
                        yOffset = voxelShape.getMax(Direction.Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }

            blockPos = blockPos.down();
        } while(blockPos.getY() >= MathHelper.floor(minY) - 1);

        if (foundGround) {
            LivingEntity spikeOwner = golem.getOwner() != null ? golem.getOwner() : golem;
            EarthGolemSpikeEntity spike = new EarthGolemSpikeEntity(
                golem.getWorld(),
                x,
                (double)blockPos.getY() + yOffset,
                z,
                yaw,
                warmup,
                spikeOwner,
                SPIKE_SPELL_ID
            );

            golem.getWorld().spawnEntity(spike);
            golem.getWorld().emitGameEvent(
                GameEvent.ENTITY_PLACE,
                new Vec3d(x, (double)blockPos.getY() + yOffset, z),
                GameEvent.Emitter.of(golem)
            );
        }
    }
}
