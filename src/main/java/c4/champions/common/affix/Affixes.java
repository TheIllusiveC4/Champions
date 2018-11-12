package c4.champions.common.affix;

import c4.champions.common.affix.affix.*;
import c4.champions.common.affix.core.AffixBase;

public class Affixes {

    public static AffixBase shielding;
    public static AffixBase molten;
    public static AffixBase blasting;
    public static AffixBase reflecting;
    public static AffixBase vortex;

    public static void registerAffixes() {
        molten = new AffixMolten();
        shielding = new AffixShielding();
        blasting = new AffixBlasting();
        reflecting = new AffixReflecting();
        vortex = new AffixVortex();
    }
}
