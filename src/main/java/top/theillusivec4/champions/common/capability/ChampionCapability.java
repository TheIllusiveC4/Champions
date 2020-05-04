package top.theillusivec4.champions.common.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;

public class ChampionCapability {

  @CapabilityInject(IChampion.class)
  public static final Capability<IChampion> CHAMPION_CAP;

  public static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "champion");

  private static final String AFFIX_TAG = "affixes";
  private static final String TIER_TAG = "tier";
  private static final String DATA_TAG = "data";
  private static final String ID_TAG = "identifier";

  static {
    CHAMPION_CAP = null;
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(IChampion.class, new IStorage<IChampion>() {

      @Override
      public INBT writeNBT(Capability<IChampion> capability, IChampion instance, Direction side) {
        CompoundNBT compoundNBT = new CompoundNBT();
        Rank rank = instance.getRank();

        if (rank != null) {
          compoundNBT.putInt(TIER_TAG, rank.getTier());
        }
        List<IAffix> affixes = instance.getAffixes();
        ListNBT list = new ListNBT();
        affixes.forEach(affix -> {
          CompoundNBT tag = new CompoundNBT();
          tag.putString(ID_TAG, affix.getIdentifier());
          list.add(tag);
        });
        compoundNBT.put(AFFIX_TAG, list);
        return compoundNBT;
      }

      @Override
      public void readNBT(Capability<IChampion> capability, IChampion instance, Direction side,
          INBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt;

        if (compoundNBT.contains(TIER_TAG)) {
          int tier = compoundNBT.getInt(TIER_TAG);
          instance.setRank(RankManager.getRank(tier));
        }

        if (compoundNBT.contains(AFFIX_TAG)) {
          ListNBT list = compoundNBT.getList(AFFIX_TAG, NBT.TAG_COMPOUND);
          List<IAffix> affixes = new ArrayList<>();

          for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            String id = tag.getString(ID_TAG);
            Champions.API.getAffix(id).ifPresent(affixes::add);
          }
          instance.setAffixes(affixes);
        }
      }
    }, Champion::new);
    MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
  }

  public static Provider createProvider() {
    return new Provider();
  }

  public static LazyOptional<IChampion> getCapability(final LivingEntity livingEntity) {
    return livingEntity.getCapability(CHAMPION_CAP);
  }

  public static class Champion implements IChampion {

    private Rank rank = null;
    private List<IAffix> affixes = new ArrayList<>();
    private Set<String> affixIds = new HashSet<>();

    @Override
    public Rank getRank() {
      return rank;
    }

    @Override
    public void setRank(Rank rank) {
      this.rank = rank;
    }

    @Override
    public List<IAffix> getAffixes() {
      return Collections.unmodifiableList(affixes);
    }

    @Override
    public void setAffixes(List<IAffix> affixes) {
      this.affixes = affixes;
      this.affixIds.clear();
      affixes.forEach(affix -> this.affixIds.add(affix.getIdentifier()));
    }

    @Override
    public Set<String> getAffixIds() {
      return Collections.unmodifiableSet(affixIds);
    }

    @Override
    public void setAffixIds(Set<String> affixIds) {
      this.affixIds = affixIds;
    }
  }

  public interface IChampion {

    Rank getRank();

    void setRank(Rank rank);

    List<IAffix> getAffixes();

    void setAffixes(List<IAffix> affixes);

    Set<String> getAffixIds();

    void setAffixIds(Set<String> affixIds);
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<IChampion> optional;
    final IChampion data;

    Provider() {
      this.data = new Champion();
      this.optional = LazyOptional.of(() -> data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {
      return CHAMPION_CAP.orEmpty(capability, optional);
    }

    @Override
    public INBT serializeNBT() {
      return CHAMPION_CAP.writeNBT(data, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      CHAMPION_CAP.readNBT(data, null, nbt);
    }
  }
}
