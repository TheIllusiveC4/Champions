package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ReflectiveAffix extends BasicAffix {

  private static final String REFLECTION_DAMAGE = "reflection";

  public ReflectiveAffix() {
    super("reflective", AffixCategory.OFFENSE, true);
  }

  @SubscribeEvent
  public void onDamageEvent(LivingDamageEvent evt) {

    if (!ChampionsConfig.reflectiveLethal && evt.getSource().damageType.equals(REFLECTION_DAMAGE)) {
      LivingEntity living = evt.getEntityLiving();
      float currentDamage = evt.getAmount();

      if (currentDamage >= living.getHealth()) {
        evt.setAmount(living.getHealth() - 1);
      }
    }
  }

  @Override
  public float onDamage(IChampion champion, DamageSource source, float amount, float newAmount) {

    if (source.getTrueSource() instanceof LivingEntity) {
      LivingEntity sourceEntity = (LivingEntity) source.getTrueSource();

      if (source.damageType.equals(REFLECTION_DAMAGE) || (source instanceof EntityDamageSource
          && ((EntityDamageSource) source).getIsThornsDamage())) {
        return newAmount;
      }
      EntityDamageSource newSource =
          new EntityDamageSource(REFLECTION_DAMAGE, champion.getLivingEntity());
      newSource.setIsThornsDamage();
      float min = (float) ChampionsConfig.reflectiveMinPercent;

      if (source.isFireDamage()) {
        newSource.setFireDamage();
      }

      if (source.isProjectile()) {
        newSource.setProjectile();
      }

      if (source.isExplosion()) {
        newSource.setExplosion();
      }

      if (source.isMagicDamage()) {
        newSource.setMagicDamage();
      }

      if (source.isDamageAbsolute()) {
        newSource.setDamageIsAbsolute();
      }

      if (source.isUnblockable()) {
        newSource.setDamageBypassesArmor();
      }

      if (source.isDifficultyScaled()) {
        newSource.setDifficultyScaled();
      }

      if (source.canHarmInCreative()) {
        newSource.setDamageAllowedInCreativeMode();
      }
      float damage = (float) Math.min(
          amount * (sourceEntity.getRNG().nextFloat() * (ChampionsConfig.reflectiveMaxPercent - min)
              + min), ChampionsConfig.reflectiveMax);
      sourceEntity.attackEntityFrom(newSource, damage);
    }
    return newAmount;
  }
}
