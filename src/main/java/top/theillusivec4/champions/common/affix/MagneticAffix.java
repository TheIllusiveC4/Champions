package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;
import javax.vecmath.Vector3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.Vec3d;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.affix.core.GoalAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class MagneticAffix extends GoalAffix {

  public MagneticAffix() {
    super("magnetic", AffixCategory.CC);
  }

  @Override
  public List<Tuple<Integer, Goal>> getGoals(IChampion champion) {
    return Collections
        .singletonList(new Tuple<>(0, new PullGoal((MobEntity) champion.getLivingEntity())));
  }

  public static class PullGoal extends Goal {

    final MobEntity mobEntity;
    LivingEntity target = null;

    public PullGoal(final MobEntity mobEntity) {
      this.mobEntity = mobEntity;
    }

    @Override
    public void startExecuting() {
      target = mobEntity.getAttackTarget();
      super.startExecuting();
    }

    @Override
    public void resetTask() {
      target = null;
      super.resetTask();
    }

    @Override
    public boolean shouldExecute() {
      return mobEntity.getAttackTarget() != null && BasicAffix
          .canTarget(mobEntity, mobEntity.getAttackTarget(), true)
          && mobEntity.ticksExisted % 40 == 0 && mobEntity.getRNG().nextDouble() < 0.4D;
    }

    @Override
    public boolean shouldContinueExecuting() {
      return mobEntity.ticksExisted % 40 != 0 || mobEntity.getRNG().nextDouble() > 0.7D;
    }

    @Override
    public void tick() {
      double x = mobEntity.posX;
      double y = mobEntity.posY;
      double z = mobEntity.posZ;
      double strength = ChampionsConfig.magneticStrength;
      Vector3d vec = new Vector3d(x, y, z);
      vec.sub(new Vector3d(target.posX, target.posY, target.posZ));
      vec.normalize();
      vec.scale(strength);
      target.setMotion(new Vec3d(vec.x, vec.y, vec.z));

      if (target instanceof PlayerEntity) {
        ((PlayerEntity) target).velocityChanged = true;
      }
    }
  }
}
