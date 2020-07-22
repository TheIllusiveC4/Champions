package top.theillusivec4.champions.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.client.config.ClientChampionsConfig;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class HUDHelper {

  private static final ResourceLocation GUI_BAR_TEXTURES = new ResourceLocation(
      "textures/gui/bars.png");
  private static final ResourceLocation GUI_STAR = new ResourceLocation(Champions.MODID,
      "textures/gui/staricon.png");

  @SuppressWarnings("deprecation")
  public static boolean renderHealthBar(MatrixStack matrixStack, final LivingEntity livingEntity) {
    return ChampionCapability.getCapability(livingEntity).map(champion -> {
      IChampion.Client clientChampion = champion.getClient();
      return clientChampion.getRank().map(rank -> {
        int num = rank.getA();
        Set<String> affixSet = clientChampion.getAffixes();

        if (num > 0 || affixSet.size() > 0) {
          Minecraft client = Minecraft.getInstance();
          int i = client.getMainWindow().getScaledWidth();
          int k = i / 2 - 91;
          int j = 21;
          int xOffset = ClientChampionsConfig.hudXOffset;
          int yOffset = ClientChampionsConfig.hudYOffset;
          int color = rank.getB();
          float r = (float) ((color >> 16) & 0xFF) / 255f;
          float g = (float) ((color >> 8) & 0xFF) / 255f;
          float b = (float) ((color) & 0xFF) / 255f;
          RenderSystem.defaultBlendFunc();
          RenderSystem.color4f(r, g, b, 1.0F);
          RenderSystem.enableBlend();
          client.getTextureManager().bindTexture(GUI_BAR_TEXTURES);
          renderHealthBar(matrixStack, xOffset + k, yOffset + j,
              livingEntity.getHealth() / livingEntity.getMaxHealth());
          client.getTextureManager().bindTexture(GUI_STAR);

          if (num <= 18) {
            int startStarsX = xOffset + i / 2 - 5 - 5 * (num - 1);

            for (int tier = 0; tier < num; tier++) {
              AbstractGui.blit(matrixStack, startStarsX, yOffset + 1, 0, 0, 9, 9, 9, 9);
              startStarsX += 10;
            }
          } else {
            int startStarsX = xOffset + i / 2 - 5;
            String count = "x" + num;
            AbstractGui
                .blit(matrixStack, startStarsX - client.fontRenderer.getStringWidth(count) / 2,
                    yOffset + 1, 0, 0, 9, 9, 9, 9);
            client.fontRenderer.drawStringWithShadow(matrixStack, count,
                startStarsX + 10 - client.fontRenderer.getStringWidth(count) / 2.0F, yOffset + 2,
                16777215);
          }
          ITextComponent customName = livingEntity.getCustomName();
          String name;

          if (customName == null) {
            name = new TranslationTextComponent("rank.champions.title." + num).getString();
            name += " " + livingEntity.getName().getString();
          } else {
            name = customName.getString();
          }
          client.fontRenderer.drawStringWithShadow(matrixStack, name,
              xOffset + (float) (i / 2 - client.fontRenderer.getStringWidth(name) / 2),
              yOffset + (float) (j - 9), color);
          RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
          StringBuilder builder = new StringBuilder();

          for (String affix : affixSet) {
            builder.append(
                new TranslationTextComponent("affix." + Champions.MODID + "." + affix).getString());
            builder.append(" ");
          }
          String affixes = builder.toString().trim();
          client.fontRenderer.drawStringWithShadow(matrixStack, affixes,
              xOffset + (float) (i / 2 - client.fontRenderer.getStringWidth(affixes) / 2),
              yOffset + (float) (j + 6), 16777215);
          RenderSystem.disableBlend();
          return true;
        }
        return false;
      }).orElse(false);
    }).orElse(false);
  }

  private static void renderHealthBar(MatrixStack matrixStack, int x, int y, float percent) {
    AbstractGui.blit(matrixStack, x, y, 0, 60, 182, 5, 256, 256);
    int i = (int) (percent * 183.0F);

    if (i > 0) {
      AbstractGui.blit(matrixStack, x, y, 0, 65, i, 5, 256, 256);
    }
  }
}
