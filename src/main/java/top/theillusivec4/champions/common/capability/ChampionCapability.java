package top.theillusivec4.champions.common.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
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
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.ChampionEventsHandler;
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
        IChampion.Server champion = instance.getServer();
        champion.getRank().ifPresent(rank -> compoundNBT.putInt(TIER_TAG, rank.getTier()));
        List<IAffix> affixes = champion.getAffixes();
        ListNBT list = new ListNBT();
        affixes.forEach(affix -> {
          CompoundNBT tag = new CompoundNBT();
          String id = affix.getIdentifier();
          tag.putString(ID_TAG, id);
          tag.put(DATA_TAG, champion.getData(id));
          list.add(tag);
        });
        compoundNBT.put(AFFIX_TAG, list);
        return compoundNBT;
      }

      @Override
      public void readNBT(Capability<IChampion> capability, IChampion instance, Direction side,
          INBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt;
        IChampion.Server champion = instance.getServer();

        if (compoundNBT.contains(TIER_TAG)) {
          int tier = compoundNBT.getInt(TIER_TAG);
          champion.setRank(RankManager.getRank(tier));
        }

        if (compoundNBT.contains(AFFIX_TAG)) {
          ListNBT list = compoundNBT.getList(AFFIX_TAG, NBT.TAG_COMPOUND);
          List<IAffix> affixes = new ArrayList<>();

          for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            String id = tag.getString(ID_TAG);
            Champions.API.getAffix(id).ifPresent(affix -> {
              affixes.add(affix);

              if (tag.hasUniqueId(DATA_TAG)) {
                champion.setData(id, tag.getCompound(DATA_TAG));
              }
            });
          }
          champion.setAffixes(affixes);
        }
      }
    }, Champion::new);
    MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
    MinecraftForge.EVENT_BUS.register(new ChampionEventsHandler());
  }

  public static Provider createProvider(final LivingEntity livingEntity) {
    return new Provider(livingEntity);
  }

  public static LazyOptional<IChampion> getCapability(final LivingEntity livingEntity) {
    return livingEntity.getCapability(CHAMPION_CAP);
  }

  public static class Champion implements IChampion {

    private final LivingEntity champion;
    private final Client client;
    private final Server server;

    private Champion() {
      this(null);
    }

    private Champion(final LivingEntity livingEntity) {
      this.champion = livingEntity;
      this.client = new Client();
      this.server = new Server();
    }

    @Override
    public Client getClient() {
      return this.client;
    }

    @Override
    public Server getServer() {
      return this.server;
    }

    @Override
    public LivingEntity getLivingEntity() {
      return this.champion;
    }

    public static class Server implements IChampion.Server {

      private Rank rank = null;
      private List<IAffix> affixes = new ArrayList<>();
      private Map<String, CompoundNBT> data = new HashMap<>();

      @Override
      public Optional<Rank> getRank() {
        return Optional.ofNullable(rank);
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
      }

      @Override
      public void setData(String identifier, CompoundNBT data) {
        this.data.put(identifier, data);
      }

      @Override
      public CompoundNBT getData(String identifier) {
        return this.data.getOrDefault(identifier, new CompoundNBT());
      }
    }

    public static class Client implements IChampion.Client {

      private Tuple<Integer, Integer> rank = null;
      private Set<String> affixes = new HashSet<>();

      @Override
      public Optional<Tuple<Integer, Integer>> getRank() {
        return Optional.ofNullable(rank);
      }

      @Override
      public void setRank(Tuple<Integer, Integer> rank) {
        this.rank = rank;
      }

      @Override
      public Set<String> getAffixes() {
        return this.affixes;
      }

      @Override
      public void setAffixes(Set<String> affixes) {
        this.affixes = affixes;
      }
    }
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<IChampion> optional;
    final IChampion data;

    Provider(final LivingEntity livingEntity) {
      this.data = new Champion(livingEntity);
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
