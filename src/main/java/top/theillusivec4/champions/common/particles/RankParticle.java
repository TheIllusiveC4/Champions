package top.theillusivec4.champions.common.particles;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class RankParticle extends SpriteTexturedParticle {

  private static final Random RANDOM = new Random();
  private final IAnimatedSprite spriteSet;

  private RankParticle(World p_i51008_1_, double p_i51008_2_, double p_i51008_4_,
      double p_i51008_6_, double p_i51008_8_, double p_i51008_10_, double p_i51008_12_,
      IAnimatedSprite p_i51008_14_) {
    super(p_i51008_1_, p_i51008_2_, p_i51008_4_, p_i51008_6_, 0.5D - RANDOM.nextDouble(),
        p_i51008_10_, 0.5D - RANDOM.nextDouble());
    this.spriteSet = p_i51008_14_;
    this.motionY *= 0.2F;

    if (p_i51008_8_ == 0.0D && p_i51008_12_ == 0.0D) {
      this.motionX *= 0.1F;
      this.motionZ *= 0.1F;
    }
    this.particleScale *= 0.75F;
    this.maxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
    this.canCollide = false;
    this.selectSpriteWithAge(p_i51008_14_);
  }

  @Nonnull
  @Override
  public IParticleRenderType getRenderType() {
    return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  @Override
  public void tick() {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;

    if (this.age++ >= this.maxAge) {
      this.setExpired();
    } else {
      this.selectSpriteWithAge(this.spriteSet);
      this.motionY += 0.004D;
      this.move(this.motionX, this.motionY, this.motionZ);

      if (this.posY == this.prevPosY) {
        this.motionX *= 1.1D;
        this.motionZ *= 1.1D;
      }
      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;

      if (this.onGround) {
        this.motionX *= 0.7F;
        this.motionZ *= 0.7F;
      }
    }
  }

  public static class RankFactory implements IParticleFactory<BasicParticleType> {

    private final IAnimatedSprite spriteSet;

    public RankFactory(IAnimatedSprite spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle makeParticle(@Nonnull BasicParticleType typeIn, @Nonnull World worldIn,
        double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      RankParticle rankParticle = new RankParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed,
          this.spriteSet);
      float f = worldIn.rand.nextFloat() * 0.5F + 0.35F;
      rankParticle.setColor((float) xSpeed * f, (float) ySpeed * f, (float) zSpeed * f);
      return rankParticle;
    }
  }
}
