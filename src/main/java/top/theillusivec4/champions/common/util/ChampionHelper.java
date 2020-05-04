package top.theillusivec4.champions.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;

public class ChampionHelper {

  public static boolean isValidEntity(final Entity entity) {
    return entity instanceof LivingEntity && entity instanceof IMob;
  }
}
