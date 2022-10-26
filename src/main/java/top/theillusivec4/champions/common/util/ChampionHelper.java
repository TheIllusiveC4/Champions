package top.theillusivec4.champions.common.util;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.Permission;
import top.theillusivec4.champions.common.integration.gamestages.GameStagesPlugin;

public class ChampionHelper {

  private static final Set<BlockPos> BEACON_POS = new HashSet<>();

  private static MinecraftServer server = null;

  public static boolean isValidChampion(final Entity entity) {
    return entity instanceof LivingEntity && entity instanceof Enemy;
  }

  public static boolean checkPotential(final LivingEntity livingEntity) {
    return isValidEntity(livingEntity) &&
      isValidDimension(livingEntity.getLevel().dimension().location()) &&
      !nearActiveBeacon(livingEntity) &&
      (!Champions.gameStagesLoaded || GameStagesPlugin.hasEntityStage(livingEntity));
  }

  public static void addBeacon(BlockPos pos) {

    if (server != null) {
      BEACON_POS.add(pos);
    }
  }

  private static boolean isValidEntity(final LivingEntity livingEntity) {
    ResourceLocation rl = livingEntity.getType().getRegistryName();

    if (rl == null) {
      return false;
    }
    String entity = rl.toString();

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
    Set<BlockPos> toRemove = new HashSet<>();

    for (BlockPos pos : BEACON_POS) {
      Level level = livingEntity.getLevel();

      if (!level.isLoaded(pos)) {
        continue;
      }
      BlockEntity blockEntity = level.getBlockEntity(pos);

      if (blockEntity instanceof BeaconBlockEntity beaconBlockEntity && !blockEntity.isRemoved()) {

        if (Math.sqrt(livingEntity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())) <= range) {

          if (beaconBlockEntity.levels > 0) {
            return true;
          }
        }

      } else {
        toRemove.add(pos);
      }
    }
    BEACON_POS.removeAll(toRemove);
    return false;
  }

  public static void clearBeacons() {
    BEACON_POS.clear();
  }

  public static void setServer(MinecraftServer serverIn) {
    server = serverIn;
  }
}
