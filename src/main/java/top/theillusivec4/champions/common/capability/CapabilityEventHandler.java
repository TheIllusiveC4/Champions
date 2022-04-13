package top.theillusivec4.champions.common.capability;

import java.util.stream.Collectors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.network.SPacketSyncChampion;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.util.ChampionBuilder;
import top.theillusivec4.champions.common.util.ChampionHelper;

@SuppressWarnings("unused")
public class CapabilityEventHandler {

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
    Entity entity = evt.getObject();

    if (ChampionHelper.isValidChampion(entity)) {
      evt.addCapability(ChampionCapability.ID,
          ChampionCapability.createProvider((LivingEntity) entity));
    }
  }

  @SubscribeEvent
  public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn evt) {
    LivingEntity entity = evt.getEntityLiving();

    if (!entity.getLevel().isClientSide()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();

        if (serverChampion.getRank().isEmpty()) {

          if (!ChampionsConfig.championSpawners && evt.getSpawner() != null) {
            serverChampion.setRank(RankManager.getLowestRank());
          } else {
            ChampionBuilder.spawn(champion);
          }
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingConvert(LivingConversionEvent.Post evt) {
    LivingEntity entity = evt.getEntityLiving();

    if (!entity.getLevel().isClientSide()) {
      entity.reviveCaps();
      LivingEntity outcome = evt.getOutcome();
      ChampionCapability.getCapability(entity).ifPresent(
          oldChampion -> ChampionCapability.getCapability(outcome)
              .ifPresent(newChampion -> {
                ChampionBuilder.copy(oldChampion, newChampion);
                IChampion.Server serverChampion = newChampion.getServer();
                NetworkHandler.INSTANCE
                    .send(PacketDistributor.TRACKING_ENTITY.with(() -> outcome),
                        new SPacketSyncChampion(outcome.getId(),
                            serverChampion.getRank().map(Rank::getTier).orElse(0),
                            serverChampion.getRank().map(Rank::getDefaultColor).orElse(0),
                            serverChampion.getAffixes().stream().map(IAffix::getIdentifier)
                                .collect(Collectors.toSet())));
              }));
      entity.invalidateCaps();
    }
  }

  @SubscribeEvent
  public void startTracking(PlayerEvent.StartTracking evt) {
    Entity entity = evt.getTarget();
    Player playerEntity = evt.getPlayer();

    if (playerEntity instanceof ServerPlayer) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        NetworkHandler.INSTANCE
            .send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerEntity),
                new SPacketSyncChampion(entity.getId(),
                    serverChampion.getRank().map(Rank::getTier).orElse(0),
                    serverChampion.getRank().map(Rank::getDefaultColor).orElse(0),
                    serverChampion.getAffixes().stream().map(IAffix::getIdentifier)
                        .collect(Collectors.toSet())));
      });
    }
  }
}
