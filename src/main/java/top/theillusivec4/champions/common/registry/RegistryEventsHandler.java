package top.theillusivec4.champions.common.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import top.theillusivec4.champions.common.particles.RankParticle.RankFactory;

@EventBusSubscriber(bus = Bus.MOD)
public class RegistryEventsHandler {

  @SubscribeEvent
  public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> evt) {
    evt.getRegistry().register(new BasicParticleType(false).setRegistryName(RegistryReference.RANK));
  }

  @SubscribeEvent
  public static void registerParticleFactories(ParticleFactoryRegisterEvent evt) {
    Minecraft.getInstance().particles.registerFactory(ChampionsRegistry.RANK, RankFactory::new);
  }
}
