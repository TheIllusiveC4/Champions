package top.theillusivec4.champions.common.integration.waila;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.client.ClientEventHandler;
import top.theillusivec4.champions.client.config.ClientChampionsConfig;

@SuppressWarnings("all")
public class WailaPlugin {

  public static void setup() {
    ModList modList = ModList.get();

    if (modList.isLoaded("jade")) {
      MinecraftForge.EVENT_BUS.addListener(WailaPlugin::onRenderJade);
    } else if (modList.isLoaded("wthit")) {
      MinecraftForge.EVENT_BUS.addListener(WailaPlugin::onRenderWhat);
    }
  }

  private static void onRenderJade(final WailaRenderEvent.Pre evt) {

    if (ClientChampionsConfig.enableWailaIntegration && ClientEventHandler.isRendering &&
      !jade(evt)) {
      Champions.LOGGER.error("Error invoking Jade plugin!");
    }
  }

  private static void onRenderWhat(final WailaRenderEvent.Pre evt) {

    if (ClientChampionsConfig.enableWailaIntegration && ClientEventHandler.isRendering &&
      !wthit(evt)) {
      Champions.LOGGER.error("Error invoking WTHIT plugin!");
    }
  }

  private static boolean wthit(final WailaRenderEvent.Pre evt) {
    try {
      Rectangle rect = (Rectangle) evt.getClass().getMethod("getPosition").invoke(evt);

      if (inBounds(rect.x, rect.y)) {
        rect.y += 40;
      }
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      return false;
    }
    return true;
  }

  private static boolean jade(final WailaRenderEvent.Pre evt) {
    try {
      Rect2i rect = (Rect2i) evt.getClass().getMethod("getRect").invoke(evt);
      int y = rect.getY();

      if (inBounds(rect.getX(), y)) {
        rect.setY(y + 40);
      }
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      return false;
    }
    return true;
  }

  private static boolean inBounds(int x, int y) {

    if (x >= ClientEventHandler.startX && x <= ClientEventHandler.startX + 182) {

      if (y >= ClientEventHandler.startY - 1 && y <= ClientEventHandler.startY + 40) {
        return true;
      }
    }
    return false;
  }
}
