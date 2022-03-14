package top.theillusivec4.champions.common;

import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.LootSource;
import top.theillusivec4.champions.common.config.ConfigLoot;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.registry.RegistryReference;
import top.theillusivec4.champions.common.util.ChampionBuilder;
import top.theillusivec4.champions.common.util.ChampionHelper;

@SuppressWarnings("unused")
public class ChampionEventsHandler {

  @SubscribeEvent
  public void onLivingDrops(LivingDropsEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!ChampionHelper.isValidChampion(livingEntity) ||
        !livingEntity.getLevel().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) ||
        (!ChampionsConfig.fakeLoot && evt.getSource().getDirectEntity() instanceof FakePlayer)) {
      return;
    }
    ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
      IChampion.Server serverChampion = champion.getServer();
      ServerLevel serverWorld = (ServerLevel) livingEntity.getLevel();

      if (ChampionsConfig.lootSource != LootSource.CONFIG) {
        LootTable lootTable = serverWorld.getServer().getLootTables()
            .get(new ResourceLocation(RegistryReference.CHAMPION_LOOT));
        DamageSource source = evt.getSource();
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverWorld)
            .withRandom(livingEntity.getRandom())
            .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
            .withParameter(LootContextParams.ORIGIN, livingEntity.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, source)
            .withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getDirectEntity())
            .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY,
                source.getDirectEntity()));
        LivingEntity attackingEntity = livingEntity.getKillCredit();

        if (attackingEntity instanceof Player) {
          lootcontext$builder = lootcontext$builder
              .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player) attackingEntity)
              .withLuck(((Player) attackingEntity).getLuck());
        }
        List<ItemStack> stacks = lootTable
            .getRandomItems(lootcontext$builder.create(LootContextParamSets.ENTITY));
        stacks.forEach(stack -> {
          ItemEntity itemEntity = new ItemEntity(serverWorld, livingEntity.position().x,
              livingEntity.position().y, livingEntity.position().z, stack);
          itemEntity.setDefaultPickUpDelay();
          evt.getDrops().add(itemEntity);
        });
      }

      if (ChampionsConfig.lootSource != LootSource.LOOT_TABLE) {
        List<ItemStack> loot = ConfigLoot
            .getLootDrops(serverChampion.getRank().map(Rank::getTier).orElse(0));

        if (!loot.isEmpty()) {
          loot.forEach(stack -> {
            ItemEntity itemEntity = new ItemEntity(serverWorld, livingEntity.position().x,
                livingEntity.position().y, livingEntity.position().z, stack);
            itemEntity.setDefaultPickUpDelay();
            evt.getDrops().add(itemEntity);
          });
        }
      }
    });
  }

  @SubscribeEvent
  public void onLivingXpDrop(LivingExperienceDropEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (ChampionHelper.isValidChampion(livingEntity)) {
      ChampionCapability.getCapability(livingEntity)
          .ifPresent(champion -> champion.getServer().getRank().ifPresent(rank -> {
            int growth = rank.getGrowthFactor();

            if (growth > 0) {
              evt.setDroppedExperience(
                  (growth * ChampionsConfig.experienceGrowth * evt.getOriginalExperience() +
                      evt.getOriginalExperience()));
            }
          }));
    }
  }

  @SubscribeEvent
  public void onExplosion(ExplosionEvent.Start evt) {
    Explosion explosion = evt.getExplosion();
    Entity entity = explosion.getExploder();

    if (ChampionHelper.isValidChampion(entity) && !entity.getLevel().isClientSide()) {
      ChampionCapability.getCapability((LivingEntity) entity)
          .ifPresent(champion -> champion.getServer().getRank().ifPresent(rank -> {
            int growth = rank.getGrowthFactor();

            if (growth > 0) {
              explosion.radius += ChampionsConfig.explosionGrowth * growth;
            }
          }));
    }
  }

  @SubscribeEvent
  public void onLivingJoinWorld(EntityJoinWorldEvent evt) {
    Entity entity = evt.getEntity();

    if (!entity.getLevel().isClientSide() && ChampionHelper.isValidChampion(entity)) {
      LivingEntity livingEntity = (LivingEntity) entity;
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        Optional<Rank> maybeRank = serverChampion.getRank();

        if (maybeRank.isEmpty()) {
          ChampionBuilder.spawn(champion);
        }
        serverChampion.getAffixes().forEach(affix -> affix.onSpawn(champion));
        serverChampion.getRank().ifPresent(rank -> {
          List<Tuple<MobEffect, Integer>> effects = rank.getEffects();
          effects.forEach(effectPair -> livingEntity
              .addEffect(new MobEffectInstance(effectPair.getA(), 200, effectPair.getB())));
        });
      });
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (ChampionHelper.isValidChampion(livingEntity)) {
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {

        if (livingEntity.getLevel().isClientSide()) {
          IChampion.Client clientChampion = champion.getClient();
          clientChampion.getRank().ifPresent(rank -> {
            if (ChampionsConfig.showParticles && rank.getA() > 0) {
              int color = rank.getB();
              float r = (float) ((color >> 16) & 0xFF) / 255f;
              float g = (float) ((color >> 8) & 0xFF) / 255f;
              float b = (float) ((color) & 0xFF) / 255f;

              livingEntity.getLevel().addParticle(ChampionsRegistry.RANK,
                  livingEntity.position().x + (livingEntity.getRandom().nextDouble() - 0.5D) *
                      (double) livingEntity.getBbWidth(), livingEntity.position().y +
                      livingEntity.getRandom().nextDouble() * livingEntity.getBbHeight(),
                  livingEntity.position().z + (livingEntity.getRandom().nextDouble() - 0.5D) *
                      (double) livingEntity.getBbWidth(), r, g, b);
            }
          });
        } else {
          IChampion.Server serverChampion = champion.getServer();
          serverChampion.getAffixes().forEach(affix -> affix.onServerUpdate(champion));
          serverChampion.getRank().ifPresent(rank -> {
            if (livingEntity.tickCount % 4 == 0) {
              List<Tuple<MobEffect, Integer>> effects = rank.getEffects();
              effects.forEach(effectPair -> livingEntity.addEffect(
                  new MobEffectInstance(effectPair.getA(), 100, effectPair.getB())));
            }
          });
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingAttack(LivingAttackEvent evt) {
    Entity entity = evt.getEntity();

    if (entity.getLevel().isClientSide()) {
      return;
    }

    if (ChampionHelper.isValidChampion(entity)) {
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(affix -> {

          if (!affix.onAttacked(champion, evt.getSource(), evt.getAmount())) {
            evt.setCanceled(true);
          }
        });
      });
    }

    if (evt.isCanceled()) {
      return;
    }
    Entity source = evt.getSource().getDirectEntity();

    if (ChampionHelper.isValidChampion(source)) {
      ChampionCapability.getCapability((LivingEntity) source).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(affix -> {

          if (!affix.onAttack(champion, evt.getEntityLiving(), evt.getSource(), evt.getAmount())) {
            evt.setCanceled(true);
          }
        });
      });
    }
  }

  @SubscribeEvent
  public void onLivingHurt(LivingHurtEvent evt) {
    Entity entity = evt.getEntity();

    if (!entity.getLevel().isClientSide() && ChampionHelper.isValidChampion(entity)) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(
            affix -> amounts[1] = affix.onHurt(champion, evt.getSource(), amounts[0], amounts[1]));
      });
      evt.setAmount(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    Entity entity = evt.getEntity();

    if (!entity.getLevel().isClientSide() && ChampionHelper.isValidChampion(entity)) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(affix -> amounts[1] = affix
            .onDamage(champion, evt.getSource(), amounts[0], amounts[1]));
      });
      evt.setAmount(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (livingEntity.getLevel().isClientSide() || !ChampionHelper.isValidChampion(livingEntity)) {
      return;
    }
    ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
      IChampion.Server serverChampion = champion.getServer();
      serverChampion.getAffixes().forEach(affix -> {

        if (!affix.onDeath(champion, evt.getSource())) {
          evt.setCanceled(true);
        }
      });
      serverChampion.getRank().ifPresent(rank -> {
        if (!evt.isCanceled() && evt.getSource().getDirectEntity() instanceof Player) {
          int messageTier = ChampionsConfig.deathMessageTier;

          if (messageTier > 0 && rank.getTier() >= messageTier) {
            MinecraftServer server = livingEntity.getServer();

            if (server != null) {
              server.getPlayerList().broadcastMessage(
                  new TranslatableComponent("rank.champions.title." + rank.getTier())
                      .append(" ")
                      .append(livingEntity.getCombatTracker().getDeathMessage()),
                  ChatType.SYSTEM, Util.NIL_UUID);
            }
          }
        }
      });
    });
  }

  @SubscribeEvent
  public void onLivingHeal(LivingHealEvent evt) {
    Entity entity = evt.getEntity();

    if (!entity.getLevel().isClientSide() && ChampionHelper.isValidChampion(entity)) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes()
            .forEach(affix -> amounts[1] = affix.onHeal(champion, amounts[0], amounts[1]));
      });
      evt.setAmount(amounts[1]);
    }
  }
}
