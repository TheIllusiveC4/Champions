package top.theillusivec4.champions.client.renderer;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ChampionsRenderer {

  public static void register() {
    RenderingRegistry.registerEntityRenderingHandler(ChampionsRegistry.ARCTIC_BULLET,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0x42F5E3));
    RenderingRegistry.registerEntityRenderingHandler(ChampionsRegistry.ENKINDLING_BULLET,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0xFC5A03));
  }
}
