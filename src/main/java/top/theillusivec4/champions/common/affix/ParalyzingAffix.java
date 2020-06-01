package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ParalyzingAffix extends BasicAffix {

  public ParalyzingAffix() {
    super("paralyzing", AffixCategory.CC);
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
      float amount) {

    if (target.getRNG().nextFloat() < ChampionsConfig.paralyzingChance && !target.isPotionActive(
        ChampionsRegistry.PARALYSIS)) {
      target.addPotionEffect(new EffectInstance(ChampionsRegistry.PARALYSIS, 60, 0));
    }
    return true;
  }
}
