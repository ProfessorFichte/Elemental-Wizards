package net.elemental_wizards_rpg.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class HealingRainParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected HealingRainParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.red = 0.4F + this.random.nextFloat() * 0.1F;
        this.green = 0.8F + this.random.nextFloat() * 0.15F;
        this.blue = 1.0F;

        this.velocityX = velocityX;
        this.velocityY = velocityY - 0.2;
        this.velocityZ = velocityZ;

        this.scale = 0.15F + this.random.nextFloat() * 0.1F;

        this.maxAge = 20 + this.random.nextInt(20);

        this.alpha = 0.7F + this.random.nextFloat() * 0.3F;

        this.gravityStrength = 0.06F;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        this.velocityY -= this.gravityStrength;

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        this.velocityX *= 0.98;
        this.velocityZ *= 0.98;

        if (this.age > this.maxAge - 5) {
            this.alpha = Math.max(0, this.alpha - 0.15F);
        }

        if (this.onGround) {
            this.markDead();
        }

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                        double x, double y, double z,
                                        double velocityX, double velocityY, double velocityZ) {
            return new HealingRainParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
