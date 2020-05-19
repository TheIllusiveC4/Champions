package top.theillusivec4.champions.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
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

    public final DoubleValue affixTargetRange;

    public final DoubleValue hastyMovementBonus;

    public final DoubleValue livelyHealAmount;
    public final DoubleValue livelyPassiveMultiplier;
    public final IntValue livelyCooldown;

    public final DoubleValue magneticStrength;

    public final BooleanValue moltenWaterResistance;

    public final DoubleValue reflectiveMinPercent;
    public final DoubleValue reflectiveMaxPercent;
    public final IntValue reflectiveMax;
    public final BooleanValue reflectiveLethal;

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

      builder.pop();

      builder.push("affixes");

      affixTargetRange = builder.comment(
          "Set the maximum distance that mobs can use their targeted abilities from, 0 to disable")
          .translation(CONFIG_PREFIX + "affixTargetRange")
          .defineInRange("affixTargetRange", 0.0D, 0.0D, 100.0D);

      builder.push("hasty");

      hastyMovementBonus = builder.comment("The base movement speed bonus")
          .translation(CONFIG_PREFIX + "hastyMovementBonus")
          .defineInRange("hastyMovementBonus", 0.25D, 0.0D, Double.MAX_VALUE);

      builder.pop();

      builder.push("lively");

      livelyHealAmount = builder.comment("The amount of health per second regeneration")
          .translation(CONFIG_PREFIX + "livelyHealAmount")
          .defineInRange("livelyHealAmount", 1.0D, 0.0D, Double.MAX_VALUE);

      livelyPassiveMultiplier = builder
          .comment("Multiplier to health regeneration when not aggressive")
          .translation(CONFIG_PREFIX + "livelyPassiveMultiplier")
          .defineInRange("livelyPassiveMultiplier", 5.0D, 1.0D, Double.MAX_VALUE);

      livelyCooldown = builder
          .comment("Set cooldown (in seconds) for regeneration after getting attacked")
          .translation(CONFIG_PREFIX + "livelyCooldown")
          .defineInRange("livelyCooldown", 3, 1, Integer.MAX_VALUE);

      builder.pop();

      builder.push("molten");

      moltenWaterResistance = builder
          .comment("Set to true to have Molten champions not be damaged by water")
          .translation(CONFIG_PREFIX + "moltenWaterResistance")
          .define("moltenWaterResistance", false);

      builder.pop();

      builder.push("reflective");

      reflectiveMinPercent = builder.comment("The minimum percent of damage to reflect back")
          .translation(CONFIG_PREFIX + "reflectiveMinPercent")
          .defineInRange("reflectiveMinPercent", 0.1D, 0.0D, 1.0D);

      reflectiveMaxPercent = builder.comment("The maximum percent of damage to reflect back")
          .translation(CONFIG_PREFIX + "reflectiveMaxPercent")
          .defineInRange("reflectiveMaxPercent", 0.35D, 0.0D, 1.0D);

      reflectiveMax = builder.comment("The maximum amount of damage to reflect back")
          .translation(CONFIG_PREFIX + "reflectiveMax")
          .defineInRange("reflectiveMax", 100, 0, Integer.MAX_VALUE);

      reflectiveLethal = builder.comment("Set to true to enable deadly reflected strikes")
          .translation(CONFIG_PREFIX + "reflectiveLethal").define("reflectiveLethal", true);

      builder.pop();

      builder.push("magnetic");

      magneticStrength = builder.comment("Strength of the magnetic pulling effect")
          .translation(CONFIG_PREFIX + "magneticStrength")
          .defineInRange("magneticStrength", 0.05D, 0.0D, 100.0D);

      builder.pop();

      builder.pop();
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

  public static double affixTargetRange;

  public static double hastyMovementBonus;

  public static int livelyCooldown;
  public static double livelyHealAmount;
  public static double livelyPassiveMultiplier;

  public static double magneticStrength;

  public static boolean moltenWaterResistance;

  public static double reflectiveMaxPercent;
  public static double reflectiveMinPercent;
  public static int reflectiveMax;
  public static boolean reflectiveLethal;

  public static void bake() {
    healthGrowth = SERVER.healthGrowth.get();
    attackGrowth = SERVER.attackGrowth.get();
    armorGrowth = SERVER.armorGrowth.get();
    toughnessGrowth = SERVER.toughnessGrowth.get();
    knockbackResistanceGrowth = SERVER.knockbackResistanceGrowth.get();
    experienceGrowth = SERVER.experienceGrowth.get();

    affixTargetRange = SERVER.affixTargetRange.get();

    hastyMovementBonus = SERVER.hastyMovementBonus.get();

    livelyHealAmount = SERVER.livelyHealAmount.get();
    livelyPassiveMultiplier = SERVER.livelyPassiveMultiplier.get();
    livelyCooldown = SERVER.livelyCooldown.get();

    moltenWaterResistance = SERVER.moltenWaterResistance.get();

    reflectiveLethal = SERVER.reflectiveLethal.get();
    reflectiveMax = SERVER.reflectiveMax.get();
    reflectiveMaxPercent = SERVER.reflectiveMaxPercent.get();
    reflectiveMinPercent = SERVER.reflectiveMinPercent.get();

    magneticStrength = SERVER.magneticStrength.get();
  }
}

