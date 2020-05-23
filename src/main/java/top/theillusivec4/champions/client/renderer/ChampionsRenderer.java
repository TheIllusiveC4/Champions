package top.theillusivec4.champions.client.renderer;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import top.theillusivec4.champions.common.entity.ArcticBulletEntity;
import top.theillusivec4.champions.common.entity.EnkindlingBulletEntity;

public class ChampionsRenderer {

  public static void register() {
    RenderingRegistry.registerEntityRenderingHandler(ArcticBulletEntity.class,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0x42F5E3));
    RenderingRegistry.registerEntityRenderingHandler(EnkindlingBulletEntity.class,
        (renderManager) -> new ColorizedBulletRenderer(renderManager, 0xFC5A03));
  }
}
