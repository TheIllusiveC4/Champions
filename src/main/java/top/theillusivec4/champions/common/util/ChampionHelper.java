package top.theillusivec4.champions.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.Permission;

public class ChampionHelper {

  public static boolean isValidChampion(final Entity entity) {
    return entity instanceof LivingEntity && entity instanceof Enemy;
  }

  public static boolean checkPotential(final LivingEntity livingEntity) {
    return isValidEntity(livingEntity) &&
        isValidDimension(livingEntity.getLevel().dimension().location()) &&
        !nearActiveBeacon(livingEntity);
  }

  private static boolean isValidEntity(final LivingEntity livingEntity) {
    String entity = livingEntity.getType().toString();
    if (ChampionsConfig.entitiesPermission == Permission.BLACKLIST) {
      return !ChampionsConfig.entitiesList.contains(entity);
    } else {
      return ChampionsConfig.entitiesList.contains(entity);
    }
  }

  private static boolean isValidDimension(final ResourceLocation resourceLocation) {
    String dimension = resourceLocation.toString();

    if (ChampionsConfig.dimensionPermission == Permission.BLACKLIST) {
      return !ChampionsConfig.dimensionList.contains(dimension);
    } else {
      return ChampionsConfig.dimensionList.contains(dimension);
    }
  }

  private static boolean nearActiveBeacon(final LivingEntity livingEntity) {
    int range = ChampionsConfig.beaconProtectionRange;

    if (range <= 0) {
      return false;
    }

    for (TickingBlockEntity te : livingEntity.getLevel().blockEntityTickers) {
      BlockPos pos = te.getPos();

      if (Math.sqrt(livingEntity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())) <= range
          && te instanceof BeaconBlockEntity beacon) {

        if (beacon.levels > 0) {
          return true;
        }
      }
    }
    return false;
  }
}
