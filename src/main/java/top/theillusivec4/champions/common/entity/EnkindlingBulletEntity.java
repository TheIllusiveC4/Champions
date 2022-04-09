package top.theillusivec4.champions.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class EnkindlingBulletEntity extends AbstractBulletEntity {

  public EnkindlingBulletEntity(World world) {
    super(ChampionsRegistry.ENKINDLING_BULLET, world);
  }

  @OnlyIn(Dist.CLIENT)
  public EnkindlingBulletEntity(World worldIn, double x, double y, double z, double motionXIn,
                                double motionYIn, double motionZIn) {
    super(ChampionsRegistry.ENKINDLING_BULLET, worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
  }

  public EnkindlingBulletEntity(World worldIn, LivingEntity ownerIn, Entity targetIn,
                                Axis direction) {
    super(ChampionsRegistry.ENKINDLING_BULLET, worldIn, ownerIn, targetIn, direction);
  }

  @Override
  protected void bulletEffect(Entity target) {

    if (this.func_234616_v_() != null) {
      target.attackEntityFrom(new IndirectEntityDamageSource("cinderBullet.indirect", this,
          this.func_234616_v_()).setFireDamage().setMagicDamage(), 1111);
    } else {
      target.attackEntityFrom(
          new EntityDamageSource("cinderBullet", this).setFireDamage().setMagicDamage(), 1111);
    }
    target.setFire(8);
  }

  @Override
  protected IParticleData getParticle() {
    return ParticleTypes.FLAME;
  }
}
