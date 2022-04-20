package top.theillusivec4.champions.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.rank.Rank;

public record LootItemChampionPropertyCondition(LootContext.EntityTarget target,
                                                MinMaxBounds.Ints tier, AffixesPredicate affixes)
  implements LootItemCondition {

  public static final LootItemConditionType INSTANCE =
    new LootItemConditionType(new ChampionConditionSerializer());

  @Nonnull
  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(this.target.getParam());
  }

  @Override
  public boolean test(LootContext context) {
    Entity entity = context.getParamOrNull(this.target.getParam());
    return ChampionCapability.getCapability(entity).map(champion -> {
      IChampion.Server server = champion.getServer();
      int tier = server.getRank().map(Rank::getTier).orElse(0);

      if (tier <= 0 || !this.tier.matches(tier)) {
        return false;
      }
      List<IAffix> affixes = server.getAffixes();
      return this.affixes.matches(affixes);
    }).orElse(false);
  }

  @Nonnull
  @Override
  public LootItemConditionType getType() {
    return INSTANCE;
  }

  public static class ChampionConditionSerializer
    implements Serializer<LootItemChampionPropertyCondition> {

    @Override
    public void serialize(final JsonObject json, final LootItemChampionPropertyCondition value,
                          final JsonSerializationContext context) {
      json.add("tier", value.tier.serializeToJson());
      json.add("affixes", value.affixes.serializeToJson());
      json.add("entity", context.serialize(value.target));
    }

    @Nonnull
    @Override
    public LootItemChampionPropertyCondition deserialize(JsonObject json, @Nonnull
      JsonDeserializationContext context) {
      MinMaxBounds.Ints tier = MinMaxBounds.Ints.fromJson(json.get("tier"));
      AffixesPredicate affixes = AffixesPredicate.fromJson(json.get("affixes"));
      return new LootItemChampionPropertyCondition(
        GsonHelper.getAsObject(json, "entity", context, LootContext.EntityTarget.class), tier,
        affixes);
    }
  }

  private record AffixesPredicate(Set<String> values, MinMaxBounds.Ints matches,
                                  MinMaxBounds.Ints count) {

    private static final AffixesPredicate ANY =
      new AffixesPredicate(new HashSet<>(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY);

    private boolean matches(List<IAffix> input) {

      if (this.values.isEmpty()) {
        return this.count.matches(input.size());
      } else {
        Set<String> affixes = input.stream().map(IAffix::getIdentifier).collect(Collectors.toSet());
        int found = 0;

        for (String affix : this.values) {

          if (affixes.contains(affix)) {
            found++;
          }
        }
        return this.matches.matches(found) && this.count.matches(input.size());
      }
    }

    private static AffixesPredicate fromJson(JsonElement json) {

      if (json != null && !json.isJsonNull()) {

        if (json.isJsonArray()) {
          JsonArray jsonArray = GsonHelper.convertToJsonArray(json, "affixes");
          Set<String> affixes = new HashSet<>();

          for (JsonElement jsonElement : jsonArray) {

            if (jsonElement.isJsonPrimitive()) {
              affixes.add(jsonElement.getAsString());
            }
          }
          return new AffixesPredicate(affixes, MinMaxBounds.Ints.atLeast(1), MinMaxBounds.Ints.ANY);
        } else {
          JsonObject jsonObject = json.getAsJsonObject();
          Set<String> affixes = new HashSet<>();

          if (jsonObject.has("values")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "values");

            for (JsonElement jsonElement : jsonArray) {

              if (jsonElement.isJsonPrimitive()) {
                affixes.add(jsonElement.getAsString());
              }
            }
          }
          MinMaxBounds.Ints matches = MinMaxBounds.Ints.atLeast(1);

          if (jsonObject.has("matches")) {
            matches = MinMaxBounds.Ints.fromJson(jsonObject.get("matches"));
          }
          MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;

          if (jsonObject.has("count")) {
            count = MinMaxBounds.Ints.fromJson(jsonObject.get("count"));
          }
          return new AffixesPredicate(affixes, matches, count);
        }
      }
      return ANY;
    }

    public JsonElement serializeToJson() {
      if (this.values.isEmpty() && this.count.isAny() && this.matches.isAny()) {
        return JsonNull.INSTANCE;
      } else {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        for (String value : this.values) {
          jsonArray.add(value);
        }
        Integer min = this.count.getMin();
        Integer max = this.count.getMax();

        if (min != null && min == 1 && max == null) {
          return jsonArray;
        }
        jsonObject.add("values", jsonArray);
        jsonObject.add("matches", this.matches.serializeToJson());
        jsonObject.add("count", this.count.serializeToJson());
        return jsonObject;
      }
    }
  }
}
