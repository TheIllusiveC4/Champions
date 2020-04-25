package top.theillusivec4.champions.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.rank.RankManager;

public class SPacketSyncChampion {

  private int entityId;
  private int tier;

  public SPacketSyncChampion(int entityId, int tier) {
    this.entityId = entityId;
    this.tier = tier;
  }

  public static void encode(SPacketSyncChampion msg, PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeInt(msg.tier);
  }

  public static SPacketSyncChampion decode(PacketBuffer buf) {
    return new SPacketSyncChampion(buf.readInt(), buf.readInt());
  }

  public static void handle(SPacketSyncChampion msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);

      if (entity instanceof LivingEntity) {
        ChampionCapability.getCapability((LivingEntity) entity)
            .ifPresent(champion -> champion.setRank(RankManager.getRank(msg.tier)));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
