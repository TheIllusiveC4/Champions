package top.theillusivec4.champions.common.particle;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class RankParticle extends TextureSheetParticle {
  private static final Random RANDOM = new Random();
  private final SpriteSet spriteSet;

  private RankParticle(ClientLevel p_i51008_1_, double p_i51008_2_, double p_i51008_4_,
                       double p_i51008_6_, double p_i51008_8_, double p_i51008_10_,
                       double p_i51008_12_, SpriteSet spriteSet) {
    super(p_i51008_1_, p_i51008_2_, p_i51008_4_, p_i51008_6_, 0.5D - RANDOM.nextDouble(),
        p_i51008_10_, 0.5D - RANDOM.nextDouble());
    this.spriteSet = spriteSet;
    this.yd *= 0.2F;

    if (p_i51008_8_ == 0.0D && p_i51008_12_ == 0.0D) {
      this.xd *= 0.1F;
      this.zd *= 0.1F;
    }
    this.quadSize *= 0.75F;
    this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
    this.hasPhysics = false;
    this.setSpriteFromAge(spriteSet);
  }

  @Override
  public void tick() {
    this.xo = this.x;
    this.yo = this.y;
    this.zo = this.z;

    if (this.age++ >= this.lifetime) {
      this.remove();
    } else {
      this.setSpriteFromAge(this.spriteSet);
      this.yd += 0.004D;
      this.move(this.xd, this.yd, this.zd);

      if (this.y == this.yo) {
        this.xd *= 1.1D;
        this.zd *= 1.1D;
      }
      this.xd *= 0.96F;
      this.yd *= 0.96F;
      this.zd *= 0.96F;

      if (this.onGround) {
        this.xd *= 0.7F;
        this.zd *= 0.7F;
      }
    }
  }

  @Nonnull
  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  public record RankFactory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

    @Override
    public Particle createParticle(
        @Nonnull SimpleParticleType typeIn, @Nonnull ClientLevel worldIn,
        double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      RankParticle rankParticle =
          new RankParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      float f = worldIn.random.nextFloat() * 0.5F + 0.35F;
      rankParticle.setColor((float) xSpeed * f, (float) ySpeed * f, (float) zSpeed * f);
      return rankParticle;
    }
  }
}