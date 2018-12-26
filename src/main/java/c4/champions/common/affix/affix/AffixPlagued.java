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

import c4.champions.common.affix.Affixes;
import c4.champions.common.affix.IAffix;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;

public class AffixPlagued extends AffixBase {

    public AffixPlagued() {
        super("plagued", AffixCategory.OFFENSE);
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        if (!entity.world.isRemote) {
            List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getEntityBoundingBox
                    ().grow(3));

            for (Entity entity1 : list) {

                if (entity1 instanceof EntityLivingBase) {
                    EntityLivingBase target = (EntityLivingBase)entity1;

                    if (canEntityBeInfected(entity, target)) {
                        target.addPotionEffect(new PotionEffect(ChampionsRegistry.plague, ConfigHandler.affix.plagued.duration));
                    }
                }
            }
        }
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {
        target.addPotionEffect(new PotionEffect(ChampionsRegistry.plague, ConfigHandler.affix.plagued.duration));
    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {

        if (source == DamageSource.WITHER) {
            evt.setCanceled(true);
        }
    }

    public static boolean canEntityBeInfected(EntityLivingBase host, EntityLivingBase target) {
        return host.world.rayTraceBlocks(new Vec3d(host.posX, host.posY + host.height / 2.0f, host.posZ), new
                Vec3d(target.posX, target.posY + target.height / 2.0f, target.posZ),
                true, true, false) == null;
    }
}
