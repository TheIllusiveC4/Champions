package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.entity.EntityCinderSpark;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.EnumDifficulty;

public class AffixCinder extends AffixBase {

  public AffixCinder() {
    super("cinder", AffixCategory.OFFENSE);
  }

  @Override
  public void onSpawn(EntityLiving entity, IChampionship cap) {
    entity.tasks.addTask(0, new AffixCinder.AIAttack(entity));
  }

  static class AIAttack extends EntityAIBase {

    private final EntityLiving entity;
    private int attackTime;

    AIAttack(EntityLiving entity) {
      this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = entity.getAttackTarget();
      return isValidAffixTarget(entity, entitylivingbase, false)
          && entitylivingbase.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    public void startExecuting() {
      this.attackTime = ConfigHandler.affix.cinder.attackInterval;
    }

    @Override
    public void updateTask() {
      if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
        --this.attackTime;
        EntityLivingBase entitylivingbase = entity.getAttackTarget();

        if (entitylivingbase != null) {
          entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
          if (this.attackTime <= 0) {
            this.attackTime =
                ConfigHandler.affix.cinder.attackInterval + entity.getRNG().nextInt(5) * 10;
            EntityCinderSpark spark = new EntityCinderSpark(entity.world, entity, entitylivingbase,
                entity.getHorizontalFacing().getAxis());
            entity.world.spawnEntity(spark);
            entity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F,
                (entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.2F + 1.0F);
          }
        }
      }
    }
  }
}
