package top.theillusivec4.champions.common.affix.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public abstract class BasicAffix implements IAffix {

  private final String id;
  private final AffixCategory category;

  public BasicAffix(String id, AffixCategory category) {
    this(id, category, false);
  }

  public BasicAffix(String id, AffixCategory category, boolean hasSubscriptions) {
    this.id = id;
    this.category = category;

    if (hasSubscriptions) {
      MinecraftForge.EVENT_BUS.register(this);
    }
  }

  @Override
  public String getIdentifier() {
    return this.id;
  }

  @Override
  public AffixCategory getCategory() {
    return this.category;
  }

  @SuppressWarnings("ConstantConditions")
  public static boolean canTarget(LivingEntity livingEntity, LivingEntity target,
      boolean sightCheck) {

    if (target == null || !target.isAlive() || target instanceof ArmorStandEntity || (sightCheck
        && !livingEntity.canEntityBeSeen(target))) {
      return false;
    }
    IAttributeInstance attributeInstance = livingEntity
        .getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
    double range = attributeInstance == null ? 16.0D : attributeInstance.getValue();
    range = ChampionsConfig.affixTargetRange == 0 ? range
        : Math.min(range, ChampionsConfig.affixTargetRange);
    return livingEntity.getDistance(target) <= range;
  }
}
