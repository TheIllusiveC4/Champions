package top.theillusivec4.champions.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ArcticBulletEntity extends AbstractBulletEntity {

  public ArcticBulletEntity(Level world) {
    super(ChampionsRegistry.ARCTIC_BULLET, world);
  }

  @OnlyIn(Dist.CLIENT)
  public ArcticBulletEntity(Level worldIn, double x, double y, double z, double motionXIn,
      double motionYIn, double motionZIn) {
    super(ChampionsRegistry.ARCTIC_BULLET, worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
  }

  public ArcticBulletEntity(Level worldIn, LivingEntity ownerIn, Entity targetIn, Direction.Axis direction) {
    super(ChampionsRegistry.ARCTIC_BULLET, worldIn, ownerIn, targetIn, direction);
  }

  @Override
  protected void bulletEffect(Entity target) {

    if (target instanceof LivingEntity) {
      ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
      ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2));
    }
  }

  @Override
  protected ParticleOptions getParticle() {
    return ParticleTypes.ITEM_SNOWBALL;
  }
}
