package top.theillusivec4.champions.common.config;

import java.util.List;

public class AffixesConfig {

  public List<AffixConfig> affixes;

  public static class AffixConfig {
    public String identifier;
    public Boolean enabled;
    public Integer minTier;
    public Integer maxTier;
    public List<String> mobList;
    public String mobPermission;
  }
}
