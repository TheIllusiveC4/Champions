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
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;

import javax.annotation.Nonnull;
import java.util.Random;

public class EntityIsChampion implements EntityProperty {

    private final int tier;
    private final int minTier;
    private final int maxTier;

    public EntityIsChampion(int tierIn, int minTierIn, int maxTierIn) {
        this.tier = tierIn;
        this.minTier = minTierIn;
        this.maxTier = maxTierIn;
    }

    public boolean testProperty(@Nonnull Random random, @Nonnull Entity entityIn) {

        if (ChampionHelper.isValidChampion(entityIn)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityIn);

            if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                int tier = chp.getRank().getTier();

                if (this.tier == 0) {
                    return (minTier == 0 || tier >= minTier) && (maxTier == 0 || tier <= maxTier);
                } else {
                    return this.tier == tier;
                }
            }
        }
        return false;
    }

    public static class Serializer extends EntityProperty.Serializer<EntityIsChampion> {

        public Serializer() {
            super(new ResourceLocation(Champions.MODID, "is_champion"), EntityIsChampion.class);
        }

        @Nonnull
        public JsonElement serialize(@Nonnull EntityIsChampion property, @Nonnull JsonSerializationContext serializationContext) {
            JsonObject json = new JsonObject();
            json.addProperty("tier", property.tier);
            json.addProperty("min_tier", property.minTier);
            json.addProperty("max_tier", property.maxTier);
            return json;
        }

        @Nonnull
        public EntityIsChampion deserialize(@Nonnull JsonElement element, @Nonnull JsonDeserializationContext
                deserializationContext) {
            JsonObject json = element.getAsJsonObject();
            int tier = JsonUtils.getInt(json, "tier", 0);
            int minTier = JsonUtils.getInt(json, "min_tier", 0);
            int maxTier = JsonUtils.getInt(json, "max_tier", 0);
            return new EntityIsChampion(tier, minTier, maxTier);
        }
    }
}