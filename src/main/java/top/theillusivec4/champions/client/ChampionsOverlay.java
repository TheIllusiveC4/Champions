package top.theillusivec4.champions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import top.theillusivec4.champions.client.util.HUDHelper;
import top.theillusivec4.champions.client.util.MouseHelper;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ChampionsOverlay implements IIngameOverlay {

  public static boolean isRendering = false;
  public static int startX = 0;
  public static int startY = 0;

  @Override
  public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width,
                     int height) {

    if (ChampionsConfig.showHud) {
      Minecraft mc = Minecraft.getInstance();
      Optional<LivingEntity> livingEntity =
        MouseHelper.getMouseOverChampion(mc, partialTick);
      livingEntity.ifPresent(entity -> isRendering = HUDHelper.renderHealthBar(poseStack, entity));

      if (livingEntity.isEmpty()) {
        isRendering = false;
      }
    }
  }
}
