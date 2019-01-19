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

package c4.champions.common.capability;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import c4.champions.integrations.gamestages.ChampionStages;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffix;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public final class CapabilityChampionship {

    @CapabilityInject(IChampionship.class)
    public static final Capability<IChampionship> CHAMPION_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "championship");

    private static final String AFFIX_TAG = "affixes";
    private static final String TIER_TAG = "tier";
    private static final String DATA_TAG = "data";
    private static final String NAME_TAG = "name";

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static IChampionship getChampionship(final EntityLiving entityIn) {

        if (entityIn != null && entityIn.hasCapability(CHAMPION_CAP, DEFAULT_FACING)) {
            return entityIn.getCapability(CHAMPION_CAP, DEFAULT_FACING);
        }
        return null;
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IChampionship.class, new Capability.IStorage<IChampionship>() {
            @Override
            public NBTBase writeNBT(Capability<IChampionship> capability, IChampionship instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                Rank rank = instance.getRank();

                if (rank != null) {

                    if (rank.getTier() > 0) {
                        NBTTagList list = new NBTTagList();

                        for (String id : instance.getAffixes()) {
                            NBTTagCompound tag = new NBTTagCompound();
                            tag.setString("identifier", id);
                            tag.setTag(DATA_TAG, instance.getAffixData(id));
                            list.appendTag(tag);
                        }
                        compound.setTag(AFFIX_TAG, list);
                        compound.setString(NAME_TAG, instance.getName());
                    }
                    compound.setInteger(TIER_TAG, rank.getTier());
                }
                return compound;
            }

            @Override
            public void readNBT(Capability<IChampionship> capability, IChampionship instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound)nbt;

                if (compound.hasKey(TIER_TAG)) {
                    int tier = compound.getInteger(TIER_TAG);
                    instance.setRank(RankManager.getRankForTier(tier));

                    if (tier > 0) {
                        NBTTagList list = compound.getTagList(AFFIX_TAG, Constants.NBT.TAG_COMPOUND);
                        Map<String, NBTTagCompound> affixes = Maps.newHashMap();

                        for (int i = 0; i < list.tagCount(); i++) {
                            NBTTagCompound tag = list.getCompoundTagAt(i);
                            affixes.put(tag.getString("identifier"), tag.getCompoundTag(DATA_TAG));
                        }
                        instance.setAffixData(affixes);
                        instance.setName(compound.getString(NAME_TAG));
                    }
                }
            }
        }, Championship::new);
    }

    public static ICapabilityProvider createProvider(final IChampionship championship) {
        return new Provider(championship, CHAMPION_CAP, DEFAULT_FACING);
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<IChampionship> capability;
        final EnumFacing facing;
        final IChampionship instance;

        Provider (final IChampionship instance, final Capability<IChampionship> capability, @Nullable final EnumFacing
                facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<IChampionship> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final IChampionship getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability().writeNBT(getInstance(), getFacing());
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            getCapability().readNBT(getInstance(), getFacing(), nbt);
        }
    }

    @Mod.EventBusSubscriber(modid = Champions.MODID)
    public static class EventHandler {

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            Entity entity = evt.getObject();

            if (ChampionHelper.isValidChampion(entity) && ChampionHelper.isValidDimension(entity.dimension)) {
                evt.addCapability(ID, createProvider(new Championship()));
            }
        }

        @SubscribeEvent
        public static void entityJoin(EntityJoinWorldEvent evt) {
            Entity entity = evt.getEntity();

            if (!entity.world.isRemote && ChampionHelper.isValidChampion(entity)) {
                EntityLiving living = (EntityLiving)entity;
                IChampionship chp = getChampionship(living);

                if (chp != null && chp.getRank() == null) {
                    Rank rank = ChampionHelper.generateRank(living);
                    chp.setRank(rank);

                    if (rank.getTier() > 0) {
                        Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                        chp.setAffixes(affixes);
                        chp.setName(ChampionHelper.generateRandomName());
                        chp.getRank().applyGrowth(living);

                        for (String s : chp.getAffixes()) {
                            IAffix affix = AffixRegistry.getAffix(s);

                            if (affix != null) {
                                affix.onInitialSpawn(living, chp);
                            }
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void entitySpawn(LivingSpawnEvent.SpecialSpawn evt) {
            Entity entity = evt.getEntity();

            if (!entity.world.isRemote && ChampionHelper.isValidChampion(entity)) {
                EntityLiving living = (EntityLiving)entity;
                IChampionship chp = getChampionship(living);

                if (chp != null) {

                    if (evt.getSpawner() != null && !ConfigHandler.championSpawners) {
                        chp.setRank(RankManager.getEmptyRank());
                        return;
                    }

                    if (Champions.isGameStagesLoaded && !ChampionStages.canSpawn(living)) {
                        chp.setRank(RankManager.getEmptyRank());
                        return;
                    }

                    if (chp.getRank() == null) {
                        Rank rank = ChampionHelper.generateRank(living);
                        chp.setRank(rank);

                        if (rank.getTier() > 0) {
                            Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                            chp.setAffixes(affixes);
                            chp.setName(ChampionHelper.generateRandomName());
                            chp.getRank().applyGrowth(living);

                            for (String s : chp.getAffixes()) {
                                IAffix affix = AffixRegistry.getAffix(s);

                                if (affix != null) {
                                    affix.onInitialSpawn(living, chp);
                                }
                            }
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void startTracking(PlayerEvent.StartTracking evt) {
            Entity entity = evt.getTarget();

            if (evt.getEntityPlayer() instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP)evt.getEntityPlayer();

                if (ChampionHelper.isValidChampion(entity)) {
                    IChampionship chp = getChampionship((EntityLiving) entity);

                    if (chp != null && ChampionHelper.isElite(chp.getRank())) {
                        NetworkHandler.INSTANCE.sendTo(new PacketSyncAffix(entity.getEntityId(),
                                chp.getRank().getTier(), chp.getAffixData(), chp.getName()), playerMP);
                    }
                }
            }
        }
    }
}
