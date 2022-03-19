package top.theillusivec4.champions.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.rank.Rank;

public class EntityIsChampion implements LootItemCondition {

  public static LootItemConditionType type =
      new LootItemConditionType(new EntityIsChampion.Serializer());

  @Nullable
  private final Integer minTier;
  @Nullable
  private final Integer maxTier;
  private final LootContext.EntityTarget target;

  private EntityIsChampion(@Nullable Integer minTier, @Nullable Integer maxTier,
                           LootContext.EntityTarget targetIn) {
    this.minTier = minTier;
    this.maxTier = maxTier;
    this.target = targetIn;
  }

  @NotNull
  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(this.target.getParam());
  }

  @Override
  public boolean test(LootContext context) {
    Entity entity = context.getParam(this.target.getParam());

    if (entity == null) {
      return false;
    } else {
      return ChampionCapability.getCapability(entity).map(champion -> {
        int tier = champion.getServer().getRank().map(Rank::getTier).orElse(0);
        boolean aboveMin = minTier == null ? tier >= 1 : tier >= minTier;
        boolean belowMax = maxTier == null || tier <= maxTier;
        return aboveMin && belowMax;
      }).orElse(false);
    }
  }

  @Nonnull
  @Override
  public LootItemConditionType getType() {
    return type;
  }

  public static class Serializer
      implements net.minecraft.world.level.storage.loot.Serializer<EntityIsChampion> {

    @Override
    public void serialize(final JsonObject json, final EntityIsChampion value,
                          final JsonSerializationContext context) {
      json.addProperty("maxTier", value.maxTier);
      json.addProperty("minTier", value.minTier);
      json.add("entity", context.serialize(value.target));
    }

    @Nonnull
    @Override
    public EntityIsChampion deserialize(
        JsonObject json,
        @Nonnull JsonDeserializationContext context) {
      Integer minTier = json.has("minTier") ? GsonHelper.getAsInt(json, "minTier") : null;
      Integer maxTier = json.has("maxTier") ? GsonHelper.getAsInt(json, "maxTier") : null;

      return new EntityIsChampion(minTier, maxTier,
          GsonHelper.getAsObject(json, "entity", context, LootContext.EntityTarget.class));
    }
  }
}
