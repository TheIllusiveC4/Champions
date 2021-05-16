package top.theillusivec4.champions.common.integration.scalinghealth;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.entity.LivingEntity;
import net.silentchaos512.scalinghealth.resources.mechanics.SHMechanicListener;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ScalingHealthManager {

  private static final Map<Integer, Double> MODIFIERS = new TreeMap<>();

  public static double getSpawnIncrease(int tier, LivingEntity livingEntity) {
    double difficulty = SHMechanicListener.getDifficultyMechanics().mode
        .getDifficulty(livingEntity.world, livingEntity.getPosition());
    return getSpawnModifier(tier) * difficulty;
  }

  public static double getSpawnModifier(int tier) {
    return MODIFIERS.getOrDefault(tier, 0.0D);
  }

  public static void buildModifiers() {
    MODIFIERS.clear();

    for (String s : ChampionsConfig.scalingHealthSpawnModifiers) {
      String[] parsed = s.split(";");

      if (parsed.length > 1) {
        int tier = Integer.parseInt(parsed[0]);
        double modifier = Double.parseDouble(parsed[1]);

        if (tier > 0 && modifier > 0) {
          MODIFIERS.put(tier, modifier);
        }
      }
    }
  }
}
