package top.theillusivec4.champions.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.common.rank.Rank;

public interface IChampion {

  interface Client {

    Optional<Tuple<Integer, Integer>> getRank();

    void setRank(Tuple<Integer, Integer> rank);

    Optional<IAffix> getAffix(String id);

    List<IAffix> getAffixes();

    void setAffixes(Set<String> affixIds);

    CompoundTag getData(String identifier);

    void setData(String identifier, CompoundTag data);
  }

  interface Server {

    Optional<Rank> getRank();

    void setRank(Rank rank);

    List<IAffix> getAffixes();

    void setAffixes(List<IAffix> affixes);

    CompoundTag getData(String identifier);

    void setData(String identifier, CompoundTag data);
  }

  Client getClient();

  Server getServer();

  LivingEntity getLivingEntity();
}
