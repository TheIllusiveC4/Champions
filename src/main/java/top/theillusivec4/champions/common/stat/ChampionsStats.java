package top.theillusivec4.champions.common.stat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import top.theillusivec4.champions.Champions;

public class ChampionsStats {

  public static ResourceLocation CHAMPION_MOBS_KILLED;

  public static void setup() {
    CHAMPION_MOBS_KILLED = makeCustomStat("champion_mobs_killed", StatFormatter.DEFAULT);
  }

  private static ResourceLocation makeCustomStat(String key, StatFormatter formatter) {
    ResourceLocation resourcelocation = new ResourceLocation(Champions.MODID, key);
    Registry.register(Registry.CUSTOM_STAT, resourcelocation.toString(), resourcelocation);
    Stats.CUSTOM.get(resourcelocation, formatter);
    return resourcelocation;
  }
}
