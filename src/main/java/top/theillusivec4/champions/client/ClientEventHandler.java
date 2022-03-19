package top.theillusivec4.champions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.client.util.HUDHelper;
import top.theillusivec4.champions.client.util.MouseHelper;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ClientEventHandler {

  @SubscribeEvent
  public void renderChampionHealth(RenderGameOverlayEvent.BossInfo.Pre evt) {

    if (ChampionsConfig.showHud) {
      Minecraft mc = Minecraft.getInstance();
      Optional<LivingEntity> livingEntity =
          MouseHelper.getMouseOverChampion(mc, evt.getPartialTicks());
      livingEntity.ifPresent(entity -> {
        PoseStack matrixStack = evt.getMatrixStack();

        if (HUDHelper.renderHealthBar(matrixStack, entity)) {
          evt.setCanceled(true);
          MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(matrixStack,
              new RenderGameOverlayEvent(matrixStack, evt.getPartialTicks(), mc.getWindow()),
              ElementType.BOSSINFO));
        }
      });
    }
  }
}
