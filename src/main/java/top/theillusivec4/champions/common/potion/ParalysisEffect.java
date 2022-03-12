package top.theillusivec4.champions.common.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class ParalysisEffect extends MobEffect
{
  public ParalysisEffect() {
    super(MobEffectCategory.HARMFUL, 0xff5733);
    this.setRegistryName(RegistryReference.PARALYSIS);
    this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
        "2e1d5db6-1bb0-49a7-907d-2e5531d04736", 1, AttributeModifier.Operation.ADDITION);
  }
}
