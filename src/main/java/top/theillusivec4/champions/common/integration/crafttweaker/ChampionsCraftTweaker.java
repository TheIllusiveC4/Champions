package top.theillusivec4.champions.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;
import top.theillusivec4.champions.common.integration.crafttweaker.action.AddEntityStageAction;
import top.theillusivec4.champions.common.integration.crafttweaker.action.AddTierStageAction;

@Name("mods.champions.ChampionStages")
@ZenRegister(modDeps = {"gamestages"})
public class ChampionsCraftTweaker {

  @Method
  public static void addStage(String stage, String entity) {
    CraftTweakerAPI.apply(new AddEntityStageAction(stage, entity));
  }

  @Method
  public static void addStage(String stage, String entity, String dimension) {
    CraftTweakerAPI.apply(new AddEntityStageAction(stage, entity, dimension));
  }

  @Method
  public static void addTierStage(String stage, int tier) {
    CraftTweakerAPI.apply(new AddTierStageAction(stage, tier));
  }

  @Method
  public static void addTierStage(String stage, int tier, String dimension) {
    CraftTweakerAPI.apply(new AddTierStageAction(stage, tier, dimension));
  }
}
