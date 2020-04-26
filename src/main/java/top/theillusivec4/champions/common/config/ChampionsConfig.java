package top.theillusivec4.champions.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.RanksConfig.RankConfig;

public class ChampionsConfig {

  private static final String CONFIG_PREFIX = "gui." + Champions.MODID + ".config.";

  public static final ForgeConfigSpec SERVER_SPEC;
  public static final Server SERVER;

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class Server {

    public final DoubleValue healthGrowth;
    public final DoubleValue attackGrowth;
    public final DoubleValue armorGrowth;
    public final DoubleValue toughnessGrowth;
    public final DoubleValue knockbackResistanceGrowth;
    public final IntValue experienceGrowth;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("growth");

      healthGrowth = builder
          .comment("The percent increase in health multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "healthGrowth")
          .defineInRange("healthGrowth", 0.35D, 0.0D, Double.MAX_VALUE);

      attackGrowth = builder
          .comment("The percent increase in attack damage multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "attackGrowth")
          .defineInRange("attackGrowth", 0.5D, 0.0D, Double.MAX_VALUE);

      armorGrowth = builder.comment("The increase in armor multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "armorGrowth")
          .defineInRange("armorGrowth", 2.0D, 0.0D, 30.0D);

      toughnessGrowth = builder
          .comment("The increase in armor toughness multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "toughnessGrowth")
          .defineInRange("toughnessGrowth", 1.0D, 0.0D, 30.0D);

      knockbackResistanceGrowth = builder
          .comment("The increase in knockback resistance multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "knockbackResistanceGrowth")
          .defineInRange("knockbackResistanceGrowth", 0.05D, 0.0D, 1.0D);

      experienceGrowth = builder
          .comment("The increase in experience multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "experienceGrowth")
          .defineInRange("experienceGrowth", 1, 0, Integer.MAX_VALUE);
    }
  }

  public static final ForgeConfigSpec RANKS_SPEC;
  public static final Ranks RANKS;

  static {
    final Pair<Ranks, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Ranks::new);
    RANKS_SPEC = specPair.getRight();
    RANKS = specPair.getLeft();
  }

  public static class Ranks {

    public RanksConfig ranks;

    public Ranks(ForgeConfigSpec.Builder builder) {
      builder.comment("List of ranks").define("ranks", new ArrayList<>());
      builder.build();
    }
  }

  public static List<RankConfig> ranks;

  public static void transform(CommentedConfig configData) {
    RANKS.ranks = new ObjectConverter().toObject(configData, RanksConfig::new);
    ranks = RANKS.ranks.ranks;
  }

  public static double healthGrowth;
  public static double attackGrowth;
  public static double armorGrowth;
  public static double toughnessGrowth;
  public static double knockbackResistanceGrowth;
  public static int experienceGrowth;

  public static void bake() {
    healthGrowth = SERVER.healthGrowth.get();
    attackGrowth = SERVER.attackGrowth.get();
    armorGrowth = SERVER.armorGrowth.get();
    toughnessGrowth = SERVER.toughnessGrowth.get();
    knockbackResistanceGrowth = SERVER.knockbackResistanceGrowth.get();
    experienceGrowth = SERVER.experienceGrowth.get();
  }
}

