package top.theillusivec4.champions.client.affix;

import net.minecraft.client.player.Input;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

@SuppressWarnings("unused")
public class ClientAffixEventsHandler {

  @SubscribeEvent
  public void handleJailing(MovementInputUpdateEvent evt) {
    if (evt.getPlayer().hasEffect(ChampionsRegistry.PARALYSIS)) {
      Input input = evt.getInput();
      input.shiftKeyDown = false;
      input.jumping = false;
      input.forwardImpulse = 0;
      input.leftImpulse = 0;
    }
  }
}
