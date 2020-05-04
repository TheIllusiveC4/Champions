package top.theillusivec4.champions.common.network;

import java.util.HashSet;
import java.util.Set;
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
  private Set<String> affixes;
  private int affixSize;

  public SPacketSyncChampion(int entityId, int tier, Set<String> affixes) {
    this.entityId = entityId;
    this.tier = tier;
    this.affixSize = affixes.size();
    this.affixes = affixes;
  }

  public static void encode(SPacketSyncChampion msg, PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeInt(msg.tier);
    buf.writeInt(msg.affixSize);
    msg.affixes.forEach(buf::writeString);
  }

  public static SPacketSyncChampion decode(PacketBuffer buf) {
    int entityId = buf.readInt();
    int tier = buf.readInt();
    Set<String> affixes = new HashSet<>();
    int affixSize = buf.readInt();

    for (int i = 0; i < affixSize; i++) {
      affixes.add(buf.readString());
    }
    return new SPacketSyncChampion(entityId, tier, affixes);
  }

  public static void handle(SPacketSyncChampion msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);

      if (entity instanceof LivingEntity) {
        ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
          champion.setRank(RankManager.getRank(msg.tier));
          champion.setAffixIds(msg.affixes);
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
