package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.entity.EntityJail;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixJailer extends AffixBase {

    public AffixJailer() {
        super("jailer", AffixCategory.CC);
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

        if (!entity.world.isRemote && entity.getRNG().nextFloat() < 0.25f &&
                !target.isPotionActive(ChampionsRegistry.jailed)) {
            target.addPotionEffect(new PotionEffect(ChampionsRegistry.jailed, 5, 0, false, false));
            EntityJail jail = new EntityJail(entity.world, target.posX, target.posY, target.posZ);
            jail.setPrisoner(target);
            entity.world.spawnEntity(jail);
        }
    }
}
