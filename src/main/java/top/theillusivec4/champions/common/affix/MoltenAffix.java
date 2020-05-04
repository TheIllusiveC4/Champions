package top.theillusivec4.champions.common.affix;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class MoltenAffix extends BasicAffix {

  private static final Field GOALS = ObfuscationReflectionHelper
      .findField(GoalSelector.class, "field_220892_d");

  public MoltenAffix() {
    super("molten", AffixCategory.OFFENSE);
  }

  @Override
  public void onSpawn(LivingEntity livingEntity) {
    livingEntity.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0, true, false));

    if (livingEntity instanceof MobEntity) {
      MobEntity mobEntity = (MobEntity) livingEntity;

      mobEntity.setPathPriority(PathNodeType.WATER, -1.0F);
      mobEntity.setPathPriority(PathNodeType.LAVA, 8.0F);
      mobEntity.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
      mobEntity.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);

      try {
        @SuppressWarnings("unchecked") Set<PrioritizedGoal> goals = (Set<PrioritizedGoal>) GOALS
            .get(mobEntity.goalSelector);
        Iterator<PrioritizedGoal> iter = goals.iterator();

        while (iter.hasNext()) {
          PrioritizedGoal goal = iter.next();
          Goal baseGoal = goal.getGoal();

          if (baseGoal instanceof FleeSunGoal || baseGoal instanceof RestrictSunGoal) {
            iter.remove();
          }
        }
      } catch (Exception e) {
        Champions.LOGGER.error("Error accessing goals!");
      }

      if (mobEntity.getNavigator() instanceof GroundPathNavigator) {
        ((GroundPathNavigator) mobEntity.getNavigator()).setAvoidSun(false);
      }
    }
  }

  @Override
  public void onUpdate(LivingEntity livingEntity) {

    if (!livingEntity.getEntityWorld().isRemote() && livingEntity.ticksExisted % 20 == 0) {
      livingEntity.setFire(10);
      livingEntity.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0, true, false));

      if (!ChampionsConfig.moltenWaterResistance && livingEntity.isWet()) {
        livingEntity.attackEntityFrom(DamageSource.DROWN, 1.0F);
      }
    }
  }

  @Override
  public boolean onAttack(LivingEntity livingEntity, LivingEntity target, DamageSource source,
      float amount) {
    target.setFire(10);
    source.setFireDamage();
    return true;
  }
}
