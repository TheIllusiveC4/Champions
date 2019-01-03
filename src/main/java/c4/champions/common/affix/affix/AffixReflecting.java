/*
 * Copyright (C) 2018-2019  C4
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
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
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
            float min = (float)ConfigHandler.affix.reflecting.minimumPerc;
            source.damageType = "reflecting";

            if (source instanceof EntityDamageSource) {
                ((EntityDamageSource) source).setIsThornsDamage();
            }
            entityLivingBase.attackEntityFrom(source, amount * (entity.getRNG().nextFloat() *
                    (float)(ConfigHandler.affix.reflecting.maximumPerc - min) + min));
        }
        return newAmount;
    }
}
