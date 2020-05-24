package top.theillusivec4.champions.common.affix.core;

import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.affix.AdaptableAffix;
import top.theillusivec4.champions.common.affix.ArcticAffix;
import top.theillusivec4.champions.common.affix.DampeningAffix;
import top.theillusivec4.champions.common.affix.DesecratingAffix;
import top.theillusivec4.champions.common.affix.EnkindlingAffix;
import top.theillusivec4.champions.common.affix.HastyAffix;
import top.theillusivec4.champions.common.affix.InfestedAffix;
import top.theillusivec4.champions.common.affix.KnockingAffix;
import top.theillusivec4.champions.common.affix.LivelyAffix;
import top.theillusivec4.champions.common.affix.MagneticAffix;
import top.theillusivec4.champions.common.affix.MoltenAffix;
import top.theillusivec4.champions.common.affix.ParalyzingAffix;
import top.theillusivec4.champions.common.affix.PlaguedAffix;
import top.theillusivec4.champions.common.affix.ReflectiveAffix;
import top.theillusivec4.champions.common.affix.ShieldingAffix;
import top.theillusivec4.champions.common.affix.WoundingAffix;

public class AffixManager {

  public static void register() {
    Champions.API.registerAffixes(new MoltenAffix(), new HastyAffix(), new ReflectiveAffix(),
        new LivelyAffix(), new MagneticAffix(), new DampeningAffix(), new AdaptableAffix(),
        new KnockingAffix(), new DesecratingAffix(), new PlaguedAffix(), new InfestedAffix(),
        new ParalyzingAffix(), new WoundingAffix(), new ShieldingAffix(), new ArcticAffix(),
        new EnkindlingAffix());
  }
}
