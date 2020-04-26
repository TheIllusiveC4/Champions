package top.theillusivec4.champions.common.config;

import java.util.List;

public class RanksConfig {

  public List<RankConfig> ranks;

  public static class RankConfig {
    public Integer tier;
    public Integer numAffixes;
    public Integer growthFactor;
    public Integer defaultColor;
    public Double chance;
    public List<String> effects;
  }
}
