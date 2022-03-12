package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
        return Collections.singletonList(new Tuple<>(0, new PullGoal((Mob) champion.getLivingEntity())));
    }

    public static class PullGoal extends Goal {
        final Mob mobEntity;
        LivingEntity target = null;

        public PullGoal(final Mob mobEntity) {
            this.mobEntity = mobEntity;
        }

        @Override
        public void start() {
            target = mobEntity.getTarget();
            super.start();
        }

        @Override
        public void stop() {
            target = null;
            super.stop();
        }

        @Override
        public boolean canUse() {
            return mobEntity.getTarget() != null && BasicAffix
                    .canTarget(mobEntity, mobEntity.getTarget(), true)
                    && mobEntity.tickCount % 40 == 0 && mobEntity.getRandom().nextDouble() < 0.4D;
        }

        @Override
        public boolean canContinueToUse() {
            return mobEntity.tickCount % 40 != 0 || mobEntity.getRandom().nextDouble() > 0.7D;
        }

        @Override
        public void tick() {
            double x = mobEntity.position().x;
            double y = mobEntity.position().y;
            double z = mobEntity.position().z;
            double strength = ChampionsConfig.magneticStrength;
            Vec3 vec = new Vec3(x, y, z).subtract(new Vec3(target.position().x, target.position().y, target.position().z)).normalize().scale(strength);
            target.setDeltaMovement(vec);

            if (target instanceof Player) {
                target.hurtMarked = true;
            }
        }
    }
}
