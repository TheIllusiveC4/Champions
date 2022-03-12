package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.affix.core.GoalAffix;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.RankManager;

public class InfestedAffix extends GoalAffix {

    public InfestedAffix() {
        super("infested", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(IChampion champion) {
        AffixData.IntegerData buffer = AffixData.getData(champion, this.getIdentifier(), AffixData.IntegerData.class);
        buffer.num = Math.min(ChampionsConfig.infestedTotal, Math.max(1, (int) (champion.getLivingEntity().getMaxHealth() * ChampionsConfig.infestedPerHealth)));
        buffer.saveData();
    }

    @Override
    public float onHeal(IChampion champion, float amount, float newAmount) {
        if (newAmount > 0 && champion.getLivingEntity().getRandom().nextFloat() < 0.5F) {
            AffixData.IntegerData buffer = AffixData
                    .getData(champion, this.getIdentifier(), AffixData.IntegerData.class);
            buffer.num = Math.min(ChampionsConfig.infestedTotal, buffer.num + 2);
            buffer.saveData();
            return Math.max(0, newAmount - 1);
        }
        return newAmount;
    }

    @Override
    public boolean onDeath(IChampion champion, DamageSource source) {
        AffixData.IntegerData buffer = AffixData
                .getData(champion, this.getIdentifier(), AffixData.IntegerData.class);
        LivingEntity target = null;

        if (source.getDirectEntity() instanceof LivingEntity) {
            target = (LivingEntity) source.getDirectEntity();
        }
        Level world = champion.getLivingEntity().getLevel();

        if (world instanceof ServerLevel) {
            spawnParasites(champion.getLivingEntity(), buffer.num, target, (ServerLevel) world);
        }
        return true;
    }

    @Override
    public List<Tuple<Integer, Goal>> getGoals(IChampion champion) {
        return Collections.singletonList(new Tuple<>(0, new SpawnParasiteGoal((Mob) champion.getLivingEntity())));
    }

    @Override
    public boolean canApply(IChampion champion) {
        EntityType<?> type = champion.getLivingEntity().getType();
        return type != ChampionsConfig.infestedParasite && type != ChampionsConfig.infestedEnderParasite
                && super.canApply(champion);
    }

    private static void spawnParasites(LivingEntity livingEntity, int amount, @Nullable LivingEntity target, ServerLevel world) {
        boolean isEnder =
                livingEntity instanceof EnderMan || livingEntity instanceof Shulker
                        || livingEntity instanceof Endermite || livingEntity instanceof EnderDragon;
        EntityType<?> type =
                isEnder ? ChampionsConfig.infestedEnderParasite : ChampionsConfig.infestedParasite;

        for (int i = 0; i < amount; i++) {
            Entity entity = type
                    .create(world, null, null, null, livingEntity.blockPosition(), MobSpawnType.MOB_SUMMONED,
                            false, false);

            if (entity instanceof LivingEntity) {
                ChampionCapability.getCapability((LivingEntity) entity)
                        .ifPresent(champion -> champion.getServer().setRank(RankManager.getLowestRank()));
                livingEntity.getLevel().addFreshEntity(entity);

                if (entity instanceof Mob) {
                    ((Monster) entity).spawnAnim();
                    ((Monster) entity).setLastHurtByMob(target);
                    ((Monster) entity).setTarget(target);
                }
            }
        }
    }

    private class SpawnParasiteGoal extends Goal {
        private final Mob mobEntity;
        private int attackTime;

        public SpawnParasiteGoal(Mob mobEntity) {
            this.mobEntity = mobEntity;
        }

        @Override
        public void start() {
            this.attackTime = ChampionsConfig.infestedInterval * 20;
        }

        @Override
        public void tick() {
            this.attackTime--;

            if (this.attackTime <= 0) {
                ChampionCapability.getCapability(this.mobEntity).ifPresent(champion -> {
                    AffixData.IntegerData buffer = AffixData
                            .getData(champion, InfestedAffix.this.getIdentifier(), AffixData.IntegerData.class);

                    if (buffer.num > 0 && this.mobEntity.level instanceof ServerLevel) {
                        this.attackTime =
                                ChampionsConfig.infestedInterval * 20 + this.mobEntity.getRandom().nextInt(5) * 10;
                        int amount = ChampionsConfig.infestedAmount;
                        spawnParasites(this.mobEntity, amount, this.mobEntity.getTarget(),
                                (ServerLevel) this.mobEntity.level);
                        buffer.num = Math.max(0, buffer.num - amount);
                        buffer.saveData();
                    }
                });
            }
        }

        @Override
        public boolean canUse() {
            return BasicAffix.canTarget(this.mobEntity, this.mobEntity.getTarget(), true);
        }
    }
}
