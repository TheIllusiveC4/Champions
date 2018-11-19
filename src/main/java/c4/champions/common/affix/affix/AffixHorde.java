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
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class AffixHorde extends AffixBase {

    public AffixHorde() {
        super("horde", AffixCategory.DEFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        int size = 2 + cap.getRank().getTier() * 2;

        for (int i = 0; i < size; i++) {
            ResourceLocation rl = EntityList.getKey(entity);
            if (rl != null) {
                Entity hordeEntity = EntityList.createEntityByIDFromName(rl, entity.world);

                if (hordeEntity != null) {

                    if (ChampionHelper.isValidChampion(hordeEntity)) {
                        IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving) hordeEntity);

                        if (chp != null) {
                            chp.setRank(RankManager.getEmptyRank());
                        }
                    }
                    hordeEntity.setPosition(entity.posX + entity.getRNG().nextInt(4) - 2, entity.posY,
                            entity.posZ + entity.getRNG().nextInt(4) - 2);
                    entity.world.spawnEntity(hordeEntity);
                }
            }
        }
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.isNonBoss();
    }
}
