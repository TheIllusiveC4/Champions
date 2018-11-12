package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.vecmath.Vector3d;

public class AffixVortex extends AffixBase {

    public AffixVortex() {
        super("vortex", AffixCategory.CC);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        EntityLivingBase target = entity.getAttackTarget();

        if (target != null) {
            double x = entity.posX;
            double y = entity.posY;
            double z = entity.posZ;
            float strength = 0.07f;
            Vector3d vec = new Vector3d(x, y, z);
            vec.sub(new Vector3d(target.posX, target.posY, target.posZ));
            vec.normalize();
            vec.scale(strength);
            target.motionX += vec.x;
            target.motionY += vec.y;
            target.motionZ += vec.z;

            if (target instanceof EntityPlayer) {
                ((EntityPlayer) target).velocityChanged = true;
            }
        }
    }
}
