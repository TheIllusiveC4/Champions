/*
 * Copyright (C) 2018  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
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
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return isValidAffixTarget(entity, entitylivingbase, false) && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = ConfigHandler.affix.arctic.attackInterval;
        }

        @Override
        public void updateTask() {
            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase entitylivingbase = entity.getAttackTarget();

                if (entitylivingbase != null) {
                    entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
                    if (this.attackTime <= 0) {
                        this.attackTime = ConfigHandler.affix.arctic.attackInterval + entity.getRNG().nextInt(5) * 10;
                        EntityArcticSpark spark = new EntityArcticSpark(entity.world, entity, entitylivingbase,
                                entity.getHorizontalFacing().getAxis());
                        entity.world.spawnEntity(spark);
                        entity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (entity.getRNG().nextFloat() -
                                entity.getRNG().nextFloat()) * 0.2F + 1.0F);
                    }
                }
            }
        }
    }
}
