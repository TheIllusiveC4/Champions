package top.theillusivec4.champions.common.integration.crafttweaker.action;

import com.blamejared.crafttweaker.api.actions.IAction;
import top.theillusivec4.champions.common.integration.gamestages.ChampionsStages;

public class AddTierStageAction implements IAction {

  private final String stage;
  private final int tier;
  private final int dimension;
  private final boolean dimensional;

  public AddTierStageAction(String stage, int tier) {
    this(stage, tier, 0, false);
  }

  public AddTierStageAction(String stage, int tier, int dimension) {
    this(stage, tier, dimension, true);
  }

  public AddTierStageAction(String stage, int tier, int dimension, boolean dimensional) {
    this.stage = stage;
    this.tier = tier;
    this.dimension = dimension;
    this.dimensional = dimensional;
  }

  @Override
  public void apply() {

    if (this.dimensional) {
      ChampionsStages.addTierStage(tier, stage, dimension);
    } else {
      ChampionsStages.addTierStage(tier, stage);
    }
  }

  @Override
  public String describe() {
    return "Adding tier " + this.tier + " to stage " + this.stage + (this.dimensional ?
        " for dimension " + this.dimension : "");
  }
}
