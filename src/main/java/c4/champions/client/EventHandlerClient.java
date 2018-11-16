package c4.champions.client;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {

    private static final int NAME_DISTANCE = 24;

    @SubscribeEvent
    public void onMovementInput(InputUpdateEvent evt) {

        if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.jailed)) {
            MovementInput input = evt.getMovementInput();
            input.sneak = false;
            input.jump = false;
            input.moveForward = 0;
            input.moveStrafe = 0;
        }
    }

    @SubscribeEvent
    public void renderChampionHealth(RenderGameOverlayEvent.Pre evt) {

        if (ConfigHandler.client.renderGUI && evt.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            RayTraceResult mouseOver = ClientUtil.getMouseOver(evt.getPartialTicks(), 50);

            if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entity = mouseOver.entityHit;

                if (ChampionHelper.isValidChampion(entity)) {
                    EntityLiving living = (EntityLiving)entity;
                    IChampionship chp = CapabilityChampionship.getChampionship(living);

                    if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                        ClientUtil.renderChampionHealth(living, chp);
                        evt.setCanceled(true);
                    }
                }
            }
        }
    }
}
