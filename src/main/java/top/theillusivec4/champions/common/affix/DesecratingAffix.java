package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.affix.core.GoalAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class DesecratingAffix extends GoalAffix {

  public DesecratingAffix() {
    super("desecrating", AffixCategory.OFFENSE);
  }

  @Override
  public boolean onAttacked(IChampion champion, DamageSource source, float amount) {
    return !(source.getImmediateSource() instanceof AreaEffectCloudEntity)
        || source.getTrueSource() != champion.getLivingEntity();
  }

  @Override
  public List<Tuple<Integer, Goal>> getGoals(IChampion champion) {
    return Collections
        .singletonList(new Tuple<>(0, new DesecrateGoal((MobEntity) champion.getLivingEntity())));
  }

  public static class DesecrateGoal extends Goal {

    private final MobEntity mobEntity;
    private int attackTime;

    public DesecrateGoal(final MobEntity mobEntity) {
      this.mobEntity = mobEntity;
    }

    @Override
    public void startExecuting() {
      this.attackTime = ChampionsConfig.desecratingCloudInterval * 20;
    }

    @Override
    public void tick() {
      LivingEntity target = this.mobEntity.getAttackTarget();
      this.attackTime--;

      if (this.attackTime <= 0 && target != null) {
        this.attackTime =
            ChampionsConfig.desecratingCloudInterval * 20 + this.mobEntity.getRNG().nextInt(5) * 10;
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(target.getEntityWorld(),
            target.posX, target.posY, target.posZ);
        cloud.setOwner(this.mobEntity);
        cloud.setRadius((float) ChampionsConfig.desecratingCloudRadius);
        cloud.setDuration(ChampionsConfig.desecratingCloudDuration * 20);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(ChampionsConfig.desecratingCloudActivationTime * 20);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1));
        target.getEntityWorld().addEntity(cloud);
      }
    }

    @Override
    public boolean shouldExecute() {
      return BasicAffix.canTarget(this.mobEntity, this.mobEntity.getAttackTarget(), true);
    }
  }
}
