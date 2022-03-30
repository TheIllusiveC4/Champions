package top.theillusivec4.champions.common.entity;

import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class EnkindlingBulletEntity extends BaseBulletEntity {

  public EnkindlingBulletEntity(Level level) {
    super(ChampionsRegistry.ENKINDLING_BULLET, level);
  }

  public EnkindlingBulletEntity(Level level, LivingEntity livingEntity, @Nonnull Entity entity,
                                Direction.Axis axis) {
    super(ChampionsRegistry.ENKINDLING_BULLET, level, livingEntity, entity, axis);
  }

  @Override
  protected void bulletEffect(LivingEntity target) {

    if (this.getOwner() != null) {
      target.hurt(
          new IndirectEntityDamageSource("cinderBullet.indirect", this, this.getOwner()).setIsFire()
              .setMagic(), 1);
    } else {
      target.hurt(new EntityDamageSource("cinderBullet", this).setIsFire().setMagic(), 1);
    }
    target.setSecondsOnFire(8);
  }

  @Override
  protected ParticleOptions getParticle() {
    return ParticleTypes.FLAME;
  }
}
