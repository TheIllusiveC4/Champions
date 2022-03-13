package top.theillusivec4.champions.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Champions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChampionsRenderer {

  @SubscribeEvent
  public static void rendererRegistering(final EntityRenderersEvent.RegisterRenderers evt) {
    evt.registerEntityRenderer(ChampionsRegistry.ARCTIC_BULLET,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0x42F5E3));
    evt.registerEntityRenderer(ChampionsRegistry.ENKINDLING_BULLET,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0xFC5A03));
  }
}
