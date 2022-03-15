package top.theillusivec4.champions.common.affix;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    if (!livingEntity.getLevel().isClientSide()) {

      if (livingEntity.tickCount % 10 == 0) {
        List<Entity> list = livingEntity.getLevel().getEntities(livingEntity,
            livingEntity.getBoundingBox().inflate(ChampionsConfig.plaguedRange),
            entity -> entity instanceof LivingEntity && BasicAffix
                .canTarget(livingEntity, (LivingEntity) entity, true));
        list.forEach(entity -> {

          if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(
                new MobEffectInstance(ChampionsConfig.plaguedEffect.getEffect(),
                    ChampionsConfig.plaguedEffect.getDuration(),
                    ChampionsConfig.plaguedEffect.getAmplifier()));
          }
        });
        livingEntity.removeEffect(ChampionsConfig.plaguedEffect.getEffect());
      }
    } else {
      float radius = ChampionsConfig.plaguedRange;
      float circle = (float) Math.PI * radius * radius;

      for (int circleParticles = 0; (float) circleParticles < circle; ++circleParticles) {
        float f6 = livingEntity.getRandom().nextFloat() * ((float) Math.PI * 2F);
        float randomRadiusSection = Mth.sqrt(livingEntity.getRandom().nextFloat()) * radius;
        float f8 = Mth.cos(f6) * randomRadiusSection;
        float f9 = Mth.sin(f6) * randomRadiusSection;
        int l1 = ChampionsConfig.plaguedEffect.getEffect().getColor();
        int i2 = l1 >> 16 & 255;
        int j2 = l1 >> 8 & 255;
        int j1 = l1 & 255;
        livingEntity.getLevel()
            .addParticle(ParticleTypes.ENTITY_EFFECT, livingEntity.position().x + (double) f8,
                livingEntity.position().y, livingEntity.position().z + (double) f9,
                ((float) i2 / 255.0F), ((float) j2 / 255.0F), ((float) j1 / 255.0F));
      }
    }
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
                          float amount) {
    target.addEffect(new MobEffectInstance(ChampionsConfig.plaguedEffect.getEffect(),
        ChampionsConfig.plaguedEffect.getDuration(), ChampionsConfig.plaguedEffect.getAmplifier()));
    return true;
  }
}
