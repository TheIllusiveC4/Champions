package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AffixBlasting extends AffixBase {

    public AffixBlasting() {
        super("blasting", AffixCategory.OFFENSE);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        entity.tasks.addTask(0, new AIFireballAttack(entity));
        entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100);
    }

    static class AIFireballAttack extends EntityAIBase
    {
        private final EntityLiving entity;
        private int cooldown;

        public AIFireballAttack(EntityLiving livingIn)
        {
            this.entity = livingIn;
            this.setMutexBits(0);
        }

        @Override
        public boolean shouldExecute()
        {
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive() && entitylivingbase.getDistance
                    (entity) >= 5;
        }

        @Override
        public void startExecuting()
        {
            this.cooldown = 0;
        }



        @Override
        public void updateTask() {
            this.cooldown--;
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            double d0 = this.entity.getDistanceSq(entitylivingbase);

            if (d0 < this.getFollowDistance() * this.getFollowDistance()) {
                double d1 = 4.0D;
                Vec3d vec3d = entity.getLook(1.0F);
                double d2 = entitylivingbase.posX - (entity.posX + vec3d.x * 4.0D);
                double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) -
                        (0.5D + entity.posY + (double)(entity.height / 2.0F));
                double d4 = entitylivingbase.posZ - (entity.posZ + vec3d.z * 4.0D);

                if (this.cooldown <= 0) {
                    this.fireFireball(d2, d3, d4, vec3d);
                    cooldown = 30;
                }

                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0F, 10.0F);
            }
            else
            {
                this.entity.getNavigator().clearPath();
                this.entity.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ,
                        1.0D);
            }

            super.updateTask();
        }

        private double getFollowDistance() {
            IAttributeInstance iattributeinstance = this.entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return iattributeinstance.getAttributeValue();
        }

        private void fireFireball(double d2, double d3, double d4, Vec3d vec3d) {
            entity.world.playEvent(null, 1016, new BlockPos(entity), 0);
            EntityLargeFireball entitylargefireball = new EntityLargeFireball(entity.world, entity, d2, d3, d4);
            entitylargefireball.explosionPower = 1;
            entitylargefireball.posX = entitylargefireball.posX + vec3d.x * 4.0D;
            entitylargefireball.posY = entity.posY + (double)(entity.height / 2.0F) + 0.5D;
            entitylargefireball.posZ = entity.posZ + vec3d.z * 4.0D;
            entity.world.spawnEntity(entitylargefireball);
        }
    }
}
