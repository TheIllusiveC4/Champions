package top.theillusivec4.champions.common.affix.core;

import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;

public abstract class BasicAffix implements IAffix {

  private final String id;
  private final AffixCategory category;

  public BasicAffix(String id, AffixCategory category) {
    this(id, category, false);
  }

  public BasicAffix(String id, AffixCategory category, boolean hasSubscriptions) {
    this.id = id;
    this.category = category;

    if (hasSubscriptions) {
      MinecraftForge.EVENT_BUS.register(this);
    }
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
