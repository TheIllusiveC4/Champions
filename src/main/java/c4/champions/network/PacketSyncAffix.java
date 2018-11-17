package c4.champions.network;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class PacketSyncAffix implements IMessage {

    public PacketSyncAffix() {}

    private int entityId;
    private int tier;
    private Map<String, NBTTagCompound> affixData;
    private int num;
    private String name;

    public PacketSyncAffix(int entityId, int tier, Map<String, NBTTagCompound> affixData, String name) {
        this.entityId = entityId;
        this.tier = tier;
        this.affixData = affixData;
        this.num = affixData.size();
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.tier = buf.readInt();
        this.num = buf.readInt();
        Map<String, NBTTagCompound> data = Maps.newHashMap();
        for (int i = 0; i < this.num; i++) {
            data.putIfAbsent(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readTag(buf));
        }
        this.affixData = data;
        this.name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(tier);
        buf.writeInt(num);
        for (Map.Entry<String, NBTTagCompound> entry : affixData.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            ByteBufUtils.writeTag(buf, entry.getValue());
        }
        ByteBufUtils.writeUTF8String(buf, name);
    }

    public static class PacketSyncHandler implements IMessageHandler<PacketSyncAffix, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncAffix message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if (ChampionHelper.isValidChampion(entity)) {
                    IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);

                    if (chp != null) {
                        chp.setRank(RankManager.getRankForTier(message.tier));
                        chp.setAffixData(message.affixData);
                        chp.setName(message.name);
                    }
                }
            });
            return null;
        }
    }
}
