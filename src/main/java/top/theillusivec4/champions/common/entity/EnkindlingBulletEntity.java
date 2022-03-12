package top.theillusivec4.champions.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class EnkindlingBulletEntity extends AbstractBulletEntity {

  public EnkindlingBulletEntity(Level world) {
    super(ChampionsRegistry.ENKINDLING_BULLET, world);
  }

  @OnlyIn(Dist.CLIENT)
  public EnkindlingBulletEntity(Level worldIn, double x, double y, double z, double motionXIn,
      double motionYIn, double motionZIn) {
    super(ChampionsRegistry.ENKINDLING_BULLET, worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
  }

  public EnkindlingBulletEntity(Level worldIn, LivingEntity ownerIn, Entity targetIn,
      Direction.Axis direction) {
    super(ChampionsRegistry.ENKINDLING_BULLET, worldIn, ownerIn, targetIn, direction);
  }

  @Override
  protected void bulletEffect(Entity target) {

    if (target instanceof LivingEntity) {
      target.hurt(
          new IndirectEntityDamageSource("cinderBullet", this.getOwner(), this).setIsFire()
              .setMagic(), 1);
      target.setSecondsOnFire(8);
    }
  }

  @Override
  protected ParticleOptions getParticle() {
    return ParticleTypes.FLAME;
  }
}
