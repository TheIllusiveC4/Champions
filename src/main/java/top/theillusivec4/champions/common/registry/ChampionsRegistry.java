package top.theillusivec4.champions.common.registry;

import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.champions.Champions;

@ObjectHolder(Champions.MODID)
public class ChampionsRegistry {

  @ObjectHolder(RegistryReference.EGG)
  public static final Item EGG;

  @ObjectHolder(RegistryReference.RANK)
  public static final BasicParticleType RANK;

  @ObjectHolder(RegistryReference.JAILED)
  public static final Effect JAILED;

  static {
    RANK = null;
    EGG = null;
    JAILED = null;
  }
}
