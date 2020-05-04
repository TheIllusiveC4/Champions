package top.theillusivec4.champions.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.rank.Rank;

public class HUDHelper {

  private static final ResourceLocation GUI_BAR_TEXTURES = new ResourceLocation(
      "textures/gui/bars.png");
  private static final ResourceLocation GUI_STAR = new ResourceLocation(Champions.MODID,
      "textures/gui/staricon.png");

  public static void renderHealthBar(final LivingEntity livingEntity) {
    ChampionCapability.getCapability(livingEntity).ifPresent(champion -> {
      Rank rank = champion.getRank();

      if (rank != null) {
        int num = rank.getTier();

        if (num > 0) {
          Minecraft client = Minecraft.getInstance();
          int i = client.mainWindow.getScaledWidth();
          int k = i / 2 - 91;
          int j = 21;
          int color = champion.getRank().getDefaultColor();
          float r = (float) ((color >> 16) & 0xFF) / 255f;
          float g = (float) ((color >> 8) & 0xFF) / 255f;
          float b = (float) ((color) & 0xFF) / 255f;
          GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
              GlStateManager.DestFactor.ZERO);
          GlStateManager.enableBlend();
          GlStateManager.color4f(r, g, b, 1.0F);
          client.getTextureManager().bindTexture(GUI_BAR_TEXTURES);
          renderHealthBar(k, j, livingEntity.getHealth() / livingEntity.getMaxHealth());
          client.getTextureManager().bindTexture(GUI_STAR);

          if (num <= 18) {
            int startStarsX = i / 2 - 5 - 5 * (num - 1);

            for (int tier = 0; tier < num; tier++) {
              AbstractGui.blit(startStarsX, 1, 0, 0, 9, 9, 9, 9);
              startStarsX += 10;
            }
          } else {
            int startStarsX = i / 2 - 5;
            String count = "x" + num;
            AbstractGui
                .blit(startStarsX - client.fontRenderer.getStringWidth(count) / 2, 1, 0, 0, 9, 9, 9,
                    9);
            client.fontRenderer.drawStringWithShadow(count, startStarsX + 10 - client.fontRenderer.getStringWidth(count) / 2.0F, 2, 16777215);
          }
          String name = new TranslationTextComponent("rank.champions.title." + num).getString();
          name += " " + livingEntity.getName().getString();
          client.fontRenderer.drawStringWithShadow(name, (float) (i / 2 - client.fontRenderer.getStringWidth(name) / 2), (float) (j - 9),
              color);
          GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
          StringBuilder builder = new StringBuilder();

          for (String affix : champion.getAffixIds()) {
            builder.append(new TranslationTextComponent("affix." + Champions.MODID + "." + affix).getString());
            builder.append(" ");
          }
          String affixes = builder.toString().trim();
          client.fontRenderer.drawStringWithShadow(affixes, (float) (i / 2 - client.fontRenderer.getStringWidth(affixes) / 2), (float) (j + 6),
              16777215);
          GlStateManager.disableBlend();
        }
      }
    });
  }

  private static void renderHealthBar(int x, int y, float percent) {
    AbstractGui.blit(x, y, 0, 60, 182, 5, 256, 256);
    int i = (int) (percent * 183.0F);

    if (i > 0) {
      AbstractGui.blit(x, y, 0, 65, i, 5, 256, 256);
    }
  }
}
