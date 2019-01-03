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

package c4.champions.common.affix.core;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

import java.util.Random;

public abstract class AffixBase implements IAffix {

    protected static final Random rand = new Random();

    private final String identifier;
    private final AffixCategory category;
    private final int tier;

    public AffixBase(String identifier, AffixCategory category) {
        this(identifier, category, 1);
    }

    public AffixBase(String identifier, AffixCategory category, int tier) {
        this.identifier = identifier;
        this.category = category;
        this.tier = tier;
        AffixRegistry.registerAffix(identifier, this);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AffixCategory getCategory() {
        return category;
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {

    }

    @Override
    public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return newAmount;
    }

    @Override
    public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return newAmount;
    }

    @Override
    public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {

    }

    @Override
    public void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt) {

    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return true;
    }

    @Override
    public boolean isCompatibleWith(IAffix affix) {
        return affix != this;
    }

    @Override
    public int getTier() {
        return tier;
    }

    public static boolean isValidAffixTarget(EntityLiving mob, EntityLivingBase target, boolean checkSight) {

        if (target == null || !target.isEntityAlive()) {
            return false;
        }

        if (checkSight && !mob.canEntityBeSeen(target)) {
            return false;
        }
        IAttributeInstance iattributeinstance = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        double targetRange = iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        targetRange = ConfigHandler.affix.abilityRange == 0 ? targetRange : Math.min(targetRange, ConfigHandler.affix
                .abilityRange);
        return mob.getDistance(target) <= targetRange;
    }
}
