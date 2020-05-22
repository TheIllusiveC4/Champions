package top.theillusivec4.champions.common.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import top.theillusivec4.champions.common.registry.RegistryReference;

public class WoundEffect extends Effect {

  public WoundEffect() {
    super(EffectType.HARMFUL, 0x8d0037);
    this.setRegistryName(RegistryReference.WOUND);
  }
}
