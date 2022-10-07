package top.theillusivec4.champions.common.affix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
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
    if (!ChampionsConfig.reflectiveLethal && evt.getSource().getMsgId().equals(REFLECTION_DAMAGE)) {
      LivingEntity living = evt.getEntityLiving();
      float currentDamage = evt.getAmount();

      if (currentDamage >= living.getHealth()) {
        evt.setAmount(living.getHealth() - 1);
      }
    }
  }

  @Override
  public float onDamage(IChampion champion, DamageSource source, float amount, float newAmount) {

    if (source.getDirectEntity() instanceof LivingEntity sourceEntity) {

      if (source.getMsgId().equals(REFLECTION_DAMAGE) ||
        (source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns())) {
        return newAmount;
      }
      EntityDamageSource newSource =
        new EntityDamageSource(REFLECTION_DAMAGE, champion.getLivingEntity());
      newSource.setThorns();
      float min = (float) ChampionsConfig.reflectiveMinPercent;

      if (source.isFire()) {
        newSource.setIsFire();
      }

      if (source.isProjectile()) {
        newSource.setProjectile();
      }

      if (source.isExplosion()) {
        newSource.setExplosion();
      }

      if (source.isMagic()) {
        newSource.setMagic();
      }

      if (source.isDamageHelmet()) {
        newSource.damageHelmet();
      }

      if (source.isBypassArmor()) {
        newSource.bypassArmor();
      }

      if (source.scalesWithDifficulty()) {
        newSource.setScalesWithDifficulty();
      }

      if (source.isBypassInvul()) {
        newSource.bypassInvul();
      }
      float damage = (float) Math.min(
        amount *
          (sourceEntity.getRandom().nextFloat() * (ChampionsConfig.reflectiveMaxPercent - min)
            + min), ChampionsConfig.reflectiveMax);
      sourceEntity.hurt(newSource, damage);
    }
    return newAmount;
  }
}
