package top.theillusivec4.champions.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class SPacketSyncAffixData {

  private final int entityId;
  private final String id;
  private final CompoundTag data;

  public SPacketSyncAffixData(int entityId, String id, CompoundTag data) {
    this.entityId = entityId;
    this.data = data;
    this.id = id;
  }

  public static void encode(SPacketSyncAffixData msg, FriendlyByteBuf buf) {
    buf.writeInt(msg.entityId);
    buf.writeUtf(msg.id);
    buf.writeNbt(msg.data);
  }

  public static SPacketSyncAffixData decode(FriendlyByteBuf buf) {
    return new SPacketSyncAffixData(buf.readInt(), buf.readUtf(), buf.readNbt());
  }

  public static void handle(SPacketSyncAffixData msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel world = Minecraft.getInstance().level;

      if (world != null) {
        Entity entity = world.getEntity(msg.entityId);
        ChampionCapability.getCapability(entity).ifPresent(champion -> {
          IChampion.Client clientChampion = champion.getClient();
          clientChampion.getAffix(msg.id)
              .ifPresent(affix -> affix.readSyncTag(champion, msg.data));
        });
      }
    });
    ctx.get().setPacketHandled(true);
  }
}

