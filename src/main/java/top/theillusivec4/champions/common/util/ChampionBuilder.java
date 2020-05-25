package top.theillusivec4.champions.common.util;

import com.google.common.collect.ImmutableSortedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;

public class ChampionBuilder {

  private static final Random RAND = new Random();

  public static void spawn(final IChampion champion) {
    LivingEntity entity = champion.getLivingEntity();
    Rank newRank = ChampionBuilder.createRank(entity);
    champion.getServer().setRank(newRank);
    ChampionBuilder.applyGrowth(entity, newRank.getGrowthFactor());
    List<IAffix> newAffixes = ChampionBuilder.createAffixes(newRank, champion);
    champion.getServer().setAffixes(newAffixes);
    newAffixes.forEach(affix -> affix.onInitialSpawn(champion));
  }

  public static void spawnPreset(final IChampion champion, int tier, List<IAffix> affixes) {
    LivingEntity entity = champion.getLivingEntity();
    Rank newRank = RankManager.getRank(tier);
    champion.getServer().setRank(newRank);
    ChampionBuilder.applyGrowth(entity, newRank.getGrowthFactor());
    affixes = affixes.isEmpty() ? ChampionBuilder.createAffixes(newRank, champion) : affixes;
    champion.getServer().setAffixes(affixes);
    affixes.forEach(affix -> affix.onInitialSpawn(champion));
  }

  public static List<IAffix> createAffixes(final Rank rank, final IChampion champion) {
    int size = rank.getNumAffixes();
    int tier = rank.getTier();
    List<IAffix> affixesToAdd = new ArrayList<>();
    Map<AffixCategory, List<IAffix>> allAffixes = Champions.API.getCategoryMap();
    Map<AffixCategory, List<IAffix>> validAffixes = new HashMap<>();

    for (AffixCategory category : Champions.API.getCategories()) {
      validAffixes.put(category, new ArrayList<>());
    }
    allAffixes.forEach((k, v) -> validAffixes.get(k).addAll(
        v.stream().filter(affix -> affix.getTier() <= tier && affix.canApply(champion))
            .collect(Collectors.toList())));
    double chance = 0.33D;

    if (affixesToAdd.size() < size) {
      List<IAffix> cc = validAffixes.get(AffixCategory.CC);

      if (!cc.isEmpty() && RAND.nextDouble() < chance) {
        Collections.shuffle(cc);
        affixesToAdd.add(cc.get(0));
      }
    }

    if (affixesToAdd.size() < size) {
      List<IAffix> defense = validAffixes.get(AffixCategory.DEFENSE);

      if (!defense.isEmpty() && RAND.nextDouble() < chance) {
        Collections.shuffle(defense);
        affixesToAdd.add(defense.get(0));
      }
    }

    if (affixesToAdd.size() < size) {
      List<IAffix> offense = validAffixes.get(AffixCategory.OFFENSE);
      Collections.shuffle(offense);
      int index = 0;

      while (index < offense.size() && affixesToAdd.size() < size) {
        affixesToAdd.add(offense.get(index));
        index++;
      }
    }
    return affixesToAdd;
  }

  public static Rank createRank(final LivingEntity livingEntity) {

    if (!ChampionHelper.checkPotential(livingEntity)) {
      return RankManager.getEmptyRank();
    }

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

    if (growthFactor <= 1) {
      return;
    }
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
