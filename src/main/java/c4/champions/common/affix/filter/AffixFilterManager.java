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

package c4.champions.common.affix.filter;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.util.JsonUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AffixFilterManager {

    private static final Map<String, AffixFilter> FILTERS = Maps.newHashMap();
    private static final Map<String, Set<String>> ENTITY_AFFIX_MAP = Maps.newHashMap();

    @Nullable
    public static AffixFilter getAffixFilter(String identifier) {
        return FILTERS.get(identifier);
    }

    public static boolean hasAffixFilter(String identifier) {
        return getAffixFilter(identifier) != null;
    }

    public static boolean isValidAffix(AffixBase affix, EntityLiving entityLiving, int tier) {
        AffixFilter filter = getAffixFilter(affix.getIdentifier());
        boolean hasTier = affix.getTier() <= tier;

        if (filter != null) {
            hasTier = filter.getTier() <= tier;
            return filter.isEnabled() && hasTier && !isEntityBlacklisted(filter, entityLiving);
        }
        return hasTier;
    }

    public static boolean isEntityBlacklisted(@Nonnull AffixFilter filter, Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);

        if (rl != null) {

            for (String key : filter.getEntityBlacklist()) {

                if (key.equals(rl.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    public static Set<String> getPresetAffixesForEntity(Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);
        return rl != null ? ENTITY_AFFIX_MAP.getOrDefault(rl.toString(), Sets.newHashSet()) : Sets.newHashSet();
    }

    public static void readAffixFiltersFromJson() {
        AffixFilter[] filters = JsonUtil.fromJson(TypeToken.get(AffixFilter[].class), new File(Loader.instance()
                .getConfigDir(), Champions.MODID + "/affixes.json"), buildDefaultAffixFilters());

        for (AffixFilter filter : filters) {
            FILTERS.put(filter.getIdentifier(), filter);
            String[] alwaysOn = filter.getAlwaysOnEntity();

            if (alwaysOn.length > 0) {

                for (String entityName : alwaysOn) {
                    Set<String> affixes = ENTITY_AFFIX_MAP.getOrDefault(entityName, Sets.newHashSet());
                    affixes.add(filter.getIdentifier());
                    ENTITY_AFFIX_MAP.putIfAbsent(entityName, affixes);
                }
            }
        }
    }

    private static AffixFilter[] buildDefaultAffixFilters() {
        ImmutableList<AffixBase> affixes = AffixRegistry.getAllAffixes();
        List<AffixFilter> filters = Lists.newArrayList();

        for (AffixBase aff : affixes) {
            filters.add(new AffixFilter(aff.getIdentifier(), true, new String[]{}, new String[]{}, aff.getTier()));
        }
        AffixFilter[] arr = new AffixFilter[filters.size()];
        return filters.toArray(arr);
    }
}
