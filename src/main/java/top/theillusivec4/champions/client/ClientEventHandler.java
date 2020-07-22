package top.theillusivec4.champions.client;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.client.util.HUDHelper;
import top.theillusivec4.champions.client.util.MouseHelper;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ClientEventHandler {

  @SubscribeEvent
  public void renderChampionHealth(RenderGameOverlayEvent.Pre evt) {

    if (evt.getType() == ElementType.BOSSHEALTH && ChampionsConfig.showHud) {
      Optional<LivingEntity> livingEntity = MouseHelper
          .getMouseOverChampion(Minecraft.getInstance(), evt.getPartialTicks());
      livingEntity.ifPresent(entity -> {
        if (HUDHelper.renderHealthBar(evt.getMatrixStack(), entity)) {
          evt.setCanceled(true);
        }
      });
    }
  }
}
