package top.theillusivec4.champions.common.affix;

import java.lang.reflect.Field;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ReflectiveAffix extends BasicAffix {

  private static final Field DAMAGE_TYPE = ObfuscationReflectionHelper
      .findField(DamageSource.class, "field_76373_n");

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

      if (source.damageType.equals(REFLECTION_DAMAGE) || (
          source instanceof IndirectEntityDamageSource && ((IndirectEntityDamageSource) source)
              .getIsThornsDamage())) {
        return newAmount;
      }
      float min = (float) ChampionsConfig.reflectiveMinPercent;

      try {
        DAMAGE_TYPE.set(source, REFLECTION_DAMAGE);
      } catch (IllegalAccessException e) {
        Champions.LOGGER.error("Error trying to reset reflected damage type!");
      }

      if (source instanceof EntityDamageSource) {
        ((EntityDamageSource) source).setIsThornsDamage();
      }
      float damage = (float) Math.min(
          amount * (sourceEntity.getRNG().nextFloat() * (ChampionsConfig.reflectiveMaxPercent - min)
              + min), ChampionsConfig.reflectiveMax);
      sourceEntity.attackEntityFrom(source, damage);
    }
    return newAmount;
  }
}
