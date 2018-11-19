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

package c4.champions;

import c4.champions.common.EventHandlerCommon;
import c4.champions.common.affix.AffixEvents;
import c4.champions.common.affix.Affixes;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.loot.EntityHasTier;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import c4.champions.debug.CommandDebug;
import c4.champions.network.NetworkHandler;
import c4.champions.proxy.IProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Champions.MODID,
        name = Champions.NAME,
        version = "@VERSION@",
        dependencies = "required-after:forge@[14.23.5.2768,)",
        acceptedMinecraftVersions = "[1.12, 1.13)",
        certificateFingerprint = "@FINGERPRINT@")
public class Champions
{
    public static final String MODID = "champions";
    public static final String NAME = "Champions";

    private static final boolean DEBUG = false;

    public static Logger logger;

    @SidedProxy(clientSide = "c4.champions.proxy.ClientProxy", serverSide = "c4.champions.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        proxy.preInit(evt);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        NetworkHandler.register();
        CapabilityChampionship.register();
        Affixes.registerAffixes();
        LootTableList.register(new ResourceLocation(Champions.MODID, "ranked_mobs"));
        EntityPropertyManager.registerProperty(new EntityHasTier.Serializer());
        MinecraftForge.EVENT_BUS.register(new AffixEvents());
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        proxy.init(evt);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        RankManager.readRanksFromJson();
        AffixFilterManager.readAffixFiltersFromJson();
        ChampionHelper.parseDimensionConfigs();
        proxy.postInit(evt);
    }

    @EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
        FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent evt) {

        if (DEBUG) {
            evt.registerServerCommand(new CommandDebug());
        }
    }
}
