package top.theillusivec4.champions.common.util;

import com.google.common.collect.ImmutableSortedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import top.theillusivec4.champions.common.affix.core.AffixManager;
import top.theillusivec4.champions.common.affix.core.AffixManager.AffixSettings;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.util.EntityManager.EntitySettings;

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
    List<IAffix> affixesToAdd = new ArrayList<>();
    Optional<EntitySettings> entitySettings = EntityManager
        .getSettings(champion.getLivingEntity().getType());

    if (size > 0) {
      entitySettings.ifPresent(settings -> {

        if (settings.presetAffixes != null) {
          affixesToAdd.addAll(settings.presetAffixes);
        }
      });
      rank.getPresetAffixes().forEach(affix -> {

        if (!affixesToAdd.contains(affix)) {
          affixesToAdd.add(affix);
        }
      });
    }
    Map<AffixCategory, List<IAffix>> allAffixes = Champions.API.getCategoryMap();
    Map<AffixCategory, List<IAffix>> validAffixes = new HashMap<>();

    for (AffixCategory category : Champions.API.getCategories()) {
      validAffixes.put(category, new ArrayList<>());
    }
    allAffixes.forEach((k, v) -> validAffixes.get(k).addAll(v.stream().filter(affix -> {
      Optional<AffixSettings> settings = AffixManager.getSettings(affix.getIdentifier());
      return !affixesToAdd.contains(affix) && entitySettings
          .map(entitySettings1 -> entitySettings1.canApply(affix)).orElse(true) && settings
          .map(affixSettings -> affixSettings.canApply(champion)).orElse(true) && affix
          .canApply(champion);
    }).collect(Collectors.toList())));
    List<IAffix> randomList = new ArrayList<>();
    validAffixes.forEach((k, v) -> randomList.addAll(v));

    while (randomList.size() > 0 && affixesToAdd.size() < size) {
      int randomIndex = RAND.nextInt(randomList.size());
      IAffix randomAffix = randomList.get(randomIndex);

      if (affixesToAdd.stream().allMatch(affix -> affix.isCompatible(randomAffix) && (
          randomAffix.getCategory() == AffixCategory.OFFENSE || (affix.getCategory() != randomAffix
              .getCategory())))) {
        affixesToAdd.add(randomAffix);
      }
      randomList.remove(randomIndex);
    }
    return affixesToAdd;
  }

  public static Rank createRank(final LivingEntity livingEntity) {

    if (!ChampionHelper.checkPotential(livingEntity)) {
      return RankManager.getLowestRank();
    }
    Integer[] tierRange = new Integer[]{null, null};
    EntityManager.getSettings(livingEntity.getType()).ifPresent(entitySettings -> {
      tierRange[0] = entitySettings.minTier;
      tierRange[1] = entitySettings.maxTier;
    });
    ImmutableSortedMap<Integer, Rank> ranks = RankManager.getRanks();
    Integer firstTier = tierRange[0] != null ? tierRange[0] : ranks.firstKey();
    Iterator<Integer> iter = ranks.navigableKeySet().tailSet(firstTier, false).iterator();
    Rank result = ranks.get(firstTier);

    while (iter.hasNext()) {
      Rank rank = ranks.get(iter.next());

      if (RAND.nextFloat() < rank.getChance()) {
        result = rank;

        if (tierRange[1] != null && rank.getTier() >= tierRange[1]) {
          return result;
        }
      } else {
        return result;
      }
    }
    return result != null ? result : RankManager.getLowestRank();
  }

  public static void applyGrowth(final LivingEntity livingEntity, int growthFactor) {

    if (growthFactor < 1) {
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
