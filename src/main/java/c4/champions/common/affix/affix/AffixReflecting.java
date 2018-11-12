package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class AffixReflecting extends AffixBase {

    public AffixReflecting() {
        super("reflecting", AffixCategory.OFFENSE);
    }

    @Override
    public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {

        if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)source.getTrueSource();

            if (source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect) source)
                    .getIsThornsDamage()) {
                return newAmount;
            }
            DamageSource reflect = source.setMagicDamage();
            entityLivingBase.attackEntityFrom(reflect, amount * entity.getRNG().nextFloat());
        }
        return newAmount;
    }
}
