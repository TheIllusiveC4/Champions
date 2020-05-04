package top.theillusivec4.champions.common.capability;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.network.SPacketSyncChampion;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.util.ChampionBuilder;
import top.theillusivec4.champions.common.util.ChampionHelper;

public class CapabilityEventHandler {

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
    Entity entity = evt.getObject();

    if (ChampionHelper.isValidEntity(entity)) {
      evt.addCapability(ChampionCapability.ID, ChampionCapability.createProvider());
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent evt) {
    LivingEntity entity = evt.getEntityLiving();

    if (entity.getEntityWorld().isRemote()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {

        if (champion.getRank().getTier() > 0) {
          int color = champion.getRank().getDefaultColor();
          float r = (float) ((color >> 16) & 0xFF) / 255f;
          float g = (float) ((color >> 8) & 0xFF) / 255f;
          float b = (float) ((color) & 0xFF) / 255f;
          entity.getEntityWorld().addParticle(ChampionsRegistry.RANK,
              entity.posX + (entity.getRNG().nextDouble() - 0.5D) * (double) entity.getWidth(),
              entity.posY + entity.getRNG().nextDouble() * entity.getHeight(),
              entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * (double) entity.getWidth(), r,
              g, b);
        }
      });
    }
  }

  @SubscribeEvent
  public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn evt) {
    LivingEntity entity = evt.getEntityLiving();

    if (!entity.getEntityWorld().isRemote()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {

        if (champion.getRank() == null) {
          Rank newRank = ChampionBuilder.createRank(entity);
          champion.setRank(newRank);
          ChampionBuilder.applyGrowth(entity, newRank.getGrowthFactor());
          List<IAffix> newAffixes = ChampionBuilder.createAffixes(newRank, entity);
          champion.setAffixes(newAffixes);
        }
      });
    }
  }

  @SubscribeEvent
  public void startTracking(PlayerEvent.StartTracking evt) {
    Entity entity = evt.getTarget();
    PlayerEntity playerEntity = evt.getPlayer();

    if (entity instanceof LivingEntity && playerEntity instanceof ServerPlayerEntity) {
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(
          champion -> NetworkHandler.INSTANCE
              .send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity),
                  new SPacketSyncChampion(entity.getEntityId(), champion.getRank().getTier(),
                      champion.getAffixIds())));
    }
  }
}
