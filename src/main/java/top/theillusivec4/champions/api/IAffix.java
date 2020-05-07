package top.theillusivec4.champions.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface IAffix {

  String getIdentifier();

  AffixCategory getCategory();

  default void onInitialSpawn(IChampion champion) {

  }

  default void onSpawn(IChampion champion) {

  }

  default void onUpdate(IChampion champion) {

  }

  default boolean onAttack(IChampion champion, LivingEntity target, DamageSource source,
      float amount) {
    return true;
  }

  default boolean onAttacked(IChampion champion, DamageSource source, float amount) {
    return true;
  }

  default float onHurt(IChampion champion, DamageSource source, float amount,
      float newAmount) {
    return newAmount;
  }

  default float onHeal(IChampion champion, float amount, float newAmount) {
    return newAmount;
  }

  default float onDamage(IChampion champion, DamageSource source, float amount,
      float newAmount) {
    return newAmount;
  }

  default boolean onDeath(IChampion champion, DamageSource source) {
    return true;
  }

  default float onKnockBack(IChampion champion, float strength, float newStrength) {
    return newStrength;
  }

  default boolean canApply(IChampion champion) {
    return true;
  }

  default boolean isCompatible(IAffix affix) {
    return affix != this;
  }

  default int getTier() {
    return 1;
  }
}
