package top.theillusivec4.champions.common.loot;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.util.FakePlayer;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums;
import top.theillusivec4.champions.common.config.ConfigLoot;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class ChampionLootModifier extends LootModifier {

  public ChampionLootModifier(LootItemCondition[] conditions) {
    super(conditions);
  }

  @Nonnull
  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);

    if (entity == null) {
      return generatedLoot;
    }
    DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);

    if (damageSource == null) {
      return generatedLoot;
    }

    if (!entity.getLevel().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) ||
      (!ChampionsConfig.fakeLoot && damageSource.getDirectEntity() instanceof FakePlayer)) {
      return generatedLoot;
    }
    ChampionCapability.getCapability(entity).ifPresent(champion -> {
      IChampion.Server serverChampion = champion.getServer();
      ServerLevel serverWorld = (ServerLevel) entity.getLevel();

      if (ChampionsConfig.lootSource != ConfigEnums.LootSource.CONFIG) {
        LootTable lootTable = serverWorld.getServer().getLootTables()
          .get(new ResourceLocation(RegistryReference.CHAMPION_LOOT));
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverWorld)
          .withRandom(entity.level.getRandom())
          .withParameter(LootContextParams.THIS_ENTITY, entity)
          .withParameter(LootContextParams.ORIGIN, entity.position())
          .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
          .withOptionalParameter(LootContextParams.KILLER_ENTITY, damageSource.getEntity())
          .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY,
            damageSource.getDirectEntity()));

        if (entity instanceof LivingEntity livingEntity) {
          LivingEntity attackingEntity = livingEntity.getKillCredit();

          if (attackingEntity instanceof Player) {
            lootcontext$builder = lootcontext$builder
              .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player) attackingEntity)
              .withLuck(((Player) attackingEntity).getLuck());
          }
        }
        lootTable.getRandomItemsRaw(lootcontext$builder.create(LootContextParamSets.ENTITY),
          generatedLoot::add);
      }

      if (ChampionsConfig.lootSource != ConfigEnums.LootSource.LOOT_TABLE) {
        List<ItemStack> loot = ConfigLoot
          .getLootDrops(serverChampion.getRank().map(Rank::getTier).orElse(0));

        if (!loot.isEmpty()) {
          generatedLoot.addAll(loot);
        }
      }
    });
    return generatedLoot;
  }

  public static class Serializer extends GlobalLootModifierSerializer<ChampionLootModifier> {

    @Override
    public ChampionLootModifier read(ResourceLocation name, JsonObject object,
                                     LootItemCondition[] conditions) {
      return new ChampionLootModifier(conditions);
    }

    @Override
    public JsonObject write(ChampionLootModifier instance) {
      return makeConditions(instance.conditions);
    }
  }
}
