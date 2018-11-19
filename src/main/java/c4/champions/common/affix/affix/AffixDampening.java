package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class AffixDampening extends AffixBase {

    public AffixDampening() {
        super("dampening", AffixCategory.DEFENSE);
    }

    @Override
    public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return source instanceof EntityDamageSourceIndirect ? newAmount * 0.2f : newAmount;
    }
}
