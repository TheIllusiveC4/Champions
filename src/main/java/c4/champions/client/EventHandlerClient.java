/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

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

        if (evt.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            RayTraceResult mouseOver = ClientUtil.getMouseOver(evt.getPartialTicks(), ConfigHandler.client.healthVisibility);

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
