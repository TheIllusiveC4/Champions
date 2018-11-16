package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixShielding extends AffixBase {

    public AffixShielding() {
        super("shielding", AffixCategory.DEFENSE);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        if (!entity.world.isRemote && entity.ticksExisted % 40 == 0) {
            AffixNBT.Boolean shielding = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);
            float chance = shielding.mode ? 0.8f : 0.3f;

            if (entity.getRNG().nextFloat() < chance) {
                shielding.mode = !shielding.mode;
                shielding.saveData(entity);
            }
        }
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent
                           evt) {
        AffixNBT.Boolean shielding = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);

        if (shielding.mode) {
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents
                    .ENTITY_PLAYER_ATTACK_NODAMAGE, entity.getSoundCategory(), 1.0F, 1.0F);
            evt.setCanceled(true);
        }
    }
}
