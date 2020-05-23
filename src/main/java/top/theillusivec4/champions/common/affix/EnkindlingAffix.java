package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.Difficulty;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.affix.core.GoalAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.entity.EnkindlingBulletEntity;

public class EnkindlingAffix extends GoalAffix {

  public EnkindlingAffix() {
    super("enkindling", AffixCategory.OFFENSE);
  }

  @Override
  public List<Tuple<Integer, Goal>> getGoals(IChampion champion) {
    return Collections
        .singletonList(new Tuple<>(0, new AttackGoal((MobEntity) champion.getLivingEntity())));
  }

  static class AttackGoal extends Goal {

    private final MobEntity mobEntity;

    private int attackTime;

    public AttackGoal(MobEntity mobEntity) {
      this.mobEntity = mobEntity;
    }

    @Override
    public boolean shouldExecute() {
      LivingEntity livingentity = this.mobEntity.getAttackTarget();

      if (livingentity != null && livingentity.isAlive()) {
        return BasicAffix.canTarget(this.mobEntity, livingentity, true)
            && this.mobEntity.getEntityWorld().getDifficulty() != Difficulty.PEACEFUL;
      } else {
        return false;
      }
    }

    @Override
    public void startExecuting() {
      this.attackTime = ChampionsConfig.enkindlingAttackInterval * 20;
    }

    @Override
    public void tick() {

      if (this.mobEntity.getEntityWorld().getDifficulty() != Difficulty.PEACEFUL) {
        --this.attackTime;
        LivingEntity livingentity = this.mobEntity.getAttackTarget();

        if (livingentity != null) {
          this.mobEntity.getLookController()
              .setLookPositionWithEntity(livingentity, 180.0F, 180.0F);
          double d0 = this.mobEntity.getDistanceSq(livingentity);

          if (d0 < 400.0D) {
            if (this.attackTime <= 0) {
              this.attackTime = ChampionsConfig.enkindlingAttackInterval * 20
                  + this.mobEntity.getRNG().nextInt(10) * 20 / 2;
              this.mobEntity.world.addEntity(
                  new EnkindlingBulletEntity(this.mobEntity.getEntityWorld(), this.mobEntity,
                      livingentity, this.mobEntity.getHorizontalFacing().getAxis()));
              this.mobEntity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F,
                  (this.mobEntity.getRNG().nextFloat() - this.mobEntity.getRNG().nextFloat()) * 0.2F
                      + 1.0F);
            }
          } else {
            this.mobEntity.setAttackTarget(null);
          }
        }
        super.tick();
      }
    }
  }
}
