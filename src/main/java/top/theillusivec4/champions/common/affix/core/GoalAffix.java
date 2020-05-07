package top.theillusivec4.champions.common.affix.core;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Tuple;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;

public abstract class GoalAffix extends BasicAffix {

  public GoalAffix(String id, AffixCategory category) {
    this(id, category, false);
  }

  public GoalAffix(String id, AffixCategory category, boolean hasSubscriptions) {
    super(id, category, hasSubscriptions);
  }

  @Override
  public void onSpawn(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity instanceof MobEntity) {
      MobEntity mobEntity = (MobEntity) livingEntity;
      this.getGoals(champion)
          .forEach(goal -> mobEntity.goalSelector.addGoal(goal.getA(), goal.getB()));
    }
  }

  @Override
  public boolean canApply(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();
    return livingEntity instanceof MobEntity && super.canApply(champion);
  }

  public abstract List<Tuple<Integer, Goal>> getGoals(IChampion champion);
}
