package top.theillusivec4.champions.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface IAffix {

  String getIdentifier();

  AffixCategory getCategory();

  default void onInitialSpawn(LivingEntity livingEntity) {

  }

  default void onSpawn(LivingEntity livingEntity) {

  }

  default void onUpdate(LivingEntity livingEntity) {

  }

  default boolean onAttack(LivingEntity livingEntity, LivingEntity target, DamageSource source,
      float amount) {
    return true;
  }

  default boolean onAttacked(LivingEntity livingEntity, DamageSource source, float amount) {
    return true;
  }

  default float onHurt(LivingEntity livingEntity, DamageSource source, float amount,
      float newAmount) {
    return newAmount;
  }

  default float onHealed(LivingEntity livingEntity, float amount, float newAmount) {
    return newAmount;
  }

  default float onDamaged(LivingEntity livingEntity, DamageSource source, float amount,
      float newAmount) {
    return newAmount;
  }

  default boolean onDeath(LivingEntity livingEntity, DamageSource source) {
    return true;
  }

  default float onKnockback(LivingEntity livingEntity) {
    return 1.0F;
  }

  default boolean canApply(LivingEntity livingEntity) {
    return true;
  }

  default boolean isCompatible(IAffix affix) {
    return affix != this;
  }

  default int getTier() {
    return 1;
  }
}
