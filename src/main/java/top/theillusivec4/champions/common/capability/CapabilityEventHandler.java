package top.theillusivec4.champions.common.capability;

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
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.network.SPacketSyncChampion;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.util.ChampionBuilder;
import top.theillusivec4.champions.common.util.ChampionHelper;

public class CapabilityEventHandler {

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
    Entity entity = evt.getObject();

    if (ChampionHelper.isValidEntity(entity)) {
      evt.addCapability(ChampionCapability.ID,
          ChampionCapability.createProvider((LivingEntity) entity));
    }
  }

  @SubscribeEvent
  public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn evt) {
    LivingEntity entity = evt.getEntityLiving();

    if (!entity.getEntityWorld().isRemote()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {
        if (!champion.getRank().isPresent()) {

          if (!ChampionsConfig.championSpawners && evt.getSpawner() != null) {
            champion.setRank(RankManager.getEmptyRank());
          } else {
            ChampionBuilder.spawn(champion);
          }
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
                  new SPacketSyncChampion(entity.getEntityId(),
                      champion.getRank().map(Rank::getTier).orElse(0), champion.getAffixIds())));
    }
  }
}
