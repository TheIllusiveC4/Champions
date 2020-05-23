package top.theillusivec4.champions.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction.Axis;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ArcticBulletEntity extends AbstractBulletEntity {

  public ArcticBulletEntity(World world) {
    super(ChampionsRegistry.ARCTIC_BULLET, world);
  }

  @OnlyIn(Dist.CLIENT)
  public ArcticBulletEntity(World worldIn, double x, double y, double z, double motionXIn,
      double motionYIn, double motionZIn) {
    super(ChampionsRegistry.ARCTIC_BULLET, worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
  }

  public ArcticBulletEntity(World worldIn, LivingEntity ownerIn, Entity targetIn, Axis direction) {
    super(ChampionsRegistry.ARCTIC_BULLET, worldIn, ownerIn, targetIn, direction);
  }

  @Override
  protected void bulletEffect(Entity target) {

    if (target instanceof LivingEntity) {
      ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
      ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 100, 2));
    }
  }

  @Override
  protected IParticleData getParticle() {
    return ParticleTypes.ITEM_SNOWBALL;
  }
}
