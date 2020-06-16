package top.theillusivec4.champions.common.integration.gamestages;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class ChampionsStages {

  private static final Map<String, Info> ENTITY_STAGE_INFO = new HashMap<>();
  private static final Map<Integer, Info> TIER_STAGE_INFO = new HashMap<>();

  public static void addStage(String entity, String stage) {
    ENTITY_STAGE_INFO.merge(entity, new Info(stage), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  public static void addStage(String entity, String stage, int dimension) {
    ENTITY_STAGE_INFO.merge(entity, new Info(stage, dimension), (k, v) -> {
      v.addStage(stage, dimension);
      return v;
    });
  }

  public static void addTierStage(int tier, String stage) {
    TIER_STAGE_INFO.merge(tier, new Info(stage), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  public static void addTierStage(int tier, String stage, int dimension) {
    TIER_STAGE_INFO.merge(tier, new Info(stage, dimension), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  private static Optional<Info> getStageInfo(String entity) {
    return Optional.ofNullable(ENTITY_STAGE_INFO.get(entity));
  }

  private static Optional<Info> getStageInfo(int tier) {
    return Optional.ofNullable(TIER_STAGE_INFO.get(tier));
  }

  public static boolean hasChampionStage(LivingEntity living) {
    final ResourceLocation rl = living.getType().getRegistryName();

    if (rl != null) {
      final String id = rl.toString();
      return getStageInfo(id).map(info -> hasRequiredStages(info, living)).orElse(true);
    }
    return true;
  }

  public static boolean hasTierStage(int tier, LivingEntity livingEntity) {
    return getStageInfo(tier).map(info -> hasRequiredStages(info, livingEntity)).orElse(true);
  }

  private static boolean hasRequiredStages(@Nonnull Info info, @Nonnull LivingEntity living) {
    int dimension = living.dimension.getId();
    Set<String> stages;

    if (info.dimensionalStages.containsKey(dimension)) {
      stages = info.dimensionalStages.get(dimension);
    } else {
      stages = info.globalStages;
    }

    if (stages.isEmpty()) {
      return true;
    } else if (living.world instanceof ServerWorld) {
      ServerWorld serverWorld = (ServerWorld) living.world;

      for (final ServerPlayerEntity player : serverWorld.getPlayers()) {

        if (GameStageHelper.hasAllOf(player, stages) && player.getDistance(living) <= 256) {
          return true;
        }
      }
    }
    return false;
  }

  public static class Info {

    Map<Integer, Set<String>> dimensionalStages = new HashMap<>();
    Set<String> globalStages = new HashSet<>();

    Info(String stage) {
      addStage(stage);
    }

    Info(String stage, int dimension) {
      addStage(stage, dimension);
    }

    void addStage(String stage) {
      this.globalStages.add(stage);
    }

    void addStage(String stage, int dimension) {
      this.dimensionalStages
          .merge(dimension, new HashSet<>(Collections.singleton(stage)), (k, v) -> {
            v.add(stage);
            return v;
          });
    }
  }
}
