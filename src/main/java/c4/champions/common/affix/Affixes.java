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

import c4.champions.common.affix.affix.AffixAdaptable;
import c4.champions.common.affix.affix.AffixArctic;
import c4.champions.common.affix.affix.AffixCinder;
import c4.champions.common.affix.affix.AffixDampening;
import c4.champions.common.affix.affix.AffixDesecrator;
import c4.champions.common.affix.affix.AffixHasty;
import c4.champions.common.affix.affix.AffixInfested;
import c4.champions.common.affix.affix.AffixJailer;
import c4.champions.common.affix.affix.AffixKnockback;
import c4.champions.common.affix.affix.AffixLively;
import c4.champions.common.affix.affix.AffixMolten;
import c4.champions.common.affix.affix.AffixPlagued;
import c4.champions.common.affix.affix.AffixReflecting;
import c4.champions.common.affix.affix.AffixScrapper;
import c4.champions.common.affix.affix.AffixShielding;
import c4.champions.common.affix.affix.AffixVortex;
import c4.champions.common.affix.core.AffixBase;

public class Affixes {

    public static AffixBase shielding;
    public static AffixBase molten;
    public static AffixBase reflecting;
    public static AffixBase vortex;
    public static AffixBase dampening;
    public static AffixBase infested;
    public static AffixBase jailer;
    public static AffixBase arctic;
    public static AffixBase desecrator;
    public static AffixBase hasty;
    public static AffixBase lively;
    public static AffixBase plagued;
    public static AffixBase adaptable;
    public static AffixBase scrapper;
    public static AffixBase knockback;
    public static AffixBase cinder;

    public static void registerAffixes() {
        molten = new AffixMolten();
        shielding = new AffixShielding();
        reflecting = new AffixReflecting();
        vortex = new AffixVortex();
        dampening = new AffixDampening();
        infested = new AffixInfested();
        jailer = new AffixJailer();
        arctic = new AffixArctic();
        desecrator = new AffixDesecrator();
        hasty = new AffixHasty();
        lively = new AffixLively();
        plagued = new AffixPlagued();
        adaptable = new AffixAdaptable();
        scrapper = new AffixScrapper();
        knockback = new AffixKnockback();
        cinder = new AffixCinder();
    }
}
