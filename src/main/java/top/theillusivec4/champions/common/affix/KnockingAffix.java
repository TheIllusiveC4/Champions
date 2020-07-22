package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
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
    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
    double d1 = target.getPosX() - champion.getLivingEntity().getPosX();

    double d0;
    for (d0 = target.getPosZ() - champion.getLivingEntity().getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D;
        d0 = (Math.random() - Math.random()) * 0.01D) {
      d1 = (Math.random() - Math.random()) * 0.01D;
    }
    target.applyKnockback(0.4F * (float) ChampionsConfig.knockingMultiplier, d1, d0);
    return true;
  }
}
