package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Random;

public class AffixDampening extends AffixBase {

    public AffixDampening() {
        super("dampening", AffixCategory.DEFENSE);
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {

        if (source.isProjectile()) {

            if (source.getImmediateSource() != null && !(source.getImmediateSource() instanceof EntityLivingBase)) {
                Entity sourceEntity = source.getImmediateSource();
                Random rand = entity.getRNG();
                for (int i = 0; i < 10; ++i) {
                    sourceEntity.world.spawnParticle(EnumParticleTypes.PORTAL, sourceEntity.posX + (rand.nextDouble() - 0.5D) *
                            (double)sourceEntity.width, sourceEntity.posY + rand.nextDouble() * (double)sourceEntity.height - 0.25D,
                            sourceEntity.posZ + (rand.nextDouble() - 0.5D) * (double)sourceEntity.width,
                            (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
                }
                sourceEntity.world.playSound(null, sourceEntity.prevPosX, sourceEntity.prevPosY, sourceEntity.prevPosZ,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, sourceEntity.getSoundCategory(), 1.0F, 1.0F);
                sourceEntity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
                sourceEntity.setDead();
            }
            evt.setCanceled(true);
        }
    }
}
