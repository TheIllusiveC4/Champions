package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixEvents {

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
    public void onLivingHurt(LivingHurtEvent evt) {

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
}
