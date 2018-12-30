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
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Champions.MODID, value = Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        RenderingRegistry.registerEntityRenderingHandler(EntityArcticSpark.class, RenderArcticSpark.FACTORY);
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            EntityList.EntityEggInfo entitylist$entityegginfo = EntityList.ENTITY_EGGS.get(ItemMonsterPlacer.getNamedIdFrom(stack));

            if (entitylist$entityegginfo == null) {
                return -1;
            } else {
                return tintIndex == 0 ? entitylist$entityegginfo.primaryColor : entitylist$entityegginfo.secondaryColor;
            }
        }, ChampionsRegistry.championEgg);
    }

    @Override
    public void generateRankParticle(EntityLivingBase living, int color) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleRank(living.world,
                        living.posX + (living.getRNG().nextDouble() - 0.5D) * (double)living.width,
                        living.posY + living.getRNG().nextDouble() * (double)living.height,
                        living.posZ + (living.getRNG().nextDouble() - 0.5D) * (double)living.width, 0, 0, 0,
                        color));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        ModelLoader.setCustomModelResourceLocation(ChampionsRegistry.championEgg, 0,
                new ModelResourceLocation(ChampionsRegistry.championEgg.getRegistryName(), "inventory"));
    }
}
