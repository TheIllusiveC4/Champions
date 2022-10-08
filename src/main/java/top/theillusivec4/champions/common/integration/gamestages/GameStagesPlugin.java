package top.theillusivec4.champions.common.integration.gamestages;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class GameStagesPlugin {

  private static final Map<String, StageInfo> ENTITY_STAGE_INFO = new HashMap<>();
  private static final Map<Integer, StageInfo> TIER_STAGE_INFO = new HashMap<>();

  public static void buildStages() {
    ENTITY_STAGE_INFO.clear();
    TIER_STAGE_INFO.clear();
    List<? extends String> entityStages = ChampionsConfig.entityStages;

    for (String entityStage : entityStages) {
      String[] split = entityStage.split(";");

      if (split.length < 2 || split.length > 3) {
        Champions.LOGGER.error("Malformed entity stage {}, skipping...", entityStage);
      }
      String stage = split[0];
      String entity = split[1];

      if (split.length == 2) {
        addEntityStage(entity, stage);
      } else {
        addEntityStage(entity, stage, split[2]);
      }
    }
    List<? extends String> tierStages = ChampionsConfig.tierStages;

    for (String tierStage : tierStages) {
      String[] split = tierStage.split(";");

      if (split.length < 2 || split.length > 3) {
        Champions.LOGGER.error("Malformed tier stage {}, skipping...", tierStage);
      }
      String stage = split[0];
      int tier = 0;

      try {
        tier = Integer.parseInt(split[1]);
      } catch (NumberFormatException e) {
        Champions.LOGGER.error("{} is not a valid tier, needs to be an integer", split[1]);
      }

      if (tier > 0) {

        if (split.length == 2) {
          addTierStage(tier, stage);
        } else {
          addTierStage(tier, stage, split[2]);
        }
      }
    }
  }

  public static void addEntityStage(String entity, String stage) {
    ENTITY_STAGE_INFO.merge(entity, new StageInfo(stage), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  public static void addEntityStage(String entity, String stage, String dimension) {
    ENTITY_STAGE_INFO.merge(entity, new StageInfo(stage, dimension), (k, v) -> {
      v.addStage(stage, dimension);
      return v;
    });
  }

  public static void addTierStage(int tier, String stage) {
    TIER_STAGE_INFO.merge(tier, new StageInfo(stage), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  public static void addTierStage(int tier, String stage, String dimension) {
    TIER_STAGE_INFO.merge(tier, new StageInfo(stage, dimension), (k, v) -> {
      v.addStage(stage);
      return v;
    });
  }

  private static Optional<StageInfo> getStageInfo(String entity) {
    return Optional.ofNullable(ENTITY_STAGE_INFO.get(entity));
  }

  private static Optional<StageInfo> getStageInfo(int tier) {
    return Optional.ofNullable(TIER_STAGE_INFO.get(tier));
  }

  public static boolean hasEntityStage(LivingEntity living) {
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

  private static boolean hasRequiredStages(@Nonnull StageInfo info, @Nonnull LivingEntity living) {
    String dimension = living.getLevel().dimension().location().toString();
    Set<String> stages;

    if (info.dimensionalStages.containsKey(dimension)) {
      stages = info.dimensionalStages.get(dimension);
    } else {
      stages = info.globalStages;
    }

    if (stages.isEmpty()) {
      return true;
    } else if (living.getLevel() instanceof ServerLevel serverLevel) {
      return !serverLevel.getPlayers(
          player -> GameStageHelper.hasAllOf(player, stages) && player.distanceTo(living) <= 256)
        .isEmpty();
    }
    return false;
  }

  public static class StageInfo {

    Map<String, Set<String>> dimensionalStages = new HashMap<>();
    Set<String> globalStages = new HashSet<>();

    StageInfo(String stage) {
      addStage(stage);
    }

    StageInfo(String stage, String dimension) {
      addStage(stage, dimension);
    }

    void addStage(String stage) {
      this.globalStages.add(stage);
    }

    void addStage(String stage, String dimension) {
      this.dimensionalStages
        .merge(dimension, new HashSet<>(Collections.singleton(stage)), (k, v) -> {
          v.add(stage);
          return v;
        });
    }
  }
}
