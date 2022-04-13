package top.theillusivec4.champions.server.advancement.criterion;

import java.util.HashSet;
import java.util.List;
import com.google.gson.JsonObject;
import org.codehaus.plexus.util.CollectionUtils;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;

public class ChampionKilledByPlayerTrigger extends SimpleCriterionTrigger<ChampionKilledByPlayerTrigger.Instance> {

  private static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "champion_killed_by_player");

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext parser) {
    var entity = EntityPredicate.fromJson(json.get("entity"));
    var tier = MinMaxBounds.Ints.fromJson(json.get("tier"));
    var affix = new HashSet<String>() {
      {
        if (json.has("affixes")) {
          GsonHelper.getAsJsonArray(json, "affixes").forEach((affixJson) -> add(GsonHelper.convertToString(affixJson, "affix")));
        }
      }
    };
    boolean matchAllAffixes = GsonHelper.getAsBoolean(json, "match_all_affixes", true);
    return new Instance(player, entity, tier, affix, matchAllAffixes);
  }

  public void trigger(ServerPlayer player, LivingEntity championEntity, int tier, List<IAffix> affixes) {
    this.trigger(player, (instance) -> instance.test(player, championEntity, tier, affixes));
  }

  public static class Instance extends AbstractCriterionTriggerInstance {

    private final EntityPredicate entity;
    private final MinMaxBounds.Ints tier;
    private final HashSet<String> affixes;
    private final boolean matchAllAffixes;

    public Instance(EntityPredicate.Composite player, EntityPredicate entity, MinMaxBounds.Ints tier, HashSet<String> affixes, boolean matchAllAffixes) {
      super(ID, player);
      this.entity = entity;
      this.tier = tier;
      this.affixes = affixes;
      this.matchAllAffixes = matchAllAffixes;
    }

    public boolean test(ServerPlayer player, LivingEntity championEntity, int tier, List<IAffix> affixes) {
      boolean e = this.entity.matches((ServerLevel) championEntity.getLevel(), championEntity.position(), championEntity);
      boolean t =  this.tier.matches(tier);
      boolean a = matchAffixes(this.affixes, affixes.stream().map(IAffix::getIdentifier).toList(), this.matchAllAffixes);
      return e && t && a;
    }

    private static boolean matchAffixes(HashSet<String> a, List<String> b, boolean matchAll) {
      if (a.isEmpty()) return true;
      if (matchAll) {
        if (a.size() != b.size()) return false;
        return a.equals(new HashSet<>(b));
      } else {
        return !CollectionUtils.intersection(a, b).isEmpty();
      }
    }

  }

}
