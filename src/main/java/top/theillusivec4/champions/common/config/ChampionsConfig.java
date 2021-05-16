package top.theillusivec4.champions.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.config.AffixesConfig.AffixConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.LootSource;
import top.theillusivec4.champions.common.config.ConfigEnums.Permission;
import top.theillusivec4.champions.common.config.EntitiesConfig.EntityConfig;
import top.theillusivec4.champions.common.config.RanksConfig.RankConfig;
import top.theillusivec4.champions.common.integration.scalinghealth.ScalingHealthManager;

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

    public final IntValue beaconProtectionRange;
    public final BooleanValue championSpawners;
    public final IntValue deathMessageTier;
    public final ConfigValue<List<? extends String>> dimensionList;
    public final EnumValue<ConfigEnums.Permission> dimensionPermission;
    public final ConfigValue<List<? extends String>> entitiesList;
    public final EnumValue<ConfigEnums.Permission> entitiesPermission;
    public final BooleanValue showHud;
    public final BooleanValue showParticles;

    public final BooleanValue fakeLoot;
    public final EnumValue<ConfigEnums.LootSource> lootSource;
    public final ConfigValue<List<? extends String>> lootDrops;
    public final BooleanValue lootScaling;

    public final DoubleValue healthGrowth;
    public final DoubleValue attackGrowth;
    public final DoubleValue armorGrowth;
    public final DoubleValue toughnessGrowth;
    public final DoubleValue knockbackResistanceGrowth;
    public final IntValue experienceGrowth;
    public final IntValue explosionGrowth;

    public final DoubleValue affixTargetRange;

    public final DoubleValue adaptableDamageReductionIncrement;
    public final DoubleValue adaptableMaxDamageReduction;

    public final IntValue arcticAttackInterval;

    public final DoubleValue dampenedDamageReduction;

    public final IntValue desecratingCloudInterval;
    public final IntValue desecratingCloudActivationTime;
    public final DoubleValue desecratingCloudRadius;
    public final IntValue desecratingCloudDuration;

    public final IntValue enkindlingAttackInterval;

    public final DoubleValue hastyMovementBonus;

    public final ConfigValue<String> infestedParasite;
    public final ConfigValue<String> infestedEnderParasite;
    public final IntValue infestedAmount;
    public final IntValue infestedInterval;
    public final DoubleValue infestedPerHealth;
    public final IntValue infestedTotal;

    public final DoubleValue paralyzingChance;

    public final DoubleValue knockingMultiplier;

    public final DoubleValue livelyHealAmount;
    public final DoubleValue livelyPassiveMultiplier;
    public final IntValue livelyCooldown;

    public final DoubleValue magneticStrength;

    public final BooleanValue moltenWaterResistance;

    public final ConfigValue<String> plaguedEffect;
    public final IntValue plaguedRange;

    public final DoubleValue reflectiveMinPercent;
    public final DoubleValue reflectiveMaxPercent;
    public final IntValue reflectiveMax;
    public final BooleanValue reflectiveLethal;

    public final DoubleValue woundingChance;

    public final ConfigValue<List<? extends String>> scalingHealthSpawnModifiers;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("general");

      beaconProtectionRange = builder
          .comment("The range from an active beacon where no champions will spawn (0 to disable)")
          .translation(CONFIG_PREFIX + "beaconProtectionRange")
          .defineInRange("beaconProtectionRange", 64, 0, 1000);

      championSpawners = builder.comment("Set to true to enable champions from mob spawners")
          .translation(CONFIG_PREFIX + "championSpawners").define("championSpawners", false);

      deathMessageTier = builder.comment(
          "The minimum tier of champions that will have death messages sent out upon defeat (0 to disable)")
          .translation(CONFIG_PREFIX + "deathMessageTier")
          .defineInRange("deathMessageTier", 0, 0, Integer.MAX_VALUE);

      dimensionList = builder
          .comment("A list of dimension names that are blacklisted/whitelisted for champions")
          .translation(CONFIG_PREFIX + "dimensionList")
          .defineList("dimensionList", new ArrayList<>(), s -> s instanceof String);

      dimensionPermission = builder
          .comment("Set whether the dimension list is a blacklist or whitelist")
          .translation(CONFIG_PREFIX + "dimensionPermission")
          .defineEnum("dimensionPermission", Permission.BLACKLIST);

      entitiesList = builder
          .comment("A list of entities that are blacklisted/whitelisted for champions")
          .translation(CONFIG_PREFIX + "entitiesList")
          .defineListAllowEmpty(Lists.newArrayList("entitiesList"), ArrayList::new,
              s -> s instanceof String);

      entitiesPermission = builder
          .comment("Set whether the entities list is a blacklist or whitelist")
          .translation(CONFIG_PREFIX + "entitiesPermission")
          .defineEnum("entitiesPermission", Permission.BLACKLIST);

      showHud = builder.comment(
          "Set to true to show HUD display for champions, including health, affixes, and tier")
          .translation(CONFIG_PREFIX + "showHud").define("showHud", true);

      showParticles = builder.comment(
          "Set to true to have champions generate a colored particle effect indicating their rank")
          .translation(CONFIG_PREFIX + "showParticles").define("showParticles", true);

      builder.pop();

      builder.push("loot");

      fakeLoot = builder.comment("Set to true to allow fake players to generate champion loot")
          .translation(CONFIG_PREFIX + "fakeLoot").define("fakeLoot", false);

      lootSource = builder.comment("Set the source of champion mob drops")
          .translation(CONFIG_PREFIX + "lootSource")
          .defineEnum("lootSource", LootSource.LOOT_TABLE);

      lootDrops = builder.comment(
          "List of loot drops from champions if sourced from config\nFormat: [tier];[modid:name];[amount];[enchant(true/false)];[weight]")
          .translation(CONFIG_PREFIX + "lootDrops")
          .defineList("lootDrops", new ArrayList<>(), s -> s instanceof String);

      lootScaling = builder.comment(
          "Set to true to scale amount of loot drops from champions to tier if sourced from config")
          .translation(CONFIG_PREFIX + "lootScaling").define("lootScaling", false);

      builder.pop();

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

      explosionGrowth = builder
          .comment("The increase in explosive range multiplied by the growth factor")
          .translation(CONFIG_PREFIX + "explosionGrowth")
          .defineInRange("explosionGrowth", 2, 0, 100);

      builder.pop();

      builder.push("affixes");

      affixTargetRange = builder.comment(
          "Set the maximum distance that mobs can use their targeted abilities from, 0 to disable")
          .translation(CONFIG_PREFIX + "affixTargetRange")
          .defineInRange("affixTargetRange", 0.0D, 0.0D, 100.0D);

      builder.push("adaptable");

      adaptableDamageReductionIncrement = builder.comment(
          "The increase in damage reduction for each consecutive attack of the same damage type")
          .translation(CONFIG_PREFIX + "adaptableDamageReductionIncrement")
          .defineInRange("adaptableDamageReductionIncrement", 0.15D, 0.0D, 1.0D);

      adaptableMaxDamageReduction = builder.comment("The maximum damage reduction")
          .translation(CONFIG_PREFIX + "adaptableMaxDamageReduction")
          .defineInRange("adaptableMaxDamageReduction", 0.9D, 0.0D, 1.0D);

      builder.pop();

      builder.push("arctic");

      arcticAttackInterval = builder
          .comment("How often the champion will shoot projectiles (in seconds)")
          .translation(CONFIG_PREFIX + "arcticAttackInterval")
          .defineInRange("arcticAttackInterval", 1, 1, 100);

      builder.pop();

      builder.push("dampened");

      dampenedDamageReduction = builder
          .comment("The amount of damage reduction to apply to indirect attacks")
          .translation(CONFIG_PREFIX + "dampenedDamageReduction")
          .defineInRange("dampenedDamageReduction", 0.8D, 0.0D, 1.0D);

      builder.pop();

      builder.push("desecrating");

      desecratingCloudInterval = builder.comment("How long (in seconds) between cloud placements")
          .translation(CONFIG_PREFIX + "desecratingCloudInterval")
          .defineInRange("desecratingCloudInterval", 3, 1, Integer.MAX_VALUE);

      desecratingCloudActivationTime = builder.comment(
          "How long (in seconds) it takes for the effect cloud to activate after being placed")
          .translation(CONFIG_PREFIX + "desecratingCloudActivationTime")
          .defineInRange("desecratingCloudActivationTime", 1, 0, Integer.MAX_VALUE);

      desecratingCloudRadius = builder.comment("The radius of the cloud effect")
          .translation(CONFIG_PREFIX + "desecratingCloudRadius")
          .defineInRange("desecratingCloudRadius", 4.0D, 1.0D, Double.MAX_VALUE);

      desecratingCloudDuration = builder.comment("The duration (in seconds) of the cloud effect")
          .translation(CONFIG_PREFIX + "desecratingCloudDuration")
          .defineInRange("desecratingCloudDuration", 10, 1, Integer.MAX_VALUE);

      builder.pop();

      builder.push("enkindling");

      enkindlingAttackInterval = builder
          .comment("How often the champion will shoot projectiles (in seconds)")
          .translation(CONFIG_PREFIX + "enkindlingAttackInterval")
          .defineInRange("enkindlingAttackInterval", 1, 1, 100);

      builder.pop();

      builder.push("hasty");

      hastyMovementBonus = builder.comment("The base movement speed bonus")
          .translation(CONFIG_PREFIX + "hastyMovementBonus")
          .defineInRange("hastyMovementBonus", 0.25D, 0.0D, Double.MAX_VALUE);

      builder.pop();

      builder.push("infested");

      infestedAmount = builder.comment("The amount of parasites to spawn per interval")
          .translation(CONFIG_PREFIX + "infestedAmount").defineInRange("infestedAmount", 2, 1, 100);

      infestedInterval = builder.comment("The time (in seconds) between parasite spawns")
          .translation(CONFIG_PREFIX + "infestedInterval")
          .defineInRange("infestedInterval", 3, 1, 100);

      infestedPerHealth = builder
          .comment("The amount of parasites to infest per health point of the champion")
          .translation(CONFIG_PREFIX + "infestedPerHealth")
          .defineInRange("infestedPerHealth", 0.5D, 0.0D, Double.MAX_VALUE);

      infestedTotal = builder.comment("The total amount of parasites a champion can house at once")
          .translation(CONFIG_PREFIX + "infestedTotal")
          .defineInRange("infestedTotal", 20, 1, Integer.MAX_VALUE);

      infestedParasite = builder.comment("The mob to use as a parasite for infestation")
          .translation(CONFIG_PREFIX + "infestedParasite")
          .define("infestedParasite", "minecraft:silverfish");

      infestedEnderParasite = builder
          .comment("The mob to use as a parasite for infestation of ender mob")
          .translation(CONFIG_PREFIX + "infestedEnderParasite")
          .define("infestedEnderParasite", "minecraft:endermite");

      builder.pop();

      builder.push("paralyzing");

      paralyzingChance = builder.comment("The percent chance that an attack will paralyze targets")
          .translation(CONFIG_PREFIX + "paralyzingChance")
          .defineInRange("paralyzingChance", 0.2D, 0.0D, 1.0D);

      builder.pop();

      builder.push("knocking");

      knockingMultiplier = builder.comment("The multiplier to apply to the knockback strength")
          .translation(CONFIG_PREFIX + "knockingMultiplier")
          .defineInRange("knockingMultiplier", 5.0D, 0.0D, Double.MAX_VALUE);

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

      builder.push("plagued");

      plaguedEffect = builder
          .comment("The effect that will be spread\nFormat:[effect];[power];[duration(secs)]")
          .translation(CONFIG_PREFIX + "plaguedEffect")
          .define("plaguedEffect", "minecraft:poison;15;1");

      plaguedRange = builder.comment("The range of the plagued effect")
          .translation(CONFIG_PREFIX + "plaguedRange").defineInRange("plaguedRange", 5, 1, 100);

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

      builder.push("wounding");

      woundingChance = builder.comment("The percent chance that an attack will wound targets")
          .translation(CONFIG_PREFIX + "woundingChance")
          .defineInRange("woundingChance", 0.4D, 0.0D, 1.0D);

      builder.pop();

      builder.pop();

      builder.push("integrations");

      scalingHealthSpawnModifiers = builder.comment(
          "Scaling Health\nList of tiers with numbers to multiply spawn rates by difficulty\nFormat: [tier];[percent increase]")
          .translation(CONFIG_PREFIX + "scalingHealthSpawnModifiers")
          .defineList("scalingHealthSpawnModifiers", new ArrayList<>(), s -> s instanceof String);

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

  public static final ForgeConfigSpec AFFIXES_SPEC;
  public static final Affixes AFFIXES;

  static {
    final Pair<Affixes, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Affixes::new);
    AFFIXES_SPEC = specPair.getRight();
    AFFIXES = specPair.getLeft();
  }

  public static class Affixes {

    public AffixesConfig affixes;

    public Affixes(ForgeConfigSpec.Builder builder) {
      builder.comment("List of affix configurations").define("affixes", new ArrayList<>());
      builder.build();
    }
  }

  public static final ForgeConfigSpec ENTITIES_SPEC;
  public static final Entities ENTITIES;

  static {
    final Pair<Entities, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Entities::new);
    ENTITIES_SPEC = specPair.getRight();
    ENTITIES = specPair.getLeft();
  }

  public static class Entities {

    public EntitiesConfig entities;

    public Entities(ForgeConfigSpec.Builder builder) {
      builder.comment("List of entity configurations").define("entities", new ArrayList<>());
      builder.build();
    }
  }

  public static List<RankConfig> ranks;
  public static List<AffixConfig> affixes;
  public static List<EntityConfig> entities;

  public static void transformRanks(CommentedConfig configData) {
    RANKS.ranks = new ObjectConverter().toObject(configData, RanksConfig::new);
    ranks = RANKS.ranks.ranks;
  }

  public static void transformAffixes(CommentedConfig configData) {
    AFFIXES.affixes = new ObjectConverter().toObject(configData, AffixesConfig::new);
    affixes = AFFIXES.affixes.affixes;
  }

  public static void transformEntities(CommentedConfig configData) {
    ENTITIES.entities = new ObjectConverter().toObject(configData, EntitiesConfig::new);
    entities = ENTITIES.entities.entities;
  }

  public static int beaconProtectionRange;
  public static boolean championSpawners;
  public static int deathMessageTier;
  public static List<? extends String> dimensionList;
  public static Permission dimensionPermission;
  public static List<? extends String> entitiesList;
  public static Permission entitiesPermission;
  public static boolean showHud;
  public static boolean showParticles;

  public static boolean fakeLoot;
  public static LootSource lootSource;
  public static boolean lootScaling;

  public static double healthGrowth;
  public static double attackGrowth;
  public static double armorGrowth;
  public static double toughnessGrowth;
  public static double knockbackResistanceGrowth;
  public static int experienceGrowth;
  public static int explosionGrowth;

  public static double affixTargetRange;

  public static double adaptableDamageReductionIncrement;
  public static double adaptableMaxDamageReduction;

  public static int arcticAttackInterval;

  public static double dampenedDamageReduction;

  public static int desecratingCloudInterval;
  public static int desecratingCloudActivationTime;
  public static double desecratingCloudRadius;
  public static int desecratingCloudDuration;

  public static int enkindlingAttackInterval;

  public static double hastyMovementBonus;

  public static int infestedAmount;
  public static int infestedInterval;
  public static double infestedPerHealth;
  public static int infestedTotal;
  public static EntityType<?> infestedParasite;
  public static EntityType<?> infestedEnderParasite;

  public static double paralyzingChance;

  public static double knockingMultiplier;

  public static int livelyCooldown;
  public static double livelyHealAmount;
  public static double livelyPassiveMultiplier;

  public static double magneticStrength;

  public static boolean moltenWaterResistance;

  public static EffectInstance plaguedEffect;
  public static int plaguedRange;

  public static double reflectiveMaxPercent;
  public static double reflectiveMinPercent;
  public static int reflectiveMax;
  public static boolean reflectiveLethal;

  public static double woundingChance;

  public static List<? extends String> scalingHealthSpawnModifiers;

  public static void bake() {
    beaconProtectionRange = SERVER.beaconProtectionRange.get();
    championSpawners = SERVER.championSpawners.get();
    deathMessageTier = SERVER.deathMessageTier.get();
    dimensionList = SERVER.dimensionList.get();
    dimensionPermission = SERVER.dimensionPermission.get();
    entitiesList = SERVER.entitiesList.get();
    entitiesPermission = SERVER.entitiesPermission.get();
    showHud = SERVER.showHud.get();
    showParticles = SERVER.showParticles.get();

    fakeLoot = SERVER.fakeLoot.get();
    lootSource = SERVER.lootSource.get();
    lootScaling = SERVER.lootScaling.get();
    ConfigLoot.parse(SERVER.lootDrops.get());

    healthGrowth = SERVER.healthGrowth.get();
    attackGrowth = SERVER.attackGrowth.get();
    armorGrowth = SERVER.armorGrowth.get();
    toughnessGrowth = SERVER.toughnessGrowth.get();
    knockbackResistanceGrowth = SERVER.knockbackResistanceGrowth.get();
    experienceGrowth = SERVER.experienceGrowth.get();
    explosionGrowth = SERVER.explosionGrowth.get();

    affixTargetRange = SERVER.affixTargetRange.get();

    adaptableDamageReductionIncrement = SERVER.adaptableDamageReductionIncrement.get();
    adaptableMaxDamageReduction = SERVER.adaptableMaxDamageReduction.get();

    arcticAttackInterval = SERVER.arcticAttackInterval.get();

    dampenedDamageReduction = SERVER.dampenedDamageReduction.get();

    desecratingCloudActivationTime = SERVER.desecratingCloudActivationTime.get();
    desecratingCloudDuration = SERVER.desecratingCloudDuration.get();
    desecratingCloudInterval = SERVER.desecratingCloudInterval.get();
    desecratingCloudRadius = SERVER.desecratingCloudRadius.get();

    enkindlingAttackInterval = SERVER.enkindlingAttackInterval.get();

    hastyMovementBonus = SERVER.hastyMovementBonus.get();

    infestedAmount = SERVER.infestedAmount.get();
    infestedTotal = SERVER.infestedTotal.get();
    infestedPerHealth = SERVER.infestedPerHealth.get();
    infestedInterval = SERVER.infestedInterval.get();

    EntityType<?> type = ForgeRegistries.ENTITIES
        .getValue(new ResourceLocation(SERVER.infestedParasite.get()));
    infestedParasite = type != null ? type : EntityType.SILVERFISH;

    type = ForgeRegistries.ENTITIES
        .getValue(new ResourceLocation(SERVER.infestedEnderParasite.get()));
    infestedEnderParasite = type != null ? type : EntityType.ENDERMITE;

    paralyzingChance = SERVER.paralyzingChance.get();

    knockingMultiplier = SERVER.knockingMultiplier.get();

    livelyHealAmount = SERVER.livelyHealAmount.get();
    livelyPassiveMultiplier = SERVER.livelyPassiveMultiplier.get();
    livelyCooldown = SERVER.livelyCooldown.get();

    magneticStrength = SERVER.magneticStrength.get();

    moltenWaterResistance = SERVER.moltenWaterResistance.get();

    plaguedRange = SERVER.plaguedRange.get();

    try {
      String[] s = SERVER.plaguedEffect.get().split(";");

      if (s.length < 1) {
        throw new IllegalArgumentException();
      }
      Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s[0]));

      if (effect == null) {
        throw new IllegalArgumentException();
      }

      if (s.length < 2) {
        plaguedEffect = new EffectInstance(effect);
      } else if (s.length < 3) {
        plaguedEffect = new EffectInstance(effect, Integer.parseInt(s[1]) * 20);
      } else {
        plaguedEffect = new EffectInstance(effect, Integer.parseInt(s[1]) * 20,
            Integer.parseInt(s[2]) - 1);
      }
    } catch (IllegalArgumentException e) {
      plaguedEffect = new EffectInstance(Effects.POISON, 300, 0);
      Champions.LOGGER.error("Error parsing plaguedEffect config value!");
    }

    reflectiveLethal = SERVER.reflectiveLethal.get();
    reflectiveMax = SERVER.reflectiveMax.get();
    reflectiveMaxPercent = SERVER.reflectiveMaxPercent.get();
    reflectiveMinPercent = SERVER.reflectiveMinPercent.get();

    woundingChance = SERVER.woundingChance.get();

    scalingHealthSpawnModifiers = SERVER.scalingHealthSpawnModifiers.get();

    if (Champions.scalingHealthLoaded) {
      ScalingHealthManager.buildModifiers();
    }
  }
}

