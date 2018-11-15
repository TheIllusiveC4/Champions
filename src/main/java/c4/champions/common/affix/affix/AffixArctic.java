package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.entity.EntityArcticSpark;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.EnumDifficulty;

public class AffixArctic extends AffixBase {

    public AffixArctic() {
        super("arctic", AffixCategory.CC);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        entity.tasks.addTask(0, new AIAttack(entity));
    }

    class AIAttack extends EntityAIBase {

        private final EntityLiving entity;
        private int attackTime;

        public AIAttack(EntityLiving entity) {
            this.entity = entity;
            this.setMutexBits(0);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive() && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = 20;
        }

        @Override
        public void updateTask() {

            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase entitylivingbase = entity.getAttackTarget();

                if (entitylivingbase != null) {
                    entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
                    if (this.attackTime <= 0) {
                        this.attackTime = 20 + entity.getRNG().nextInt(5) * 10;
                        EntityArcticSpark spark = new EntityArcticSpark(entity.world, entity, entitylivingbase,
                                entity.getHorizontalFacing().getAxis());
                        entity.world.spawnEntity(spark);
                        entity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (entity.getRNG().nextFloat() -
                                entity.getRNG().nextFloat()) * 0.2F + 1.0F);
                    }
                }
                super.updateTask();
            }
        }
    }
}
