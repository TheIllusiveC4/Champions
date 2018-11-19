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

public class AffixFilter {

    private final String identifier;
    private final boolean enabled;
    private final String[] entityBlacklist;
    private final String[] alwaysOnEntity;
    private final int tier;

    public AffixFilter(String identifier, boolean enabled, String[] entityBlacklist, String[] alwaysOnEntity, int
            tier) {
        this.identifier = identifier;
        this.enabled = enabled;
        this.entityBlacklist = entityBlacklist;
        this.alwaysOnEntity = alwaysOnEntity;
        this.tier = tier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String[] getEntityBlacklist() {
        return entityBlacklist;
    }

    public String[] getAlwaysOnEntity() {
        return alwaysOnEntity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTier() {
        return tier;
    }
}
