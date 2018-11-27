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

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.UUID;

public class AffixHorde extends AffixBase {

    private static final String HORDE_TAG = Champions.MODID + ":horde";

    public AffixHorde() {
        super("horde", AffixCategory.DEFENSE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        int size = ConfigHandler.affix.horde.hordeSize + cap.getRank().getTier() * ConfigHandler.affix.horde.multiplier;
        Chunk chunk = entity.world.getChunk(entity.getPosition());
        performHordeSpawning(entity.world, entity, entity.getRNG(), chunk, size);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.isNonBoss();
    }

    private static void performHordeSpawning(World worldIn, EntityLiving parent, Random randomIn, Chunk chunk, int hordeSize) {

        for (int j1 = 0; j1 < hordeSize; ++j1) {
            boolean flag = false;
            ResourceLocation rl = EntityList.getKey(parent);

            if (rl != null) {
                Entity entity = EntityList.createEntityByIDFromName(rl, worldIn);

                if (entity != null && ChampionHelper.isValidChampion(entity)) {
                    EntityLiving hordeEntity = (EntityLiving)entity;
                    IChampionship chp = CapabilityChampionship.getChampionship(hordeEntity);

                    if (chp != null) {
                        chp.setRank(RankManager.getEmptyRank());
                    }

                    for (int k1 = 0; !flag && k1 < 4; ++k1) {
                        BlockPos blockpos = getRandomChunkPosition(chunk.x, chunk.z, parent);
                        int j = blockpos.getX();
                        int k = blockpos.getZ();

                        if (!worldIn.getBlockState(blockpos).isNormalCube() && WorldEntitySpawner
                                .canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity
                                        (hordeEntity.getClass()), worldIn, blockpos)) {

                            if (net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(hordeEntity,
                                    worldIn, j + 0.5f, (float) blockpos.getY(), k + 0.5f, null)
                                    == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                                continue;
                            }
                            hordeEntity.setLocationAndAngles((double) ((float) j + 0.5F), (double) blockpos.getY(),
                                    (double) ((float) k + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                            hordeEntity.getEntityData().setUniqueId(HORDE_TAG, parent.getUniqueID());
                            worldIn.spawnEntity(hordeEntity);
                            hordeEntity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(hordeEntity)), null);
                            flag = true;
                        }
                    }
                }
            }
        }
    }

    private static BlockPos getRandomChunkPosition(int x, int z, Entity entity) {
        double i = MathHelper.clamp(entity.posX + rand.nextInt(7) - 3, x * 16, x * 16 + 16);
        double j = MathHelper.clamp(entity.posZ + rand.nextInt(7) - 3, z * 16, z * 16 + 16);
        return new BlockPos(i, entity.posY, j);
    }

    static class AILeaderHasTarget extends EntityAIBase {

        EntityLiving follower;
        EntityLiving leader;
        UUID leaderUUID;

        public AILeaderHasTarget(EntityLiving follower, UUID leaderUUID) {
            this.follower = follower;
            this.leaderUUID = leaderUUID;
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {

            if (leader == null && follower.world instanceof WorldServer) {
                Entity entity = ((WorldServer) follower.world).getEntityFromUuid(leaderUUID);

                if (entity instanceof EntityLiving) {
                    leader = (EntityLiving)entity;
                }
            }

            if (leader == null) {
                return false;
            }
            EntityLivingBase target = leader.getAttackTarget();
            return target != null && target != follower.getAttackTarget() && EntityAITarget.isSuitableTarget
                    (follower, target, true, false) && follower.getDistance(leader) <= 20;
        }

        @Override
        public void startExecuting() {
            this.follower.setAttackTarget(leader.getAttackTarget());
        }
    }

    static class AIFollowLeader extends EntityAIBase {

        EntityLiving follower;
        EntityLiving leader;
        UUID leaderUUID;
        double moveSpeed;
        private int delayCounter;

        public AIFollowLeader(EntityLiving follower, UUID leaderUUID) {
            this.follower = follower;
            this.leaderUUID = leaderUUID;
            this.moveSpeed = 1.25D;
        }

        public boolean shouldExecute() {

            if (leader == null && follower.world instanceof WorldServer) {
                Entity entity = ((WorldServer) follower.world).getEntityFromUuid(leaderUUID);

                if (entity instanceof EntityLiving) {
                    leader = (EntityLiving)entity;
                }
            }

            if (leader == null || !leader.isEntityAlive()) {
                return false;
            } else {
                double distance = this.follower.getDistance(this.leader);
                return distance >= 7 && distance <= 20;
            }
        }

        public void startExecuting() {
            this.delayCounter = 0;
        }

        public void updateTask() {
            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                this.follower.getNavigator().tryMoveToEntityLiving(this.leader, this.moveSpeed);
            }
        }
    }

    @SubscribeEvent
    public void livingSpawn(EntityJoinWorldEvent evt) {
        Entity entity = evt.getEntity();

        if (ChampionHelper.isValidChampion(entity)) {
            EntityLiving living = (EntityLiving)entity;
            UUID uuid = living.getEntityData().getUniqueId(HORDE_TAG);

            if (uuid != null) {
                living.tasks.addTask(0, new AIFollowLeader(living, uuid));
                living.targetTasks.addTask(0, new AILeaderHasTarget(living, uuid));
            }
        }
    }
}
