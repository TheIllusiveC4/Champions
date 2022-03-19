package top.theillusivec4.champions.common.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.ChampionEventsHandler;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.util.ChampionHelper;

public class ChampionCapability {

  public static final Capability<IChampion> CHAMPION_CAP =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  public static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "champion");

  private static final String AFFIX_TAG = "affixes";
  private static final String TIER_TAG = "tier";
  private static final String DATA_TAG = "data";
  private static final String ID_TAG = "identifier";

  private static final Map<Entity, LazyOptional<IChampion>> SERVER_CACHE = new HashMap<>();
  private static final Map<Entity, LazyOptional<IChampion>> CLIENT_CACHE = new HashMap<>();

  public static void register() {
    MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());
    MinecraftForge.EVENT_BUS.register(new ChampionEventsHandler());
  }

  private static Map<Entity, LazyOptional<IChampion>> getCache(Level level) {
    return level.isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
  }

  public static Provider createProvider(final LivingEntity livingEntity) {
    return new Provider(livingEntity);
  }

  public static LazyOptional<IChampion> getCapability(final Entity entity) {

    if (!ChampionHelper.isValidChampion(entity)) {
      return LazyOptional.empty();
    }
    LivingEntity livingEntity = (LivingEntity) entity;
    Level level = livingEntity.getLevel();
    Map<Entity, LazyOptional<IChampion>> cache = getCache(level);
    LazyOptional<IChampion> optional = cache.get(livingEntity);

    if (optional == null) {
      optional = livingEntity.getCapability(CHAMPION_CAP);
      cache.put(livingEntity, optional);
      optional.addListener(self -> cache.remove(livingEntity));
    }
    return optional;
  }

  public static class Champion implements IChampion {

    private final LivingEntity champion;
    private final Client client;
    private final Server server;

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

    @Nonnull
    @Override
    public LivingEntity getLivingEntity() {
      return this.champion;
    }

    public static class Server implements IChampion.Server {

      private Rank rank = null;
      private List<IAffix> affixes = new ArrayList<>();
      private final Map<String, CompoundTag> data = new HashMap<>();

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
        return Collections.unmodifiableList(this.affixes);
      }

      @Override
      public void setAffixes(List<IAffix> affixes) {
        this.affixes = affixes;
      }

      @Override
      public void setData(String identifier, CompoundTag data) {
        this.data.put(identifier, data);
      }

      @Override
      public CompoundTag getData(String identifier) {
        return this.data.getOrDefault(identifier, new CompoundTag());
      }
    }

    public static class Client implements IChampion.Client {

      private Tuple<Integer, Integer> rank = null;
      private final List<IAffix> affixes = new ArrayList<>();
      private final Map<String, IAffix> idToAffix = new HashMap<>();
      private final Map<String, CompoundTag> data = new HashMap<>();

      @Override
      public Optional<Tuple<Integer, Integer>> getRank() {
        return Optional.ofNullable(rank);
      }

      @Override
      public void setRank(Tuple<Integer, Integer> rank) {
        this.rank = rank;
      }

      @Override
      public List<IAffix> getAffixes() {
        return Collections.unmodifiableList(this.affixes);
      }

      @Override
      public void setAffixes(Set<String> affixes) {
        this.affixes.clear();

        for (String affix : affixes) {
          Champions.API.getAffix(affix).ifPresent(val -> {
            this.affixes.add(val);
            this.idToAffix.put(val.getIdentifier(), val);
          });
        }
      }

      @Override
      public Optional<IAffix> getAffix(String id) {
        return Optional.ofNullable(this.idToAffix.get(id));
      }

      @Override
      public void setData(String identifier, CompoundTag data) {
        this.data.put(identifier, data);
      }

      @Override
      public CompoundTag getData(String identifier) {
        return this.data.getOrDefault(identifier, new CompoundTag());
      }
    }
  }

  public static class Provider implements ICapabilitySerializable<Tag> {

    final LazyOptional<IChampion> optional;
    final IChampion data;

    Provider(final LivingEntity livingEntity) {
      this.data = new Champion(livingEntity);
      this.optional = LazyOptional.of(() -> data);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap,
                                             @Nullable final Direction side) {
      return cap == CHAMPION_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
      CompoundTag compoundNBT = new CompoundTag();
      IChampion.Server champion = data.getServer();
      champion.getRank().ifPresent(rank -> compoundNBT.putInt(TIER_TAG, rank.getTier()));
      List<IAffix> affixes = champion.getAffixes();
      ListTag list = new ListTag();
      affixes.forEach(affix -> {
        CompoundTag tag = new CompoundTag();
        String id = affix.getIdentifier();
        tag.putString(ID_TAG, id);
        tag.put(DATA_TAG, champion.getData(id));
        list.add(tag);
      });
      compoundNBT.put(AFFIX_TAG, list);
      return compoundNBT;
    }

    @Override
    public void deserializeNBT(final Tag nbt) {
      CompoundTag compoundNBT = (CompoundTag) nbt;
      IChampion.Server champion = data.getServer();

      if (compoundNBT.contains(TIER_TAG)) {
        int tier = compoundNBT.getInt(TIER_TAG);
        champion.setRank(RankManager.getRank(tier));
      }

      if (compoundNBT.contains(AFFIX_TAG)) {
        ListTag list = compoundNBT.getList(AFFIX_TAG, CompoundTag.TAG_COMPOUND);
        List<IAffix> affixes = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
          CompoundTag tag = list.getCompound(i);
          String id = tag.getString(ID_TAG);
          Champions.API.getAffix(id).ifPresent(affix -> {
            affixes.add(affix);

            if (tag.hasUUID(DATA_TAG)) {
              champion.setData(id, tag.getCompound(DATA_TAG));
            }
          });
        }
        champion.setAffixes(affixes);
      }
    }
  }
}
