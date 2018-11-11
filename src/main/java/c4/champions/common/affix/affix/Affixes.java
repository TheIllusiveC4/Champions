package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;

public class Affixes {

    public static AffixBase shielding;
    public static AffixBase molten;

    public static void registerAffixes() {
        molten = new AffixMolten();
        shielding = new AffixShielding();
    }
}
