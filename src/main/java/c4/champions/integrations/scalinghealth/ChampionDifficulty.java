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

package c4.champions.integrations.scalinghealth;

import c4.champions.common.config.ConfigHandler;
import java.util.Map;
import java.util.TreeMap;

public class ChampionDifficulty {

    private static final Map<Integer, Double> MODIFIERS = new TreeMap<>();

    public static void loadConfigs() {

        for (String s : ConfigHandler.scalingHealth.spawnModifiers) {
            String[] parsed = s.split(";");

            if (parsed.length > 1) {
                int tier = Integer.parseInt(parsed[0]);
                double modifier = Double.parseDouble(parsed[1]);

                if (tier > 0 && modifier > 0) {
                    MODIFIERS.put(tier, modifier);
                }
            }
        }
    }

    public static double getSpawnModifier(int tier) {
        return MODIFIERS.getOrDefault(tier, 0.0d);
    }
}
