package top.theillusivec4.champions.common.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class SPacketSyncChampion {

  private int entityId;
  private int tier;
  private int defaultColor;
  private Set<String> affixes;
  private int affixSize;

  public SPacketSyncChampion(int entityId, int tier, int defaultColor, Set<String> affixes) {
    this.entityId = entityId;
    this.tier = tier;
    this.affixSize = affixes.size();
    this.affixes = affixes;
    this.defaultColor = defaultColor;
  }

  public static void encode(SPacketSyncChampion msg, PacketBuffer buf) {
    buf.writeInt(msg.entityId);
    buf.writeInt(msg.tier);
    buf.writeInt(msg.affixSize);
    buf.writeInt(msg.defaultColor);
    msg.affixes.forEach(buf::writeString);
  }

  public static SPacketSyncChampion decode(PacketBuffer buf) {
    int entityId = buf.readInt();
    int tier = buf.readInt();
    Set<String> affixes = new HashSet<>();
    int affixSize = buf.readInt();
    int defaultColor = buf.readInt();

    for (int i = 0; i < affixSize; i++) {
      affixes.add(buf.readString());
    }
    return new SPacketSyncChampion(entityId, tier, defaultColor, affixes);
  }

  public static void handle(SPacketSyncChampion msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {
        Entity entity = world.getEntityByID(msg.entityId);
        ChampionCapability.getCapability(entity).ifPresent(champion -> {
          IChampion.Client clientChampion = champion.getClient();
          clientChampion.setRank(new Tuple<>(msg.tier, msg.defaultColor));
          clientChampion.setAffixes(msg.affixes);
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
