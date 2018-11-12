package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixEvents {

    @SubscribeEvent
    public void onLivingSetAttack(LivingSetAttackTargetEvent evt) {

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
}
