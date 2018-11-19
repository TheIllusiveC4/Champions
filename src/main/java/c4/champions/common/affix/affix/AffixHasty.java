package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import java.util.UUID;

public class AffixHasty extends AffixBase {

    private static final AttributeModifier MODIFIER = new AttributeModifier(UUID.fromString
            ("f6846f61-3622-4b01-b618-7e47612579ce"), "Hasty affix", 0.5, 0);

    public AffixHasty() {
        super("hasty", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        speed.setBaseValue(speed.getBaseValue() * 1.5f);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null;
    }
}
