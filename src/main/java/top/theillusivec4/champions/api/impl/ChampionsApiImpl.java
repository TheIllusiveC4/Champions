package top.theillusivec4.champions.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampionsApi;

public class ChampionsApiImpl implements IChampionsApi {

  private static final ConcurrentHashMap<String, IAffix> affixes = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<AffixCategory, List<IAffix>> categories =
      new ConcurrentHashMap<>();

  private static ChampionsApiImpl instance = null;

  private static final Logger LOGGER = LogManager.getLogger();

  public static IChampionsApi getInstance() {
    if (instance == null) {
      instance = new ChampionsApiImpl();
      affixes.clear();
      categories.clear();

      for (AffixCategory value : AffixCategory.values()) {
        categories.put(value, new ArrayList<>());
      }
    }
    return instance;
  }

  private ChampionsApiImpl() {
  }

  @Override
  public void registerAffix(IAffix affix) {
    String id = affix.getIdentifier();

    if (affixes.containsKey(id)) {
      LOGGER.error("Skipping affix with duplicate identifier " + id);
      return;
    }
    affixes.put(id, affix);
    categories.get(affix.getCategory()).add(affix);
  }

  @Override
  public void registerAffixes(IAffix... affixes) {

    for (IAffix affix : affixes) {
      this.registerAffix(affix);
    }
  }

  @Override
  public Optional<IAffix> getAffix(String id) {
    return Optional.ofNullable(affixes.get(id));
  }

  @Override
  public List<IAffix> getAffixes() {
    return List.copyOf(affixes.values());
  }

  @Override
  public List<IAffix> getCategory(AffixCategory category) {
    return Collections.unmodifiableList(categories.get(category));
  }

  @Override
  public AffixCategory[] getCategories() {
    return AffixCategory.values();
  }

  @Override
  public Map<AffixCategory, List<IAffix>> getCategoryMap() {
    Map<AffixCategory, List<IAffix>> copy = new HashMap<>();
    categories.forEach((k, v) -> copy.put(k, Collections.unmodifiableList(v)));
    return Collections.unmodifiableMap(copy);
  }
}
