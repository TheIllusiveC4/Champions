package top.theillusivec4.champions.common.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class WoundEffect extends MobEffect
{

  public WoundEffect() {
    super(MobEffectCategory.HARMFUL, 0x8d0037);
    this.setRegistryName(RegistryReference.WOUND);
  }
}
