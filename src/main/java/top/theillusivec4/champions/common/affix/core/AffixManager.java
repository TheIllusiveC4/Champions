package top.theillusivec4.champions.common.affix.core;

import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.affix.HastyAffix;
import top.theillusivec4.champions.common.affix.LivelyAffix;
import top.theillusivec4.champions.common.affix.MagneticAffix;
import top.theillusivec4.champions.common.affix.MoltenAffix;
import top.theillusivec4.champions.common.affix.ReflectiveAffix;

public class AffixManager {

  public static void register() {
    MinecraftForge.EVENT_BUS.register(new AffixEventsHandler());
    Champions.API.registerAffixes(new MoltenAffix(), new HastyAffix(), new ReflectiveAffix(),
        new LivelyAffix(), new MagneticAffix());
  }
}
