package top.theillusivec4.champions.common.util;

import com.google.common.collect.ImmutableSortedMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;

public class ChampionBuilder {

  private static final Random RAND = new Random();

  public static Rank createRank(final LivingEntity livingEntity) {
    ImmutableSortedMap<Integer, Rank> ranks = RankManager.getRanks();
    Iterator<Integer> iter = ranks.navigableKeySet().tailSet(ranks.firstKey(), false).iterator();
    Rank result = ranks.firstEntry().getValue();

    while (iter.hasNext()) {
      Rank rank = ranks.get(iter.next());

      if (RAND.nextFloat() < rank.getChance()) {
        result = rank;
      } else {
        return result;
      }
    }
    return result;
  }

  public static void applyGrowth(final LivingEntity livingEntity, int growthFactor) {
    grow(livingEntity, SharedMonsterAttributes.MAX_HEALTH,
        ChampionsConfig.healthGrowth * growthFactor, Operation.MULTIPLY_TOTAL);
    livingEntity.setHealth(livingEntity.getMaxHealth());
    grow(livingEntity, SharedMonsterAttributes.ATTACK_DAMAGE,
        ChampionsConfig.attackGrowth * growthFactor, Operation.MULTIPLY_TOTAL);
    grow(livingEntity, SharedMonsterAttributes.ARMOR, ChampionsConfig.armorGrowth * growthFactor,
        Operation.ADDITION);
    grow(livingEntity, SharedMonsterAttributes.ARMOR_TOUGHNESS,
        ChampionsConfig.toughnessGrowth * growthFactor, Operation.ADDITION);
    grow(livingEntity, SharedMonsterAttributes.KNOCKBACK_RESISTANCE,
        ChampionsConfig.knockbackResistanceGrowth * growthFactor, Operation.ADDITION);
  }

  @SuppressWarnings("ConstantConditions")
  private static void grow(final LivingEntity livingEntity, IAttribute attribute, double amount,
      Operation operation) {
    IAttributeInstance attributeInstance = livingEntity.getAttribute(attribute);

    if (attributeInstance != null) {
      double oldMax = attributeInstance.getBaseValue();
      double newMax = 0;

      switch (operation) {
        case ADDITION:
          newMax = oldMax + amount;
          break;
        case MULTIPLY_BASE:
          newMax = oldMax * amount;
          break;
        case MULTIPLY_TOTAL:
          newMax = oldMax * (1 + amount);
          break;
      }
      attributeInstance.setBaseValue(newMax);
    }
  }
}
