package top.theillusivec4.champions.server.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import top.theillusivec4.champions.server.advancement.criterion.ChampionKilledByPlayerTrigger;

public class ChampionsCriterionTriggers {

  public static final ChampionKilledByPlayerTrigger CHAMPION_KILLED_BY_PLAYER = CriteriaTriggers.register(new ChampionKilledByPlayerTrigger());

  public static void init() {}

}
