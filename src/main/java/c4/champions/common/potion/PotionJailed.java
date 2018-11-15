package c4.champions.common.potion;

import c4.champions.Champions;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class PotionJailed extends Potion {

    public PotionJailed() {
        super(true, 0);
        this.setPotionName(Champions.MODID + ".jailed");
        this.setRegistryName(Champions.MODID, "jailed");
        this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,
                "2e1d5db6-1bb0-49a7-907d-2e5531d04736", 1, 0);
    }
}
