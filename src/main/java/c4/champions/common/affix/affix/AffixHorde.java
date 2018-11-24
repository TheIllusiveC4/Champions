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
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class AffixHorde extends AffixBase {

    public AffixHorde() {
        super("horde", AffixCategory.DEFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        int size = ConfigHandler.affix.horde.hordeSize + cap.getRank().getTier() * ConfigHandler.affix.horde.multiplier;
        Chunk chunk = entity.world.getChunk(entity.getPosition());
        performHordeSpawning(entity.world, entity, entity.getRNG(), chunk.x * 16 + 8, chunk.z * 16 + 8, size);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.isNonBoss();
    }

    private static void performHordeSpawning(World worldIn, EntityLiving parent, Random randomIn,
                                             int centerX, int centerZ, int hordeSize) {
        int diameterX = 5;
        int diameterZ = 5;
        int j = centerX + randomIn.nextInt(diameterX);
        int k = centerZ + randomIn.nextInt(diameterZ);
        int l = j;
        int i1 = k;

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
                        BlockPos blockpos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(j, 0, k));

                        if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND,
                                worldIn, blockpos)) {

                            if (net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(hordeEntity,
                                    worldIn, j + 0.5f, (float) blockpos.getY(), k + 0.5f, null)
                                    == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                                continue;
                            }
                            hordeEntity.setLocationAndAngles((double) ((float) j + 0.5F), (double) blockpos.getY(),
                                    (double) ((float) k + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                            worldIn.spawnEntity(hordeEntity);
                            hordeEntity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(hordeEntity)), null);
                            flag = true;
                        }

                        j += randomIn.nextInt(5) - randomIn.nextInt(5);

                        for (k += randomIn.nextInt(5) - randomIn.nextInt(5); j < centerX || j >= centerX + diameterX || k < centerZ || k >= centerZ + diameterX; k = i1 + randomIn.nextInt(5) - randomIn.nextInt(5)) {
                            j = l + randomIn.nextInt(5) - randomIn.nextInt(5);
                        }
                    }
                }
            }
        }
    }
}
