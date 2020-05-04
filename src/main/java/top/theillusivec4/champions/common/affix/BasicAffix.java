package top.theillusivec4.champions.common.affix;

import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;

public abstract class BasicAffix implements IAffix {

  private final String id;
  private final AffixCategory category;

  public BasicAffix(String id, AffixCategory category) {
    this.id = id;
    this.category = category;
  }

  @Override
  public String getIdentifier() {
    return this.id;
  }

  @Override
  public AffixCategory getCategory() {
    return this.category;
  }
}
