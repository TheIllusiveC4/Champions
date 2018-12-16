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

package c4.champions.network;

import c4.champions.Champions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRankParticle implements IMessage {

    public PacketRankParticle() {}

    int color;
    int entityId;

    public PacketRankParticle(int entityId, int color) {
        this.entityId = entityId;
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        color = buf.readInt();
        entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(color);
        buf.writeInt(entityId);
    }

    public static class PacketParticleHandler implements IMessageHandler<PacketRankParticle, IMessage> {

        @Override
        public IMessage onMessage(PacketRankParticle message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase)entity;
                    Champions.proxy.generateRankParticle(living, message.color);
                }
            });
            return null;
        }
    }
}
