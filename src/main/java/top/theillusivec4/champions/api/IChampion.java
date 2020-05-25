package top.theillusivec4.champions.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Tuple;
import top.theillusivec4.champions.common.rank.Rank;

public interface IChampion {

  interface Client {

    Optional<Tuple<Integer, Integer>> getRank();

    void setRank(Tuple<Integer, Integer> rank);

    Set<String> getAffixes();

    void setAffixes(Set<String> affixIds);
  }

  interface Server {

    Optional<Rank> getRank();

    void setRank(Rank rank);

    List<IAffix> getAffixes();

    void setAffixes(List<IAffix> affixes);

    CompoundNBT getData(String identifier);

    void setData(String identifier, CompoundNBT data);
  }

  Client getClient();

  Server getServer();

  LivingEntity getLivingEntity();
}
