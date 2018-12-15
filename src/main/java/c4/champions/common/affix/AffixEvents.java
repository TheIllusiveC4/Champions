/*
 * Copyright (C) 2018  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketRankParticle;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixEvents {

    @SubscribeEvent
    public void onLivingJoinWorld(EntityJoinWorldEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntity())) {
            EntityLiving living = (EntityLiving)evt.getEntity();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        affix.onSpawn(living, chp);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        affix.onUpdate(living, chp);
                    }
                }

                if (ChampionHelper.isElite(chp.getRank())) {
                    NetworkHandler.INSTANCE.sendToAllTracking(new PacketRankParticle(living.getEntityId(), chp.getRank().getColor()),
                            living);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttacked(LivingAttackEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        affix.onAttacked(living, chp, evt.getSource(), evt.getAmount(), evt);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent evt) {

        if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)evt.getSource().getTrueSource();
            if (ChampionHelper.isValidChampion(entityLivingBase)) {
                EntityLiving living = (EntityLiving)entityLivingBase;
                IChampionship chp = CapabilityChampionship.getChampionship(living);

                if (chp != null) {

                    for (String aff : chp.getAffixes()) {
                        AffixBase affix = AffixRegistry.getAffix(aff);

                        if (affix != null) {
                            affix.onAttack(living, chp, evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingWasHurt(LivingHurtEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            float amount = evt.getAmount();
            float newAmount = amount;

            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        newAmount = affix.onHurt(living, chp, evt.getSource(), amount, newAmount);
                    }
                }
                evt.setAmount(newAmount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamaged(LivingDamageEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            float amount = evt.getAmount();
            float newAmount = amount;

            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        newAmount = affix.onDamaged(living, chp, evt.getSource(), amount, newAmount);
                    }
                }
                evt.setAmount(newAmount);
            }
        }
    }

    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getOriginalAttacker())) {
            EntityLiving living = (EntityLiving)evt.getOriginalAttacker();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        affix.onKnockback(living, chp, evt);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent evt) {

        if (ChampionHelper.isValidChampion(evt.getEntityLiving())) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {

                for (String aff : chp.getAffixes()) {
                    AffixBase affix = AffixRegistry.getAffix(aff);

                    if (affix != null) {
                        affix.onDeath(living, chp, evt.getSource(), evt);
                    }
                }
            }
        }
    }
}
