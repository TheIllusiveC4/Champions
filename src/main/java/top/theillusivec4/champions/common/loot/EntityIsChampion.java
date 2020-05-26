package top.theillusivec4.champions.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class EntityIsChampion implements ILootCondition {

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

  @Override
  @Nonnull
  public Set<LootParameter<?>> getRequiredParameters() {
    return ImmutableSet.of(this.target.getParameter());
  }

  @Override
  public boolean test(LootContext context) {
    Entity entity = context.get(this.target.getParameter());

    if (entity == null) {
      return false;
    } else if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      return ChampionCapability.getCapability(livingEntity).map(champion -> {
        int tier = champion.getServer().getRank().map(Rank::getTier).orElse(0);
        boolean aboveMin = minTier == null ? tier >= 1 : tier >= minTier;
        boolean belowMax = maxTier == null || tier <= maxTier;
        return aboveMin && belowMax;
      }).orElse(false);
    }
    return false;
  }

  public static class Serializer extends ILootCondition.AbstractSerializer<EntityIsChampion> {

    public Serializer() {
      super(new ResourceLocation(RegistryReference.IS_CHAMPION), EntityIsChampion.class);
    }

    @Override
    public void serialize(JsonObject json, EntityIsChampion value,
        JsonSerializationContext context) {
      json.addProperty("maxTier", value.maxTier);
      json.addProperty("minTier", value.minTier);
      json.add("entity", context.serialize(value.target));
    }

    @Nonnull
    @Override
    public EntityIsChampion deserialize(JsonObject json,
        @Nonnull JsonDeserializationContext context) {
      Integer minTier = json.has("minTier") ? JSONUtils.getInt(json, "minTier") : null;
      Integer maxTier = json.has("maxTier") ? JSONUtils.getInt(json, "maxTier") : null;

      return new EntityIsChampion(minTier, maxTier,
          JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
    }
  }
}
