/*
 * Copyright (C) 2018-2019  C4
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
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class AffixInfested extends AffixBase {

    public AffixInfested() {
        super("infested", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
        buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, Math.max(1, (int)(entity.getMaxHealth()
                * ConfigHandler.affix.infested.silverfishPerHealth)));
        buffer.saveData(entity);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        entity.tasks.addTask(0, new AISpawnParasite(entity));
    }

    @Override
    public float onHealed(EntityLiving entity, IChampionship cap, float amount, float newAmount) {

        if (newAmount > 0 && rand.nextFloat() < 0.5F) {
            AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
            buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, buffer.num + 2);
            buffer.saveData(entity);
            return 0;
        }
        return newAmount;
    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {

        if (!entity.world.isRemote) {
            AffixNBT.Integer buffer = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Integer.class);
            EntityLivingBase target = null;

            if (source.getTrueSource() instanceof EntityLivingBase) {
                target = (EntityLivingBase) source.getTrueSource();
            }
            boolean isEnder = false;

            if (entity instanceof EntityEnderman || entity instanceof EntityShulker || entity instanceof EntityEndermite || entity instanceof EntityDragon) {
                isEnder = true;
            }
            List<EntityLiving> parasites = spawnParasites(entity.world, entity.getPosition(), buffer.num, isEnder);

            for (EntityLiving en : parasites) {
                en.setRevengeTarget(target);
            }
        }
    }

    private List<EntityLiving> spawnParasites(World world, BlockPos pos, int amount, boolean isEnder) {
        List<EntityLiving> parasites = Lists.newArrayList();

        for (int i = 0; i < amount; i++) {
            EntityLiving para = isEnder ? new EntityEndermite(world) : new EntitySilverfish(world);
            para.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            IChampionship chp = CapabilityChampionship.getChampionship(para);

            if (chp != null) {
                chp.setRank(RankManager.getEmptyRank());
            }
            world.spawnEntity(para);
            para.spawnExplosionParticle();
            parasites.add(para);
        }
        return parasites;
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return !(entity instanceof EntitySilverfish) && !(entity instanceof EntityEndermite);
    }

    class AISpawnParasite extends EntityAIBase {

        private final EntityLiving entity;
        private int attackTime;

        public AISpawnParasite(EntityLiving entityLiving) {
            this.entity = entityLiving;
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return isValidAffixTarget(entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = ConfigHandler.affix.infested.silverfishInterval;
        }

        @Override
        public void updateTask() {

            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase entitylivingbase = entity.getAttackTarget();
                IChampionship chp = CapabilityChampionship.getChampionship(entity);

                if (entitylivingbase != null && chp != null) {
                    entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
                    AffixNBT.Integer buffer = AffixNBT.getData(chp, getIdentifier(), AffixNBT.Integer.class);

                    if (this.attackTime <= 0 && buffer.num > 0) {
                        this.attackTime = ConfigHandler.affix.desecrator.attackInterval + entity.getRNG().nextInt(5) * 10;
                        boolean isEnder = false;

                        if (entity instanceof EntityEnderman || entity instanceof EntityShulker || entity instanceof EntityEndermite || entity instanceof EntityDragon) {
                            isEnder = true;
                        }
                        List<EntityLiving> parasites = spawnParasites(entity.world, entity.getPosition(),
                                ConfigHandler.affix.infested.silverfishAmount, isEnder);

                        for (EntityLiving en : parasites) {
                            en.setAttackTarget(entitylivingbase);
                        }
                        buffer.num = Math.max(0, buffer.num - parasites.size());
                        buffer.saveData(entity);
                    }
                }
                super.updateTask();
            }
        }
    }
}
