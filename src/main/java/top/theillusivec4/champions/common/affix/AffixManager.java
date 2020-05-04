package top.theillusivec4.champions.common.affix;

import top.theillusivec4.champions.Champions;

public class AffixManager {

  public static void register() {
    Champions.API.registerAffixes(new MoltenAffix());
  }
}
