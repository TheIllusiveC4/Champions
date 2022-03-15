package top.theillusivec4.champions.common.affix.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.network.NetworkHandler;
import top.theillusivec4.champions.common.network.SPacketSyncAffixData;

public abstract class BasicAffix implements IAffix {

  private final String id;
  private final AffixCategory category;

  public BasicAffix(String id, AffixCategory category) {
    this(id, category, false);
  }

  public BasicAffix(String id, AffixCategory category, boolean hasSubscriptions) {
    this.id = id;
    this.category = category;

    if (hasSubscriptions) {
      MinecraftForge.EVENT_BUS.register(this);
    }
  }

  @Override
  public String getIdentifier() {
    return this.id;
  }

  @Override
  public AffixCategory getCategory() {
    return this.category;
  }

  @Override
  public void sync(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();
    CompoundTag tag = this.writeSyncTag(champion);
    NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity),
        new SPacketSyncAffixData(livingEntity.getId(), this.getIdentifier(), tag));
  }

  public static boolean canTarget(LivingEntity livingEntity, LivingEntity target,
                                  boolean sightCheck) {

    if (target == null || !target.isAlive() || target instanceof ArmorStand || (sightCheck
        && !hasLineOfSight(livingEntity, target))) {
      return false;
    }
    AttributeInstance attributeInstance = livingEntity.getAttribute(Attributes.FOLLOW_RANGE);
    double range = attributeInstance == null ? 16.0D : attributeInstance.getValue();
    range = ChampionsConfig.affixTargetRange == 0 ? range
        : Math.min(range, ChampionsConfig.affixTargetRange);
    return livingEntity.distanceTo(target) <= range;
  }

  private static boolean hasLineOfSight(LivingEntity livingEntity, LivingEntity target) {

    if (livingEntity instanceof Mob mob) {
      return mob.getSensing().hasLineOfSight(target);
    } else {
      return livingEntity.hasLineOfSight(target);
    }
  }
}
