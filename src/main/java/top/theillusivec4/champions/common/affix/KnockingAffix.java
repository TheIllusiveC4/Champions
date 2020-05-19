package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class KnockingAffix extends BasicAffix {

  public KnockingAffix() {
    super("knocking", AffixCategory.CC);
  }

  @Override
  public float onKnockBack(IChampion champion, LivingEntity target, float strength, float newStrength) {
    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
    return strength * (float) ChampionsConfig.knockingMultiplier;
  }
}
