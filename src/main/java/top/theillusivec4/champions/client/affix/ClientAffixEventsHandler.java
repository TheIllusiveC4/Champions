package top.theillusivec4.champions.client.affix;

import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ClientAffixEventsHandler {

  @SubscribeEvent
  public void handleJailing(InputUpdateEvent evt) {

    if (evt.getPlayer().isPotionActive(ChampionsRegistry.PARALYSIS)) {
      MovementInput input = evt.getMovementInput();
      input.sneaking = false;
      input.jump = false;
      input.moveForward = 0;
      input.moveStrafe = 0;
    }
  }
}
