package top.theillusivec4.champions.common.rank;

import com.google.common.collect.ImmutableSortedMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.RanksConfig.RankConfig;

public class RankManager {

  private static final TreeMap<Integer, Rank> RANKS = new TreeMap<>();

  private static Rank emptyRank = new Rank();

  public static ImmutableSortedMap<Integer, Rank> getRanks() {
    return ImmutableSortedMap.copyOf(RANKS);
  }

  @Nonnull
  public static Rank getRank(int tier) {
    return RANKS.getOrDefault(tier, getEmptyRank());
  }

  public static Rank getEmptyRank() {
    return emptyRank;
  }

  public static void buildRanks() {
    List<RankConfig> ranks = ChampionsConfig.ranks;

    if (ranks == null) {
      return;
    }
    ranks.forEach(rank -> {
      try {
        Rank newRank = getRankFromConfig(rank);
        RANKS.put(newRank.getTier(), newRank);
      } catch (IllegalArgumentException e) {
        Champions.LOGGER.error("Invalid attribute found while building rank, skipping...");
      }
    });
  }

  private static Rank getRankFromConfig(RankConfig rank) throws IllegalArgumentException {
    if (rank.tier == null || rank.numAffixes == null || rank.chance == null
        || rank.defaultColor == null || rank.growthFactor == null || rank.effects == null) {
      throw new IllegalArgumentException("Missing rank attribute");
    }
    int tier;

    if (rank.tier < 0) {
      throw new IllegalArgumentException("Negative tier");
    } else {
      tier = rank.tier;
    }
    int numAffixes;

    if (rank.numAffixes < 0) {
      throw new IllegalArgumentException("Negative number of affixes");
    } else {
      numAffixes = rank.numAffixes;
    }
    double chance;

    if (rank.chance <= 0) {
      throw new IllegalArgumentException("Non-positive chance");
    } else {
      chance = rank.chance;
    }
    int defaultColor = rank.defaultColor;

    int growthFactor;

    if (rank.growthFactor < 0) {
      throw new IllegalArgumentException("Negative growth factor");
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
