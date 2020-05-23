package top.theillusivec4.champions.common.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.theillusivec4.champions.common.entity.ArcticBulletEntity;
import top.theillusivec4.champions.common.entity.EnkindlingBulletEntity;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.particle.RankParticle.RankFactory;
import top.theillusivec4.champions.common.potion.ParalysisEffect;
import top.theillusivec4.champions.common.potion.WoundEffect;

@EventBusSubscriber(bus = Bus.MOD)
public class RegistryEventsHandler {

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> evt) {
    evt.getRegistry().registerAll(new ChampionEggItem());
  }

  @SubscribeEvent
  public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> evt) {
    evt.getRegistry()
        .register(new BasicParticleType(false).setRegistryName(RegistryReference.RANK));
  }

  @SubscribeEvent
  public static void registerParticleFactories(ParticleFactoryRegisterEvent evt) {
    Minecraft.getInstance().particles.registerFactory(ChampionsRegistry.RANK, RankFactory::new);
  }

  @SubscribeEvent
  public static void registerEffects(RegistryEvent.Register<Effect> evt) {
    evt.getRegistry().registerAll(new ParalysisEffect(), new WoundEffect());
  }

  @SubscribeEvent
  public static void registerEntities(RegistryEvent.Register<EntityType<?>> evt) {
    EntityType<?> arcticBullet = EntityType.Builder.<ArcticBulletEntity>create(
        (type, world) -> new ArcticBulletEntity(world), EntityClassification.MISC)
        .size(0.3125F, 0.3125F).setCustomClientFactory(
            (spawnEntity, world) -> new ArcticBulletEntity(world, spawnEntity.getPosX(),
                spawnEntity.getPosY(), spawnEntity.getPosZ(), 0, 0, 0))
        .build(RegistryReference.ARCTIC_BULLET).setRegistryName(RegistryReference.ARCTIC_BULLET);

    EntityType<?> enkindlingBullet = EntityType.Builder.<EnkindlingBulletEntity>create(
        (type, world) -> new EnkindlingBulletEntity(world), EntityClassification.MISC)
        .size(0.3125F, 0.3125F).setCustomClientFactory(
            (spawnEntity, world) -> new EnkindlingBulletEntity(world, spawnEntity.getPosX(),
                spawnEntity.getPosY(), spawnEntity.getPosZ(), 0, 0, 0))
        .build(RegistryReference.ENKINDLING_BULLET)
        .setRegistryName(RegistryReference.ENKINDLING_BULLET);

    evt.getRegistry().registerAll(arcticBullet, enkindlingBullet);
  }
}
