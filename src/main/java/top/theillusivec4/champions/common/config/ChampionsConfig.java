package top.theillusivec4.champions.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.champions.common.config.RanksConfig.RankConfig;

public class ChampionsConfig {

  public static final ForgeConfigSpec RANKS_SPEC;
  public static final Ranks RANKS;

  static {
    final Pair<Ranks, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Ranks::new);
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
}

