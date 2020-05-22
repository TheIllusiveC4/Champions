package top.theillusivec4.champions.common.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class JailedEffect extends Effect {

  public JailedEffect() {
    super(EffectType.HARMFUL, 0xff5733);
    this.setRegistryName(RegistryReference.JAILED);
    this.addAttributesModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,
        "2e1d5db6-1bb0-49a7-907d-2e5531d04736", 1, Operation.ADDITION);
  }
}
