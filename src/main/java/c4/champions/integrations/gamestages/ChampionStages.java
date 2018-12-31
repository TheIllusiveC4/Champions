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

package c4.champions.integrations.gamestages;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ChampionStages {

    private static final Map<String, Info> ENTITY_STAGE_INFO = Maps.newHashMap();
    private static final Map<Integer, Info> TIER_STAGE_INFO = Maps.newHashMap();

    public static void addStage(String entity, String stage) {
        ENTITY_STAGE_INFO.merge(entity, new Info(stage), (k, v) -> {
            v.addStage(stage);
            return v;
        });
    }

    public static void addStage(String entity, String stage, int dimension) {
        ENTITY_STAGE_INFO.merge(entity, new Info(stage, dimension), (k, v) -> {
            v.addStage(stage, dimension);
            return v;
        });
    }

    public static void addTierStage(int tier, String stage) {
        TIER_STAGE_INFO.merge(tier, new Info(stage), (k, v) -> {
            v.addStage(stage);
            return v;
        });
    }

    public static void addTierStage(int tier, String stage, int dimension) {
        TIER_STAGE_INFO.merge(tier, new Info(stage, dimension), (k, v) -> {
            v.addStage(stage);
            return v;
        });
    }

    @Nullable
    private static Info getStageInfo(String entity) {
        return ENTITY_STAGE_INFO.get(entity);
    }

    @Nullable
    private static Info getStageInfo(int tier) { return TIER_STAGE_INFO.get(tier); }

    public static boolean canSpawn(EntityLiving living) {
        final ResourceLocation rl = EntityList.getKey(living);

        if (rl != null) {
            final String id = rl.toString();
            final Info stageInfo = getStageInfo(id);

            if (stageInfo != null) {
                return hasRequiredStages(stageInfo, living);
            }
        }
        return true;
    }

    public static boolean isValidTier(int tier, EntityLiving living) {
        final Info stageInfo = getStageInfo(tier);

        if (stageInfo != null) {
            return hasRequiredStages(stageInfo, living);
        }
        return true;
    }

    private static boolean hasRequiredStages(@Nonnull Info info, @Nonnull EntityLiving living) {
        int dimension = living.dimension;
        Set<String> stages;

        if (info.dimensionalStages.containsKey(dimension)) {
            stages = info.dimensionalStages.get(dimension);
        } else {
            stages = info.globalStages;
        }

        if (stages.isEmpty()) {
            return true;
        } else {

            for (final EntityPlayer player : living.world.playerEntities) {

                if (GameStageHelper.hasAllOf(player, stages) && player.getDistance(living) <= 128) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Info {

        Map<Integer, Set<String>> dimensionalStages = Maps.newHashMap();
        Set<String> globalStages = Sets.newHashSet();

        Info(String stage) {
            addStage(stage);
        }

        Info(String stage, int dimension) {
            addStage(stage, dimension);
        }

        void addStage(String stage) {
            this.globalStages.add(stage);
        }

        void addStage(String stage, int dimension) {
            this.dimensionalStages.merge(dimension, Sets.newHashSet(stage), (k, v) -> {
                v.add(stage);
                return v;
            });
        }
    }
}
