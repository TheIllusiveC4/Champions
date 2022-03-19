package top.theillusivec4.champions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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

        if (HUDHelper.renderHealthBar(matrixStack, entity) &&
            evt.getType() == ElementType.BOSSINFO) {
          evt.setCanceled(true);
          ForgeHooksClient.renderBossEventPost(matrixStack, mc.getWindow());
        }
      });
    }
  }
}
