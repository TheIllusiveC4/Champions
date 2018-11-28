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

package c4.champions.common.affix.core;

import c4.champions.Champions;
import c4.champions.common.capability.IChampionship;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffix;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

public abstract class AffixNBT {

    private IChampionship championship;
    private String identifier;

    protected AffixNBT() {}

    public void readData(IChampionship championship, String identifier) {
        this.championship = championship;
        this.identifier = identifier;
        readFromNBT(championship.getAffixData(identifier));
    }

    public abstract void readFromNBT(NBTTagCompound tag);

    public abstract NBTTagCompound writeToNBT();

    public void saveData(final EntityLiving living) {
        championship.setAffixData(identifier, writeToNBT());
        syncToTracking(living);
    }

    private void syncToTracking(final EntityLiving living) {
        if (living.world instanceof WorldServer) {
            WorldServer world = (WorldServer)living.world;

            for (EntityPlayer player : world.getEntityTracker().getTrackingPlayers(living)) {

                if (player instanceof EntityPlayerMP && championship.getRank() != null) {
                    NetworkHandler.INSTANCE.sendTo(new PacketSyncAffix(living.getEntityId(),
                            championship.getRank().getTier(), championship.getAffixData(), championship.getName()),
                            (EntityPlayerMP)player);
                }
            }
        }
    }

    public static <T extends AffixNBT> T getData(@Nonnull IChampionship championship, String affix, Class<T> clazz) {
        T data = null;

        try {
            data = clazz.newInstance();
            data.readData(championship, affix);
        } catch (IllegalAccessException | InstantiationException e) {
            Champions.logger.log(Level.ERROR, "Error reading data from class " + clazz.toString());
        }
        return data;
    }

    public static class Boolean extends AffixNBT {

        public boolean mode;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            mode = tag.getBoolean("mode");
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("mode", mode);
            return compound;
        }
    }

    public static class Text extends AffixNBT {

        public String text;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            text = tag.getString("text");
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("text", text);
            return compound;
        }
    }

    public static class Integer extends AffixNBT {

        public int num;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            num = tag.getInteger("num");
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("num", num);
            return compound;
        }
    }
}
