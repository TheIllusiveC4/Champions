package top.theillusivec4.champions.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.champions.Champions;

public class ClientChampionsConfig {

  private static final String CONFIG_PREFIX = "gui." + Champions.MODID + ".config.";

  public static final ForgeConfigSpec CLIENT_SPEC;
  public static final Client CLIENT;

  static {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Client::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static class Client {

    public final ConfigValue<List<? extends String>> rankColorOverrides;
    public final IntValue hudXOffset;
    public final IntValue hudYOffset;
    public final IntValue hudRange;

    public Client(ForgeConfigSpec.Builder builder) {
      builder.push("general");

      rankColorOverrides = builder
          .comment("List of overrides for rank colors\nFormat: [tier];[color]")
          .translation(CONFIG_PREFIX + "rankColorOverrides")
          .defineList("rankColorOverrides", new ArrayList<>(), s -> s instanceof String);

      hudXOffset = builder.comment("The x-offset for the champion HUD")
          .translation(CONFIG_PREFIX + "hudXOffset").defineInRange("hudXOffset", 0, -1000, 1000);

      hudYOffset = builder.comment("The y-offset for the champion HUD")
          .translation(CONFIG_PREFIX + "hudYOffset").defineInRange("hudYOffset", 0, -1000, 1000);

      hudRange = builder.comment("The distance, in blocks, from which the champion HUD can be seen")
          .translation(CONFIG_PREFIX + "hudRange").defineInRange("hudRange", 50, 0, 1000);

      builder.pop();
    }
  }

  public static Map<Integer, Integer> rankColorOverrides;
  public static int hudXOffset;
  public static int hudYOffset;
  public static int hudRange;

  public static void bake() {
    rankColorOverrides = new HashMap<>();
    CLIENT.rankColorOverrides.get().forEach(str -> {
      String[] split = str.split(";");
      int tier = 0;
      int color = 0;

      try {

        if (split.length > 0) {
          tier = Integer.parseInt(split[0]);
        }

        if (split.length > 1) {
          color = Integer.parseInt(split[1]);
        }
      } catch (NumberFormatException e) {
        return;
      }
      rankColorOverrides.put(tier, color);
    });

    hudXOffset = CLIENT.hudXOffset.get();
    hudYOffset = CLIENT.hudYOffset.get();
    hudRange = CLIENT.hudRange.get();
  }
}
