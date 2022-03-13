package top.theillusivec4.champions.common.affix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class DampeningAffix extends BasicAffix {
    public DampeningAffix() {
        super("dampening", AffixCategory.DEFENSE);
    }

    @Override
    public float onHurt(IChampion champion, DamageSource source, float amount, float newAmount) {
        return source instanceof IndirectEntityDamageSource ? newAmount * (float) (1.0F
                - ChampionsConfig.dampenedDamageReduction) : newAmount;
    }
}
