package top.theillusivec4.champions.common.affix;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class KnockingAffix extends BasicAffix {
  public KnockingAffix() {
    super("knocking", AffixCategory.CC);
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
                          float amount) {
    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
    LivingEntity livingEntity = champion.getLivingEntity();
    target.knockback((float) ChampionsConfig.knockingMultiplier,
        Mth.sin(livingEntity.getYRot() * ((float) Math.PI / 180F)),
        (-Mth.cos(livingEntity.getYRot() * ((float) Math.PI / 180F))));
    return true;
  }
}
