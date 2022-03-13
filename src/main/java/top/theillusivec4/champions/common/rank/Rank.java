package top.theillusivec4.champions.common.rank;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import top.theillusivec4.champions.api.IAffix;

public class Rank {

  private final int tier;
  private final int defaultColor;
  private final int numAffixes;
  private final int growthFactor;
  private final float chance;
  private final List<Tuple<MobEffect, Integer>> effects;
  private final List<IAffix> presetAffixes;

  public Rank() {
    this(0, 0, 0, 0, 0, new ArrayList<>(), new ArrayList<>());
  }

  public Rank(int tier, int numAffixes, int growthFactor, float chance, int defaultColor,
              List<Tuple<MobEffect, Integer>> effects, List<IAffix> presetAffixes) {
    this.tier = tier;
    this.numAffixes = numAffixes;
    this.growthFactor = growthFactor;
    this.chance = chance;
    this.defaultColor = defaultColor;
    this.effects = effects;
    this.presetAffixes = presetAffixes;
  }

  public int getTier() {
    return tier;
  }

  public int getDefaultColor() {
    return defaultColor;
  }

  public int getNumAffixes() {
    return numAffixes;
  }

  public int getGrowthFactor() {
    return growthFactor;
  }

  public float getChance() {
    return chance;
  }

  public List<Tuple<MobEffect, Integer>> getEffects() {
    return effects;
  }

  public List<IAffix> getPresetAffixes() {
    return presetAffixes;
  }
}
