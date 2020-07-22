package top.theillusivec4.champions.common.integration.crafttweaker.action;

import com.blamejared.crafttweaker.api.actions.IAction;
import top.theillusivec4.champions.common.integration.gamestages.ChampionsStages;

public class AddEntityStageAction implements IAction {

  private final String stage;
  private final String entity;
  private final String dimension;
  private final boolean dimensional;

  public AddEntityStageAction(String stage, String entity) {
    this(stage, entity, "", false);
  }

  public AddEntityStageAction(String stage, String entity, String dimension) {
    this(stage, entity, dimension, true);
  }

  public AddEntityStageAction(String stage, String entity, String dimension, boolean dimensional) {
    this.stage = stage;
    this.entity = entity;
    this.dimension = dimension;
    this.dimensional = dimensional;
  }

  @Override
  public void apply() {

    if (this.dimensional) {
      ChampionsStages.addStage(entity, stage, dimension);
    } else {
      ChampionsStages.addStage(entity, stage);
    }
  }

  @Override
  public String describe() {
    return "Adding " + this.entity + " to stage " + this.stage + (this.dimensional ?
        " for dimension " + this.dimension : "");
  }
}
