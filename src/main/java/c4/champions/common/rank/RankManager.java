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

package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.util.JsonUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.TreeMap;

public class RankManager {

    private static final Rank[] DEFAULT_RANKS = new Rank[] {
            new Rank(1, 1, 1, 0.2f, 0xffff00),
            new Rank(2, 2, 4, 0.2f, 0xff9900),
            new Rank(3, 3, 9, 0.2f, 0x66ffff),
            new Rank(4, 4, 16, 0.2f, 0xcc33ff)
    };
    private static final Rank EMPTY_RANK = new Rank();

    private static final TreeMap<Integer, Rank> RANKS = Maps.newTreeMap();

    public static ImmutableMap<Integer, Rank> getRanks() {
        return ImmutableMap.copyOf(RANKS);
    }

    @Nonnull
    public static Rank getRankForTier(int tier) {
        Rank rank = tier > 0 ? RANKS.get(tier) : EMPTY_RANK;

        if (rank == null) {
            Champions.logger.log(Level.ERROR, "Tried getting rank from tier " + tier + " but rank does not " +
                    "exist! Returning empty rank.");
            return EMPTY_RANK;
        }
        return rank;
    }

    public static Rank getEmptyRank() {
        return EMPTY_RANK;
    }

    public static void readRanksFromJson() {
        Rank[] ranks = JsonUtil.fromJson(TypeToken.get(Rank[].class), new File(Loader.instance().getConfigDir(),
                Champions.MODID + "/ranks.json"), DEFAULT_RANKS);

        for (Rank rank : ranks) {
            RANKS.put(rank.getTier(), rank);
        }
    }
}
