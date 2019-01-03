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

package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public interface IAffix {

    String getIdentifier();

    AffixCategory getCategory();

    void onInitialSpawn(EntityLiving entity, IChampionship cap);

    void onSpawn(EntityLiving entity, IChampionship cap);

    void onUpdate(EntityLiving entity, IChampionship cap);

    void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount,
                  LivingAttackEvent evt);

    void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt);

    float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount);

    float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount);

    void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt);

    void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt);

    boolean canApply(EntityLiving entity);

    boolean isCompatibleWith(IAffix affix);

    int getTier();
}
