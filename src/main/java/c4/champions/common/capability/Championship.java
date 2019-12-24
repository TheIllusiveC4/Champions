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

package c4.champions.common.capability;

import c4.champions.common.rank.Rank;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class Championship implements IChampionship {

    private Map<String, NBTTagCompound> affixData = Maps.newHashMap();
    private Rank rank = null;
    private String name;

    public Championship() {}

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public ImmutableSet<String> getAffixes() {
        return ImmutableSet.copyOf(affixData.keySet());
    }

    @Override
    public void setAffixData(String identifier, NBTTagCompound compound) {
        affixData.replace(identifier, compound);
    }

    @Override
    public void setAffixes(Set<String> affixes) {
        Map<String, NBTTagCompound> newData = Maps.newHashMap();

        for (String s : affixes) {
            newData.put(s, new NBTTagCompound());
        }
        affixData = newData;
    }

    @Override
    public ImmutableMap<String, NBTTagCompound> getAffixData() {
        return ImmutableMap.copyOf(affixData);
    }

    @Override
    public void setAffixData(Map<String, NBTTagCompound> affixes) {
        this.affixData = affixes;
    }

    @Override
    @Nullable
    public NBTTagCompound getAffixData(String identifier) {
        return affixData.get(identifier);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
