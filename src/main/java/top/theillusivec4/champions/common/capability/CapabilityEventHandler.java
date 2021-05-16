package top.theillusivec4.champions.common.capability;

import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.network.SPacketSyncChampion;
import top.theillusivec4.champions.common.rank.Rank;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.util.ChampionBuilder;
import top.theillusivec4.champions.common.util.ChampionHelper;

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

    if (!entity.getEntityWorld().isRemote()) {
      ChampionCapability.getCapability(entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();

        if (!serverChampion.getRank().isPresent()) {

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
  public void startTracking(PlayerEvent.StartTracking evt) {
    Entity entity = evt.getTarget();
    PlayerEntity playerEntity = evt.getPlayer();

    if (entity instanceof LivingEntity && playerEntity instanceof ServerPlayerEntity) {
      ChampionCapability.getCapability((LivingEntity) entity).ifPresent(champion -> {
        IChampion.Server serverChampion = champion.getServer();
        NetworkHandler.INSTANCE
            .send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity),
                new SPacketSyncChampion(entity.getEntityId(),
                    serverChampion.getRank().map(Rank::getTier).orElse(0),
                    serverChampion.getRank().map(Rank::getDefaultColor).orElse(0),
                    serverChampion.getAffixes().stream().map(IAffix::getIdentifier)
                        .collect(Collectors.toSet())));
      });
    }
  }
}
