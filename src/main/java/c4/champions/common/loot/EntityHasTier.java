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

package c4.champions.common.loot;

import c4.champions.Champions;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;

import javax.annotation.Nonnull;
import java.util.Random;

public class EntityHasTier implements EntityProperty {

    private final int tier;

    public EntityHasTier(int tierIn)
    {
        this.tier = tierIn;
    }

    public boolean testProperty(@Nonnull Random random, @Nonnull Entity entityIn) {

        if (ChampionHelper.isValidChampion(entityIn)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityIn);
            return chp != null && ChampionHelper.isElite(chp.getRank()) && chp.getRank().getTier() >= tier;
        }
        return false;
    }

    public static class Serializer extends EntityProperty.Serializer<EntityHasTier> {

        public Serializer() {
            super(new ResourceLocation(Champions.MODID, "tier"), EntityHasTier.class);
        }

        @Nonnull
        public JsonElement serialize(@Nonnull EntityHasTier property, @Nonnull JsonSerializationContext serializationContext) {
            return new JsonPrimitive(property.tier);
        }

        @Nonnull
        public EntityHasTier deserialize(@Nonnull JsonElement element, @Nonnull JsonDeserializationContext
                deserializationContext) {
            return new EntityHasTier(JsonUtils.getInt(element, "tier"));
        }
    }
}