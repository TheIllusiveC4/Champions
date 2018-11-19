package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.RankManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class AffixInfested extends AffixBase {

    public AffixInfested() {
        super("infested", AffixCategory.OFFENSE);
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

        if (!entity.world.isRemote) {
            EntitySilverfish silverfish = spawnSilverfish(entity.world, entity.getPosition());
            silverfish.setAttackTarget(target);
        }
    }

    @Override
    public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {

        if (!entity.world.isRemote) {
            EntitySilverfish silverfish = spawnSilverfish(entity.world, entity.getPosition());

            if (source.getTrueSource() instanceof EntityLivingBase) {
                silverfish.setRevengeTarget((EntityLivingBase) source.getTrueSource());
            }
        }
        return newAmount;
    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {

        if (!entity.world.isRemote) {
            int num = entity.getRNG().nextInt(cap.getRank().getTier() * 2) + 1;
            EntityLivingBase target = null;
            if (source.getTrueSource() instanceof EntityLivingBase) {
                target = (EntityLivingBase) source.getTrueSource();
            }
            for (int i = 0; i < num; i++) {
                EntitySilverfish silverfish = spawnSilverfish(entity.world, entity.getPosition());

                if (target != null) {
                    silverfish.setRevengeTarget(target);
                }
            }
        }
    }

    private EntitySilverfish spawnSilverfish(World world, BlockPos pos) {
        EntitySilverfish entitysilverfish = new EntitySilverfish(world);
        entitysilverfish.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
        IChampionship chp = CapabilityChampionship.getChampionship(entitysilverfish);

        if (chp != null) {
            chp.setRank(RankManager.getEmptyRank());
        }
        world.spawnEntity(entitysilverfish);
        entitysilverfish.spawnExplosionParticle();
        return entitysilverfish;
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return !(entity instanceof EntitySilverfish);
    }
}
