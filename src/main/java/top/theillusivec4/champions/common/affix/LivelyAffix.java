package top.theillusivec4.champions.common.affix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class LivelyAffix extends BasicAffix {

  public LivelyAffix() {
    super("lively", AffixCategory.DEFENSE);
  }

  @Override
  public float onDamage(IChampion champion, DamageSource source, float amount, float newAmount) {
    AffixData.IntegerData lastAttackTime = AffixData
        .getData(champion, this.getIdentifier(), AffixData.IntegerData.class);
    LivingEntity livingEntity = champion.getLivingEntity();
    lastAttackTime.num = (int) livingEntity.level.getGameTime();
    lastAttackTime.saveData();
    return super.onDamage(champion, source, amount, newAmount);
  }

  @Override
  public void onServerUpdate(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity.tickCount % 20 == 0) {
      AffixData.IntegerData lastAttackTime =
          AffixData.getData(champion, this.getIdentifier(), AffixData.IntegerData.class);

      if ((lastAttackTime.num + ChampionsConfig.livelyCooldown * 20L) <
          livingEntity.getLevel().getGameTime()) {
        double heal = ChampionsConfig.livelyHealAmount;

        if (livingEntity.getNoActionTime() >= 100) {
          heal *= ChampionsConfig.livelyPassiveMultiplier;
        }
        livingEntity.heal((float) heal);
      }
    }
  }
}
