package top.theillusivec4.champions.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.Permission;
import top.theillusivec4.champions.common.integration.gamestages.ChampionsStages;

public class ChampionHelper {

  public static boolean isValidChampion(final Entity entity) {
    return entity instanceof LivingEntity && entity instanceof IMob;
  }

  public static boolean checkPotential(final LivingEntity livingEntity) {
    return isValidEntity(livingEntity) &&
        isValidDimension(livingEntity.getEntityWorld().getDimensionKey().getLocation()) &&
        (!Champions.gameStagesLoaded || ChampionsStages.hasChampionStage(livingEntity)) &&
        !nearActiveBeacon(livingEntity);
  }

  private static boolean isValidEntity(final LivingEntity livingEntity) {
    String entity = livingEntity.getEntityString();

    if (entity != null) {

      if (ChampionsConfig.entitiesPermission == Permission.BLACKLIST) {
        return !ChampionsConfig.entitiesList.contains(entity);
      } else {
        return ChampionsConfig.entitiesList.contains(entity);
      }
    }
    return true;
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

    for (TileEntity te : livingEntity.getEntityWorld().tickableTileEntities) {
      BlockPos pos = te.getPos();

      if (Math.sqrt(livingEntity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ())) <= range
          && te instanceof BeaconTileEntity) {
        BeaconTileEntity beacon = (BeaconTileEntity) te;

        if (beacon.getLevels() > 0) {
          return true;
        }
      }
    }
    return false;
  }
}
