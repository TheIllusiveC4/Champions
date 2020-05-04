package top.theillusivec4.champions.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IChampionsApi {

  void registerAffix(IAffix affix);

  void registerAffixes(IAffix... affixes);

  Optional<IAffix> getAffix(String id);

  List<IAffix> getAffixes();

  List<IAffix> getCategory(AffixCategory category);

  AffixCategory[] getCategories();

  Map<AffixCategory, List<IAffix>> getCategoryMap();
}
