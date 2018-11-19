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

package c4.champions.common.affix;

import c4.champions.common.affix.affix.*;
import c4.champions.common.affix.core.AffixBase;

public class Affixes {

    public static AffixBase shielding;
    public static AffixBase molten;
    public static AffixBase reflecting;
    public static AffixBase vortex;
    public static AffixBase dampening;
    public static AffixBase horde;
    public static AffixBase infested;
    public static AffixBase jailer;
    public static AffixBase arctic;
    public static AffixBase desecrator;
    public static AffixBase hasty;

    public static void registerAffixes() {
        molten = new AffixMolten();
        shielding = new AffixShielding();
        reflecting = new AffixReflecting();
        vortex = new AffixVortex();
        dampening = new AffixDampening();
        horde = new AffixHorde();
        infested = new AffixInfested();
        jailer = new AffixJailer();
        arctic = new AffixArctic();
        desecrator = new AffixDesecrator();
        hasty = new AffixHasty();
    }
}
