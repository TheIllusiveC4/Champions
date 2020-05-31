package top.theillusivec4.champions.common.config;

import java.util.List;

public class EntitiesConfig {

  public List<EntityConfig> entities;

  public static class EntityConfig {
    public String entity;
    public Integer minTier;
    public Integer maxTier;
    public List<String> presetAffixes;
    public List<String> affixList;
    public String affixPermission;
  }
}
