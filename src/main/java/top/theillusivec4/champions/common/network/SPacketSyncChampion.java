package top.theillusivec4.champions.common.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraftforge.network.NetworkEvent;
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

  public static void encode(SPacketSyncChampion msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.entityId);
    buf.writeInt(msg.tier);
    buf.writeInt(msg.affixSize);
    buf.writeInt(msg.defaultColor);
    msg.affixes.forEach(buf::writeUtf);
  }

  public static SPacketSyncChampion decode(FriendlyByteBuf buf) {
    int entityId = buf.readInt();
    int tier = buf.readInt();
    Set<String> affixes = new HashSet<>();
    int affixSize = buf.readInt();
    int defaultColor = buf.readInt();

    for (int i = 0; i < affixSize; i++) {
      affixes.add(buf.readUtf());
    }
    return new SPacketSyncChampion(entityId, tier, defaultColor, affixes);
  }

  public static void handle(SPacketSyncChampion msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel world = Minecraft.getInstance().level;

      if (world != null) {
        Entity entity = world.getEntity(msg.entityId);

        if (entity instanceof LivingEntity) {
          ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
            IChampion.Client clientChampion = champion.getClient();
            clientChampion.setRank(new Tuple<>(msg.tier, msg.defaultColor));
            clientChampion.setAffixes(msg.affixes);
          });
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
