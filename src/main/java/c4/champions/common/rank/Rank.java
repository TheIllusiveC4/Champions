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

package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class Rank {

    private static final UUID GROWTH = UUID.fromString("e8471143-fb3f-4ec0-b26f-b83794d08f61");
    private static final String GROWTH_NAME = "Growth modifier";

    private final int tier;
    private final int color;
    private final int affixes;
    private final int growthFactor;
    private final float chance;

    public Rank() {
        this(0, 0, 0, 0, 0);
    }

    public Rank(int tier, int affixes, int growthFactor, float chance, int color) {
        this.tier = tier;
        this.affixes = affixes;
        this.growthFactor = growthFactor;
        this.chance = chance;
        this.color = color;
    }

    public int getTier() {
        return tier;
    }

    public int getColor() {
        String[] colors = ConfigHandler.client.colors;
        if (colors.length >= tier && tier > 0) {
            String s = colors[tier - 1];

            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Non-integer in color config! " + s);
                return color;
            }
        }
        return color;
    }

    public int getAffixes() {
        return affixes;
    }

    public int getGrowthFactor() {
        return growthFactor;
    }

    public float getChance() {
        return chance;
    }

    public void applyGrowth(EntityLivingBase entityLivingBase) {
        applyGrowth(entityLivingBase, SharedMonsterAttributes.MAX_HEALTH, ConfigHandler.growth.health, 2);
        entityLivingBase.setHealth(entityLivingBase.getMaxHealth());
        applyGrowth(entityLivingBase, SharedMonsterAttributes.ATTACK_DAMAGE, ConfigHandler.growth.attackDamage, 2);
        applyGrowth(entityLivingBase, SharedMonsterAttributes.ARMOR, ConfigHandler.growth.armor, 0);
        applyGrowth(entityLivingBase, SharedMonsterAttributes.ARMOR_TOUGHNESS, ConfigHandler.growth.armorToughness, 0);
        applyGrowth(entityLivingBase, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, ConfigHandler.growth.knockbackResist, 0);
    }

    private void applyGrowth(EntityLivingBase entityLivingBase, IAttribute attribute, double amount,
                             int operation) {
        IAttributeInstance att = entityLivingBase.getEntityAttribute(attribute);
        if (att != null) {
            double oldMax = entityLivingBase.getEntityAttribute(attribute).getBaseValue();
            double newMax = 0;
            amount *= growthFactor;

            switch (operation) {
                case 0: newMax = oldMax + amount; break;
                case 1: newMax = oldMax * amount; break;
                case 2: newMax = oldMax * (1 + amount); break;
            }
            entityLivingBase.getEntityAttribute(attribute).setBaseValue(newMax);
        }
    }
}
