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
import c4.champions.common.capability.IChampionship;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixScrapper extends AffixBase {

    public AffixScrapper() {
        super("scrapper", AffixCategory.OFFENSE);
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

        if (!entity.world.isRemote && entity.getRNG().nextFloat() < 0.4f) {
            target.addPotionEffect(new PotionEffect(ChampionsRegistry.injured, 200, 0));
        }
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return !(entity instanceof EntityCreeper);
    }
}
