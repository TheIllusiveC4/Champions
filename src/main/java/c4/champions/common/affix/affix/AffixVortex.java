/*
 * Copyright (C) 2018  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
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
            AffixNBT.Boolean vortex = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);

            if (!entity.world.isRemote) {

                if (entity.ticksExisted % 40 == 0) {
                    float chance = vortex.mode ? 0.7f : 0.4f;

                    if (entity.getRNG().nextFloat() < chance) {
                        vortex.mode = !vortex.mode;
                        vortex.saveData(entity);
                    }
                }

                if (vortex.mode) {
                    double x = entity.posX;
                    double y = entity.posY;
                    double z = entity.posZ;
                    float strength = 0.005f;
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
    }
}
