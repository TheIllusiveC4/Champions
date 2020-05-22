package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class JailingAffix extends BasicAffix {

  public JailingAffix() {
    super("jailing", AffixCategory.CC);
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
      float amount) {

    if (target.getRNG().nextFloat() < ChampionsConfig.jailingChance && !target.isPotionActive(
        ChampionsRegistry.JAILED)) {
      target.addPotionEffect(new EffectInstance(ChampionsRegistry.JAILED, 200, 0));
    }
    return true;
  }
}
