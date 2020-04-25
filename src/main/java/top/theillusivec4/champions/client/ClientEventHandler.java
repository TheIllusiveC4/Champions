package top.theillusivec4.champions.client;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.client.util.HUDHelper;
import top.theillusivec4.champions.client.util.MouseHelper;

public class ClientEventHandler {

  @SubscribeEvent
  public void renderChampionHealth(RenderGameOverlayEvent.Pre evt) {

    if (evt.getType() == ElementType.BOSSHEALTH) {
      Optional<LivingEntity> livingEntity = MouseHelper
          .getMouseOverChampion(Minecraft.getInstance(), evt.getPartialTicks());
      livingEntity.ifPresent(entity -> {
        HUDHelper.renderHealthBar(entity);
        evt.setCanceled(true);
      });
    }
  }
}
