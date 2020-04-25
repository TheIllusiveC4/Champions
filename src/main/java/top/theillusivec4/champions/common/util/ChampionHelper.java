package top.theillusivec4.champions.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

public class ChampionHelper {

  public static boolean isValidEntity(final Entity entity) {
    return entity instanceof MobEntity;
  }
}
