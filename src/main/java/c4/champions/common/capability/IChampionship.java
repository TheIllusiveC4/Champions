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
}
