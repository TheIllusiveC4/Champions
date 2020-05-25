package top.theillusivec4.champions.common.affix;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
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
    AffixData.IntegerData buffer = AffixData
        .getData(champion, this.getIdentifier(), AffixData.IntegerData.class);
    buffer.num = Math.min(ChampionsConfig.infestedTotal, Math.max(1,
        (int) (champion.getLivingEntity().getMaxHealth() * ChampionsConfig.infestedPerHealth)));
    buffer.saveData();
  }

  @Override
  public float onHeal(IChampion champion, float amount, float newAmount) {

    if (newAmount > 0 && champion.getLivingEntity().getRNG().nextFloat() < 0.5F) {
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

    if (source.getTrueSource() instanceof LivingEntity) {
      target = (LivingEntity) source.getTrueSource();
    }
    spawnParasites(champion.getLivingEntity(), buffer.num, target);
    return true;
  }

  @Override
  public List<Tuple<Integer, Goal>> getGoals(IChampion champion) {
    return Collections.singletonList(
        new Tuple<>(0, new SpawnParasiteGoal((MobEntity) champion.getLivingEntity())));
  }

  @Override
  public boolean canApply(IChampion champion) {
    EntityType<?> type = champion.getLivingEntity().getType();
    return type != ChampionsConfig.infestedParasite && type != ChampionsConfig.infestedEnderParasite
        && super.canApply(champion);
  }

  private static void spawnParasites(LivingEntity livingEntity, int amount,
      @Nullable LivingEntity target) {
    boolean isEnder =
        livingEntity instanceof EndermanEntity || livingEntity instanceof ShulkerEntity
            || livingEntity instanceof EndermiteEntity || livingEntity instanceof EnderDragonEntity;
    EntityType<?> type =
        isEnder ? ChampionsConfig.infestedEnderParasite : ChampionsConfig.infestedParasite;

    for (int i = 0; i < amount; i++) {
      Entity entity = type.create(livingEntity.getEntityWorld(), null, null, null,
          new BlockPos(livingEntity.getPosition()), SpawnReason.MOB_SUMMONED, false, false);

      if (entity instanceof LivingEntity) {
        ChampionCapability.getCapability((LivingEntity) entity)
            .ifPresent(champion -> champion.getServer().setRank(RankManager.getEmptyRank()));
        livingEntity.getEntityWorld().addEntity(entity);

        if (entity instanceof MobEntity) {
          ((MobEntity) entity).spawnExplosionParticle();
          ((MobEntity) entity).setRevengeTarget(target);
          ((MobEntity) entity).setAttackTarget(target);
        }
      }
    }
  }

  private class SpawnParasiteGoal extends Goal {

    private final MobEntity mobEntity;
    private int attackTime;

    public SpawnParasiteGoal(MobEntity mobEntity) {
      this.mobEntity = mobEntity;
    }

    @Override
    public void startExecuting() {
      this.attackTime = ChampionsConfig.infestedInterval * 20;
    }

    @Override
    public void tick() {
      this.attackTime--;

      if (this.attackTime <= 0) {
        ChampionCapability.getCapability(this.mobEntity).ifPresent(champion -> {
          AffixData.IntegerData buffer = AffixData
              .getData(champion, InfestedAffix.this.getIdentifier(), AffixData.IntegerData.class);

          if (buffer.num > 0) {
            this.attackTime =
                ChampionsConfig.infestedInterval * 20 + this.mobEntity.getRNG().nextInt(5) * 10;
            int amount = ChampionsConfig.infestedAmount;
            spawnParasites(this.mobEntity, amount, this.mobEntity.getAttackTarget());
            buffer.num = Math.max(0, buffer.num - amount);
            buffer.saveData();
          }
        });
      }
    }

    @Override
    public boolean shouldExecute() {
      return BasicAffix.canTarget(this.mobEntity, this.mobEntity.getAttackTarget(), true);
    }
  }
}
