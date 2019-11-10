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

package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.util.JsonUtil;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

public class RankManager {

    private static final Rank[] DEFAULT_RANKS = new Rank[] {
            new Rank(1, 1, 1, 0.05f, 0xffff00, new String[]{}),
            new Rank(2, 2, 4, 0.2f, 0xff9900, new String[]{}),
            new Rank(3, 3, 9, 0.2f, 0x66ffff, new String[]{}),
            new Rank(4, 4, 16, 0.2f, 0xcc33ff, new String[]{})
    };
    private static final Rank EMPTY_RANK = new Rank();

    private static final TreeMap<Integer, Rank> RANKS = Maps.newTreeMap();
    private static final TreeMap<Integer, List<Tuple<Potion, Integer>>> RANK_POTIONS = Maps.newTreeMap();

    public static ImmutableSortedMap<Integer, Rank> getRanks() {
        return ImmutableSortedMap.copyOf(RANKS);
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

    @Nullable
    public static List<Tuple<Potion, Integer>> getPotionsForTier(int tier) {
        List<Tuple<Potion, Integer>> potions = RANK_POTIONS.get(tier);
        return potions == null ? null : Collections.unmodifiableList(potions);
    }

    public static Rank getEmptyRank() {
        return EMPTY_RANK;
    }

    public static void readRanksFromJson() {
        Rank[] ranks = JsonUtil.fromJson(TypeToken.get(Rank[].class), new File(Loader.instance().getConfigDir(),
                Champions.MODID + "/ranks.json"), DEFAULT_RANKS);

        for (Rank rank : ranks) {
            RANKS.put(rank.getTier(), rank);

            if (rank.getPotions().length > 0) {
                List<Tuple<Potion, Integer>> potions = new ArrayList<>();

                for (String s : rank.getPotions()) {
                    String[] args = s.split(";");
                    int amplifier = args.length > 1 ? Math.max(Integer.parseInt(args[1]), 0) : 0;
                    Potion potion = Potion.getPotionFromResourceLocation(args[0]);

                    if (potion != null) {
                        potions.add(new Tuple<>(potion, amplifier));
                    }
                }

                if (!potions.isEmpty()) {
                    RANK_POTIONS.put(rank.getTier(), potions);
                }
            }
        }
    }
}
