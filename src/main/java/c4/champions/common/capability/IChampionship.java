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

package c4.champions.common.capability;

import c4.champions.common.rank.Rank;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.Set;

public interface IChampionship {

    Rank getRank();

    void setRank(Rank rank);

    ImmutableSet<String> getAffixes();

    NBTTagCompound getAffixData(String identifier);

    void setAffixData(String identifier, NBTTagCompound compound);

    void setAffixes(Set<String> affixes);

    ImmutableMap<String, NBTTagCompound> getAffixData();

    void setAffixData(Map<String, NBTTagCompound> affixes);

    void setName(String name);

    String getName();
}
