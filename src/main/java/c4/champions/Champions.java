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

package c4.champions;

import c4.champions.command.CommandChampionEgg;
import c4.champions.command.CommandSpawnChampion;
import c4.champions.common.EventHandlerCommon;
import c4.champions.common.affix.AffixEvents;
import c4.champions.common.affix.Affixes;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.item.ItemChampionPlacer;
import c4.champions.common.loot.EntityIsChampion;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import c4.champions.network.NetworkHandler;
import c4.champions.proxy.IProxy;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

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

    public static boolean isGameStagesLoaded = false;

    public static Logger logger;

    @SidedProxy(clientSide = "c4.champions.proxy.ClientProxy", serverSide = "c4.champions.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        LootTableList.register(new ResourceLocation(Champions.MODID, "champion_loot"));
        EntityPropertyManager.registerProperty(new EntityIsChampion.Serializer());
        proxy.preInit(evt);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        NetworkHandler.register();
        CapabilityChampionship.register();
        Affixes.registerAffixes();
        MinecraftForge.EVENT_BUS.register(new AffixEvents());
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        proxy.init(evt);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

        if (Loader.isModLoaded("gamestages")) {
            isGameStagesLoaded = true;
        }
        RankManager.readRanksFromJson();
        AffixFilterManager.readAffixFiltersFromJson();
        ChampionHelper.parseConfigs();
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ChampionsRegistry.championEgg, new BehaviorDefaultDispenseItem() {

            @Nonnull
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
                double d0 = source.getX() + (double)enumfacing.getXOffset();
                double d1 = (double)((float)(source.getBlockPos().getY() + enumfacing.getYOffset()) + 0.2F);
                double d2 = source.getZ() + (double)enumfacing.getZOffset();
                Entity entity = ItemChampionPlacer.createChampion(source.getWorld(), ItemMonsterPlacer.getNamedIdFrom(stack), d0, d1, d2);

                if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                ItemMonsterPlacer.applyItemEntityDataToEntity(source.getWorld(), null, stack, entity);

                if (ChampionHelper.isValidChampion(entity)) {
                    ItemChampionPlacer.applyItemChampionDataToEntity(source.getWorld(), null, stack, (EntityLiving)entity);
                }
                stack.shrink(1);
                return stack;
            }
        });
        proxy.postInit(evt);
    }

    @EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
        FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandSpawnChampion());
        evt.registerServerCommand(new CommandChampionEgg());
    }
}
