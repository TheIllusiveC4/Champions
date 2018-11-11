package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;

public class AffixShielding extends AffixBase {

    public AffixShielding() {
        super("shielding", AffixCategory.DEFENSE);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        if (!entity.world.isRemote && entity.ticksExisted % 100 == 0) {
            AffixNBT.Boolean shielding = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);
            shielding.mode = !shielding.mode;
            shielding.saveData();
        }
    }

    @Override
    public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        AffixNBT.Boolean shielding = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);
        return shielding.mode ? 0 : newAmount;
    }
}
