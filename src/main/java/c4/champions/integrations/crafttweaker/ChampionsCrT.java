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

package c4.champions.integrations.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.champions")
public class ChampionsCrT {

    @ZenMethod
    public static void addStage(String stage, String entity) {
        CraftTweakerAPI.apply(new ActionAddChampionStage(stage, entity));
    }

    @ZenMethod
    public static void addStage(String stage, String entity, int dimension) {
        CraftTweakerAPI.apply(new ActionAddChampionStage(stage, entity, dimension));
    }

    @ZenMethod
    public static void addTierStage(String stage, int tier) {
        CraftTweakerAPI.apply(new ActionAddTierStage(stage, tier));
    }

    @ZenMethod
    public static void addTierStage(String stage, int tier, int dimension) {
        CraftTweakerAPI.apply(new ActionAddTierStage(stage, tier, dimension));
    }
}
