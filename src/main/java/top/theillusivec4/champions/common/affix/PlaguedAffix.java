package top.theillusivec4.champions.common.affix;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class PlaguedAffix extends BasicAffix {

  public PlaguedAffix() {
    super("plagued", AffixCategory.OFFENSE);
  }

  @Override
  public void onUpdate(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity.ticksExisted % 10 == 0) {
      List<Entity> list = livingEntity.getEntityWorld().getEntitiesInAABBexcluding(livingEntity,
          livingEntity.getBoundingBox().grow(ChampionsConfig.plaguedRange),
          entity -> entity instanceof LivingEntity && BasicAffix
              .canTarget(livingEntity, (LivingEntity) entity, true));
      list.forEach(entity -> {

        if (entity instanceof LivingEntity) {
          ((LivingEntity) entity).addPotionEffect(
              new EffectInstance(ChampionsConfig.plaguedEffect.getPotion(),
                  ChampionsConfig.plaguedEffect.getDuration(),
                  ChampionsConfig.plaguedEffect.getAmplifier()));
        }
      });
    }
    float radius = ChampionsConfig.plaguedRange;
    float f5 = (float) Math.PI * radius * radius;

    for (int k1 = 0; (float) k1 < f5; ++k1) {
      float f6 = livingEntity.getRNG().nextFloat() * ((float) Math.PI * 2F);
      float f7 = MathHelper.sqrt(livingEntity.getRNG().nextFloat()) * radius;
      float f8 = MathHelper.cos(f6) * f7;
      float f9 = MathHelper.sin(f6) * f7;
      int l1 = ChampionsConfig.plaguedEffect.getPotion().getLiquidColor();
      int i2 = l1 >> 16 & 255;
      int j2 = l1 >> 8 & 255;
      int j1 = l1 & 255;
      ((ServerWorld) livingEntity.getEntityWorld())
          .spawnParticle(ParticleTypes.ENTITY_EFFECT, livingEntity.getPosX() + (double) f8,
              livingEntity.getPosY(), livingEntity.getPosZ() + (double) f9, 0,
              ((float) i2 / 255.0F), ((float) j2 / 255.0F), ((float) j1 / 255.0F), 1.0F);
    }
    livingEntity.removeActivePotionEffect(ChampionsConfig.plaguedEffect.getPotion());
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
      float amount) {
    target.addPotionEffect(new EffectInstance(ChampionsConfig.plaguedEffect.getPotion(),
        ChampionsConfig.plaguedEffect.getDuration(), ChampionsConfig.plaguedEffect.getAmplifier()));
    return true;
  }
}
