package top.theillusivec4.champions.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.client.ClientEventHandler;
import top.theillusivec4.champions.client.config.ClientChampionsConfig;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class HUDHelper {

  private static final ResourceLocation GUI_BAR_TEXTURES = new ResourceLocation(
    "textures/gui/bars.png");
  private static final ResourceLocation GUI_STAR = new ResourceLocation(Champions.MODID,
    "textures/gui/staricon.png");

  public static boolean renderHealthBar(PoseStack matrixStack, final LivingEntity livingEntity) {
    return ChampionCapability.getCapability(livingEntity).map(champion -> {
      IChampion.Client clientChampion = champion.getClient();
      return clientChampion.getRank().map(rank -> {
        int num = rank.getA();
        Set<String> affixSet = clientChampion.getAffixes().stream().map(IAffix::getIdentifier)
          .collect(Collectors.toSet());

        if (num > 0 || affixSet.size() > 0) {
          Minecraft client = Minecraft.getInstance();
          int i = client.getWindow().getGuiScaledWidth();
          int k = i / 2 - 91;
          int j = 21;
          int xOffset = ClientChampionsConfig.hudXOffset;
          int yOffset = ClientChampionsConfig.hudYOffset;
          int color = rank.getB();
          float r = (float) ((color >> 16) & 0xFF) / 255f;
          float g = (float) ((color >> 8) & 0xFF) / 255f;
          float b = (float) ((color) & 0xFF) / 255f;

          RenderSystem.defaultBlendFunc();
          RenderSystem.setShaderColor(r, g, b, 1.0F);
          RenderSystem.enableBlend();
          RenderSystem.setShader(GameRenderer::getPositionTexShader);
          RenderSystem.setShaderTexture(0, GUI_BAR_TEXTURES);
          ClientEventHandler.startX = xOffset + k;
          ClientEventHandler.startY = yOffset + 1;

          GuiComponent.blit(matrixStack, xOffset + k, yOffset + j, 0, 60, 182, 5, 256, 256);
          int healthOffset =
            (int) ((livingEntity.getHealth() / livingEntity.getMaxHealth()) * 183.0F);

          if (healthOffset > 0) {
            GuiComponent.blit(matrixStack, xOffset + k, yOffset + j, 0, 65, healthOffset, 5, 256,
              256);
          }

          RenderSystem.setShaderTexture(0, GUI_STAR);

          if (num <= 18) {
            int startStarsX = xOffset + i / 2 - 5 - 5 * (num - 1);

            for (int tier = 0; tier < num; tier++) {
              GuiComponent.blit(matrixStack, startStarsX, yOffset + 1, 0, 0, 9, 9, 9, 9);
              startStarsX += 10;
            }
          } else {
            int startStarsX = xOffset + i / 2 - 5;
            String count = "x" + num;
            GuiComponent.blit(matrixStack, startStarsX - client.font.width(count) / 2,
              yOffset + 1, 0, 0, 9, 9, 9, 9);
            client.font.drawShadow(matrixStack, count,
              startStarsX + 10 - client.font.width(count) / 2.0F, yOffset + 2,
              16777215);
          }
          Component customName = livingEntity.getCustomName();
          String name;

          if (customName == null) {
            name = new TranslatableComponent("rank.champions.title." + num).getString();
            name += " " + livingEntity.getName().getString();
          } else {
            name = customName.getString();
          }
          client.font.drawShadow(matrixStack, name,
            xOffset + (float) (i / 2 - client.font.width(name) / 2),
            yOffset + (float) (j - 9), color);
          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          StringBuilder builder = new StringBuilder();

          for (String affix : affixSet) {
            builder.append(
              new TranslatableComponent("affix." + Champions.MODID + "." + affix).getString());
            builder.append(" ");
          }
          String affixes = builder.toString().trim();
          client.font.drawShadow(matrixStack, affixes,
            xOffset + (float) (i / 2 - client.font.width(affixes) / 2),
            yOffset + (float) (j + 6), 16777215);
          RenderSystem.disableBlend();
          return true;
        }
        return false;
      }).orElse(false);
    }).orElse(false);
  }
}
