package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
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
    LivingEntity livingEntity = champion.getLivingEntity();
    target.applyKnockback(1.0F * (float) ChampionsConfig.knockingMultiplier,
        MathHelper.sin(livingEntity.rotationYaw * ((float) Math.PI / 180F)),
        (-MathHelper.cos(livingEntity.rotationYaw * ((float) Math.PI / 180F))));
    return true;
  }
}
