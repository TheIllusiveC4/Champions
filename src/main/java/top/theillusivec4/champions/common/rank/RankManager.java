package top.theillusivec4.champions.common.rank;

import com.google.common.collect.ImmutableSortedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.RanksConfig.RankConfig;

public class RankManager {

  private static final Rank COMMON = new Rank();
  private static final TreeMap<Integer, Rank> RANKS = new TreeMap<>();

  static {
    RANKS.put(0, COMMON);
  }

  public static ImmutableSortedMap<Integer, Rank> getRanks() {
    return ImmutableSortedMap.copyOf(RANKS);
  }

  @Nonnull
  public static Rank getRank(int tier) {
    return RANKS.getOrDefault(tier, getCommonRank());
  }

  public static Rank getCommonRank() {
    return COMMON;
  }

  public static void buildRanks() {
    List<RankConfig> ranks = ChampionsConfig.ranks;

    if (ranks == null) {
      return;
    }
    ranks.forEach(rank -> {
      Rank newRank = getRankFromConfig(rank);
      RANKS.put(newRank.getTier(), newRank);
    });
  }

  private static Rank getRankFromConfig(RankConfig rank) throws IllegalArgumentException {
    if (rank.tier == null || rank.numAffixes == null || rank.chance == null
        || rank.defaultColor == null || rank.growthFactor == null || rank.effects == null) {
      Champions.LOGGER
          .error("Attempted to build rank " + rank + " with missing attribute! Skipping...");
      throw new IllegalArgumentException();
    }
    int tier;

    if (rank.tier <= 0) {
      Champions.LOGGER.error("Found non-positive tier in rank, skipping...");
      throw new IllegalArgumentException();
    } else {
      tier = rank.tier;
    }
    int numAffixes;

    if (rank.numAffixes < 0) {
      Champions.LOGGER.error("Found negative number of affixes in rank, skipping...");
      throw new IllegalArgumentException();
    } else {
      numAffixes = rank.numAffixes;
    }
    double chance;

    if (rank.chance <= 0) {
      Champions.LOGGER.error("Found non-positive chance in rank, skipping...");
      throw new IllegalArgumentException();
    } else {
      chance = rank.chance;
    }
    int defaultColor;

    try {
      defaultColor = Integer.parseInt(rank.defaultColor, 16);
    } catch (NumberFormatException e) {
      Champions.LOGGER.error("Found invalid hex code in rank, skipping...");
      throw new IllegalArgumentException();
    }
    int growthFactor;

    if (rank.growthFactor < 0) {
      Champions.LOGGER.error("Found negative growth factor in rank, skipping...");
      throw new IllegalArgumentException();
    } else {
      growthFactor = rank.growthFactor;
    }
    List<Tuple<Effect, Integer>> effects = new ArrayList<>();

    rank.effects.forEach(effect -> {
      String[] parsed = effect.split(";");
      Effect found = ForgeRegistries.POTIONS.getValue(new ResourceLocation(parsed[0]));

      if (found != null) {
        int amplifier = 0;

        if (parsed.length > 1) {
          try {
            amplifier = Integer.parseInt(parsed[1]);
          } catch (NumberFormatException e) {
            Champions.LOGGER
                .error("Found invalid amplifier value for effect, setting to default 1");
          }
        }
        effects.add(new Tuple<>(found, amplifier));
      }
    });
    return new Rank(tier, numAffixes, growthFactor, (float) chance, defaultColor, effects);
  }
}
