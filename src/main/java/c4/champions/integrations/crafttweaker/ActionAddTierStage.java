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

package c4.champions.integrations.crafttweaker;

import c4.champions.integrations.gamestages.ChampionStages;
import crafttweaker.IAction;

public class ActionAddTierStage implements IAction {

    private final String stage;
    private final int tier;
    private final int dimension;
    private final boolean dimensional;

    public ActionAddTierStage(String stage, int tier) {
        this(stage, tier, 0, false);
    }

    public ActionAddTierStage(String stage, int tier, int dimension) {
        this(stage, tier, dimension, true);
    }

    public ActionAddTierStage(String stage, int tier, int dimension, boolean dimensional) {
        this.stage = stage;
        this.tier = tier;
        this.dimension = dimension;
        this.dimensional = dimensional;
    }

    @Override
    public void apply() {

        if (this.dimensional) {
            ChampionStages.addTierStage(tier, stage, dimension);
        } else {
            ChampionStages.addTierStage(tier, stage);
        }
    }

    @Override
    public String describe() {
        return "Adding tier " + this.tier + " to stage " + this.stage + (this.dimensional ? " for dimension " + this.dimension : "");
    }
}
