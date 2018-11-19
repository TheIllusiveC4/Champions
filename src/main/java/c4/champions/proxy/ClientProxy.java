/*
 * Copyright (C) 2018  C4
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

package c4.champions.proxy;

import c4.champions.Champions;
import c4.champions.client.EventHandlerClient;
import c4.champions.client.fx.ParticleRank;
import c4.champions.client.renderer.RenderArcticSpark;
import c4.champions.common.entity.EntityArcticSpark;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Champions.MODID, value = Side.CLIENT)
public class ClientProxy implements IProxy {

    private static final Random rand = new Random();

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        RenderingRegistry.registerEntityRenderingHandler(EntityArcticSpark.class, RenderArcticSpark.FACTORY);
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }

    @Override
    public void generateRankParticles(EntityLiving living, int color) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleRank(living.world,
                        living.posX + (living.getRNG().nextDouble() - 0.5D) * (double)living.width,
                        living.posY + living.getRNG().nextDouble() * (double)living.height,
                        living.posZ + (living.getRNG().nextDouble() - 0.5D) * (double)living.width, 0, 0, 0,
                        color));
    }
}
