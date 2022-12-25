package top.theillusivec4.champions.client;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

  @SubscribeEvent
  public void renderChampionHealth(final RenderGameOverlayEvent.BossInfo evt) {

    if (ChampionsOverlay.isRendering) {
      evt.setCanceled(true);
      ForgeHooksClient.renderBossEventPost(evt.getMatrixStack(), evt.getWindow());
    }
  }
}
