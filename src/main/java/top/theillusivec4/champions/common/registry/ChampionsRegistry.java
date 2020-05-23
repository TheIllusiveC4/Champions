package top.theillusivec4.champions.common.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.entity.AbstractBulletEntity;

@ObjectHolder(Champions.MODID)
public class ChampionsRegistry {

  @ObjectHolder(RegistryReference.EGG)
  public static final Item EGG;

  @ObjectHolder(RegistryReference.RANK)
  public static final BasicParticleType RANK;

  @ObjectHolder(RegistryReference.PARALYSIS)
  public static final Effect PARALYSIS;

  @ObjectHolder(RegistryReference.WOUND)
  public static final Effect WOUND;

  @ObjectHolder(RegistryReference.ARCTIC_BULLET)
  public static final EntityType<? extends AbstractBulletEntity> ARCTIC_BULLET;

  @ObjectHolder(RegistryReference.ENKINDLING_BULLET)
  public static final EntityType<? extends AbstractBulletEntity> ENKINDLING_BULLET;

  static {
    RANK = null;
    EGG = null;
    PARALYSIS = null;
    WOUND = null;
    ARCTIC_BULLET = null;
    ENKINDLING_BULLET = null;
  }
}
