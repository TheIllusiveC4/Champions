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

package c4.champions.common.init;

import c4.champions.Champions;
import c4.champions.common.entity.EntityArcticSpark;
import c4.champions.common.entity.EntityJail;
import c4.champions.common.potion.PotionJailed;
import c4.champions.common.potion.PotionPlague;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Champions.MODID)
@Mod.EventBusSubscriber(modid = Champions.MODID)
public class ChampionsRegistry {

    @GameRegistry.ObjectHolder("jailed")
    public static final Potion jailed = null;

    @GameRegistry.ObjectHolder("plague")
    public static final Potion plague = null;

    @SubscribeEvent
    public static void registerPotion(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(new PotionJailed());
        evt.getRegistry().register(new PotionPlague());
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> evt) {
        int id = 1;
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityJail.class)
                .id(new ResourceLocation(Champions.MODID, "entity_jail"), id++)
                .name(Champions.MODID + ".entity_jail")
                .tracker(160, Integer.MAX_VALUE, false)
                .build();
        EntityEntry entry1 = EntityEntryBuilder.create()
                .entity(EntityArcticSpark.class)
                .id(new ResourceLocation(Champions.MODID, "arctic_spark"), id++)
                .name(Champions.MODID + ".arctic_spark")
                .tracker(80, 3, true)
                .build();
        evt.getRegistry().registerAll(entry, entry1);
    }
}
