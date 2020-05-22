package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class WoundingAffix extends BasicAffix {

  public WoundingAffix() {
    super("wounding", AffixCategory.OFFENSE, true);
  }

  @SubscribeEvent
  public void onHeal(LivingHealEvent evt) {

    if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.WOUND)) {
      evt.setAmount(evt.getAmount() * 0.5F);
    }
  }

  @SubscribeEvent
  public void onDamage(LivingDamageEvent evt) {

    if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.WOUND)) {
      evt.setAmount(evt.getAmount() * 1.5F);
    }
  }

  @Override
  public boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
      float amount) {

    if (target.getRNG().nextFloat() < ChampionsConfig.woundingChance) {
      target.addPotionEffect(new EffectInstance(ChampionsRegistry.WOUND, 200, 0));
    }
    return true;
  }
}
