package top.theillusivec4.champions.common;

import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.stat.ChampionsStats;
import top.theillusivec4.champions.common.util.ChampionBuilder;

@SuppressWarnings("unused")
public class ChampionEventsHandler {

  @SubscribeEvent
  public void onLivingXpDrop(LivingExperienceDropEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();
    ChampionCapability.getCapability(livingEntity)
      .ifPresent(champion -> champion.getServer().getRank().ifPresent(rank -> {
        int growth = rank.getGrowthFactor();

        if (growth > 0) {
          evt.setDroppedExperience(
            (growth * ChampionsConfig.experienceGrowth * evt.getOriginalExperience() +
              evt.getDroppedExperience()));
        }
      }));
  }

  @SubscribeEvent
  public void onExplosion(ExplosionEvent.Start evt) {
    Explosion explosion = evt.getExplosion();
    Entity entity = explosion.getExploder();

    if (entity != null && !entity.getLevel().isClientSide()) {
      ChampionCapability.getCapability(entity)
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

    if (!entity.getLevel().isClientSide()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        Optional<Rank> maybeRank = serverChampion.getRank();

        if (maybeRank.isEmpty()) {
          ChampionBuilder.spawn(champion);
        }
        serverChampion.getAffixes().forEach(affix -> affix.onSpawn(champion));
        serverChampion.getRank().ifPresent(rank -> {
          List<Tuple<MobEffect, Integer>> effects = rank.getEffects();
          effects.forEach(effectPair -> champion.getLivingEntity()
            .addEffect(new MobEffectInstance(effectPair.getA(), 200, effectPair.getB())));
        });
      });
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (livingEntity.getLevel().isClientSide()) {
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        IChampion.Client clientChampion = champion.getClient();
        clientChampion.getAffixes().forEach(affix -> affix.onClientUpdate(champion));
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
      });
    } else if (livingEntity.tickCount % 10 == 0) {
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(affix -> affix.onServerUpdate(champion));
        serverChampion.getRank().ifPresent(rank -> {
          if (livingEntity.tickCount % 4 == 0) {
            List<Tuple<MobEffect, Integer>> effects = rank.getEffects();
            effects.forEach(effectPair -> livingEntity.addEffect(
              new MobEffectInstance(effectPair.getA(), 100, effectPair.getB())));
          }
        });
      });
    }
  }

  @SubscribeEvent
  public void onLivingAttack(LivingAttackEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (livingEntity.getLevel().isClientSide()) {
      return;
    }
    ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
      IChampion.Server serverChampion = champion.getServer();
      serverChampion.getAffixes().forEach(affix -> {

        if (!affix.onAttacked(champion, evt.getSource(), evt.getAmount())) {
          evt.setCanceled(true);
        }
      });
    });

    if (evt.isCanceled()) {
      return;
    }
    Entity source = evt.getSource().getDirectEntity();
    ChampionCapability.getCapability(source).ifPresent(champion -> {
      IChampion.Server serverChampion = champion.getServer();
      serverChampion.getAffixes().forEach(affix -> {

        if (!affix.onAttack(champion, evt.getEntityLiving(), evt.getSource(), evt.getAmount())) {
          evt.setCanceled(true);
        }
      });
    });
  }

  @SubscribeEvent
  public void onLivingHurt(LivingHurtEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes().forEach(
          affix -> amounts[1] = affix.onHurt(champion, evt.getSource(), amounts[0], amounts[1]));
      });
      evt.setAmount(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
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

    if (livingEntity.getLevel().isClientSide()) {
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
        if (!evt.isCanceled()) {
          Entity source = evt.getSource().getEntity();

          if (source instanceof ServerPlayer player && !(source instanceof FakePlayer)) {
            player.awardStat(ChampionsStats.CHAMPION_MOBS_KILLED);
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
        }
      });
    });
  }

  @SubscribeEvent
  public void onLivingHeal(LivingHealEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {
      float[] amounts = new float[] {evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        serverChampion.getAffixes()
          .forEach(affix -> amounts[1] = affix.onHeal(champion, amounts[0], amounts[1]));
      });
      evt.setAmount(amounts[1]);
    }
  }
}
