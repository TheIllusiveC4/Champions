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

package c4.champions.common.potion;

import c4.champions.Champions;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class PotionJailed extends Potion {

    public PotionJailed() {
        super(true, 0);
        this.setPotionName(Champions.MODID + ".jailed");
        this.setRegistryName(Champions.MODID, "jailed");
        this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,
                "2e1d5db6-1bb0-49a7-907d-2e5531d04736", 1, 0);
    }
}
