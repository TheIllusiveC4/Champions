package top.theillusivec4.champions.common.affix.core;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class AffixEventsHandler {

  @SubscribeEvent
  public void onLivingJoinWorld(EntityJoinWorldEvent evt) {
    Entity entity = evt.getEntity();

    if (!entity.getEntityWorld().isRemote() && entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        champion.getAffixes().forEach(affix -> affix.onSpawn(champion));
        champion.getRank().ifPresent(rank -> {
          List<Tuple<Effect, Integer>> effects = rank.getEffects();
          effects.forEach(effectPair -> livingEntity
              .addPotionEffect(new EffectInstance(effectPair.getA(), 200, effectPair.getB())));
        });
      });
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
        champion.getAffixes().forEach(affix -> affix.onUpdate(champion));

        if (entity.ticksExisted % 40 == 0) {
          champion.getRank().ifPresent(rank -> {
            List<Tuple<Effect, Integer>> effects = rank.getEffects();
            effects.forEach(effectPair -> livingEntity
                .addPotionEffect(new EffectInstance(effectPair.getA(), 100, effectPair.getB())));
          });
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingAttack(LivingAttackEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      ChampionCapability.getCapability(livingEntity)
          .ifPresent(champion -> champion.getAffixes().forEach(affix -> {

            if (!affix.onAttacked(champion, evt.getSource(), evt.getAmount())) {
              evt.setCanceled(true);
            }
          }));
    }

    if (evt.isCanceled()) {
      return;
    }
    Entity source = evt.getSource().getTrueSource();

    if (source instanceof LivingEntity) {
      LivingEntity livingSource = (LivingEntity) source;
      ChampionCapability.getCapability(livingSource)
          .ifPresent(champion -> champion.getAffixes().forEach(affix -> {

            if (!affix
                .onAttack(champion, evt.getEntityLiving(), evt.getSource(), evt.getAmount())) {
              evt.setCanceled(true);
            }
          }));
    }
  }

  @SubscribeEvent
  public void onLivingHurt(LivingHurtEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      float[] amounts = new float[]{evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> champion.getAffixes()
          .forEach(affix -> amounts[1] = affix
              .onHurt(champion, evt.getSource(), amounts[0], amounts[1])));
      evt.setAmount(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      float[] amounts = new float[]{evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> champion.getAffixes()
          .forEach(affix -> amounts[1] = affix
              .onDamage(champion, evt.getSource(), amounts[0], amounts[1])));
      evt.setAmount(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingKnockBack(LivingKnockBackEvent evt) {
    Entity entity = evt.getOriginalAttacker();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      float[] amounts = new float[]{evt.getOriginalStrength(), evt.getOriginalStrength()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> champion.getAffixes()
          .forEach(affix -> amounts[1] = affix
              .onKnockBack(champion, evt.getEntityLiving(), amounts[0], amounts[1])));
      evt.setStrength(amounts[1]);
    }
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();
    ChampionCapability.getCapability(livingEntity)
        .ifPresent(champion -> champion.getAffixes().forEach(affix -> {

          if (!affix.onDeath(champion, evt.getSource())) {
            evt.setCanceled(true);
          }
        }));
  }

  @SubscribeEvent
  public void onLivingHeal(LivingHealEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      float[] amounts = new float[]{evt.getAmount(), evt.getAmount()};
      ChampionCapability.getCapability(livingEntity).ifPresent(champion -> champion.getAffixes()
          .forEach(affix -> amounts[1] = affix.onHeal(champion, amounts[0], amounts[1])));
      evt.setAmount(amounts[1]);
    }
  }
}
