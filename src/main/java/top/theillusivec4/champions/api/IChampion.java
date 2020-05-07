package top.theillusivec4.champions.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import top.theillusivec4.champions.common.rank.Rank;

public interface IChampion {

  LivingEntity getLivingEntity();

  Optional<Rank> getRank();

  void setRank(Rank rank);

  List<IAffix> getAffixes();

  void setAffixes(List<IAffix> affixes);

  Set<String> getAffixIds();

  void setAffixIds(Set<String> affixIds);

  void setData(String identifier, CompoundNBT data);

  CompoundNBT getData(String identifier);
}
