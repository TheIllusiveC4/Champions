package top.theillusivec4.champions.common.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.entity.ArcticBulletEntity;
import top.theillusivec4.champions.common.entity.EnkindlingBulletEntity;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.loot.ChampionLootModifier;
import top.theillusivec4.champions.common.particle.RankParticle.RankFactory;
import top.theillusivec4.champions.common.potion.ParalysisEffect;
import top.theillusivec4.champions.common.potion.WoundEffect;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Champions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventsHandler {

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> evt) {
    evt.getRegistry().registerAll(new ChampionEggItem());
  }

  @SubscribeEvent
  public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> evt) {
    evt.getRegistry()
      .register(new SimpleParticleType(false).setRegistryName(RegistryReference.RANK));
  }

  @SubscribeEvent
  public static void registerParticleFactories(ParticleFactoryRegisterEvent evt) {
    Minecraft.getInstance().particleEngine.register(ChampionsRegistry.RANK, RankFactory::new);
  }

  @SubscribeEvent
  public static void registerEffects(RegistryEvent.Register<MobEffect> evt) {
    evt.getRegistry().registerAll(new ParalysisEffect(), new WoundEffect());
  }

  @SubscribeEvent
  public static void registerLoot(RegistryEvent.Register<GlobalLootModifierSerializer<?>> evt) {
    evt.getRegistry().register(
      new ChampionLootModifier.Serializer().setRegistryName(Champions.MODID, "champion_loot"));
  }

  @SubscribeEvent
  public static void registerEntities(RegistryEvent.Register<EntityType<?>> evt) {
    EntityType<?> arcticBullet = EntityType.Builder.of(
        (type, world) -> new ArcticBulletEntity(world), MobCategory.MISC)
      .sized(0.3125F, 0.3125F).setCustomClientFactory(
        (spawnEntity, world) -> new ArcticBulletEntity(world))
      .build(RegistryReference.ARCTIC_BULLET).setRegistryName(RegistryReference.ARCTIC_BULLET);

    EntityType<?> enkindlingBullet = EntityType.Builder.of(
        (type, world) -> new EnkindlingBulletEntity(world), MobCategory.MISC)
      .sized(0.3125F, 0.3125F).setCustomClientFactory(
        (spawnEntity, world) -> new EnkindlingBulletEntity(world))
      .build(RegistryReference.ENKINDLING_BULLET)
      .setRegistryName(RegistryReference.ENKINDLING_BULLET);

    evt.getRegistry().registerAll(arcticBullet, enkindlingBullet);
  }
}
