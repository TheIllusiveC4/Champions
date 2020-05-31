package top.theillusivec4.champions.common.affix.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.AdaptableAffix;
import top.theillusivec4.champions.common.affix.ArcticAffix;
import top.theillusivec4.champions.common.affix.DampeningAffix;
import top.theillusivec4.champions.common.affix.DesecratingAffix;
import top.theillusivec4.champions.common.affix.EnkindlingAffix;
import top.theillusivec4.champions.common.affix.HastyAffix;
import top.theillusivec4.champions.common.affix.InfestedAffix;
import top.theillusivec4.champions.common.affix.KnockingAffix;
import top.theillusivec4.champions.common.affix.LivelyAffix;
import top.theillusivec4.champions.common.affix.MagneticAffix;
import top.theillusivec4.champions.common.affix.MoltenAffix;
import top.theillusivec4.champions.common.affix.ParalyzingAffix;
import top.theillusivec4.champions.common.affix.PlaguedAffix;
import top.theillusivec4.champions.common.affix.ReflectiveAffix;
import top.theillusivec4.champions.common.affix.ShieldingAffix;
import top.theillusivec4.champions.common.affix.WoundingAffix;
import top.theillusivec4.champions.common.config.AffixesConfig.AffixConfig;
import top.theillusivec4.champions.common.config.ChampionsConfig;
import top.theillusivec4.champions.common.config.ConfigEnums.Permission;

public class AffixManager {

  private static final Map<String, AffixSettings> SETTINGS = new HashMap<>();

  public static void register() {
    Champions.API.registerAffixes(new MoltenAffix(), new HastyAffix(), new ReflectiveAffix(),
        new LivelyAffix(), new MagneticAffix(), new DampeningAffix(), new AdaptableAffix(),
        new KnockingAffix(), new DesecratingAffix(), new PlaguedAffix(), new InfestedAffix(),
        new ParalyzingAffix(), new WoundingAffix(), new ShieldingAffix(), new ArcticAffix(),
        new EnkindlingAffix());
  }

  public static Optional<AffixSettings> getSettings(String identifier) {
    return Optional.ofNullable(SETTINGS.get(identifier));
  }

  public static void buildAffixSettings() {
    List<AffixConfig> configs = ChampionsConfig.affixes;
    SETTINGS.clear();

    if (configs.isEmpty()) {
      return;
    }

    configs.forEach(affixConfig -> {

      if (affixConfig.identifier == null) {
        Champions.LOGGER.error("Missing identifier while building affix settings, skipping...");
        return;
      }

      if (!Champions.API.getAffix(affixConfig.identifier).isPresent()) {
        Champions.LOGGER.error("Invalid identifier while building affix settings, skipping...");
        return;
      }
      AffixSettings settings = new AffixSettings(affixConfig.identifier, affixConfig.enabled,
          affixConfig.minTier, affixConfig.maxTier, affixConfig.mobList, affixConfig.mobPermission);
      SETTINGS.put(affixConfig.identifier, settings);
    });
  }

  public static class AffixSettings {

    final String identifier;
    final boolean enabled;
    final int minTier;
    @Nullable
    final Integer maxTier;
    final List<EntityType<?>> mobList;
    final Permission mobPermission;

    public AffixSettings(String identifier, Boolean enabled, Integer minTier,
        @Nullable Integer maxTier, List<String> mobList, String mobPermission) {
      this.identifier = identifier;
      this.enabled = enabled != null ? enabled : true;
      this.minTier = minTier != null ? minTier : 1;
      this.maxTier = maxTier;
      this.mobList = new ArrayList<>();

      if (mobList != null) {

        for (String s : mobList) {
          EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s));

          if (type != null) {
            this.mobList.add(type);
          }
        }
      }
      Permission permission = Permission.BLACKLIST;

      try {
        permission = Permission.valueOf(mobPermission);
      } catch (IllegalArgumentException e) {
        Champions.LOGGER.error("Invalid permission value " + mobPermission);
      }
      this.mobPermission = permission;
    }

    public boolean canApply(IChampion champion) {
      boolean isValidEntity;

      if (mobPermission == Permission.BLACKLIST) {
        isValidEntity = !mobList.contains(champion.getLivingEntity().getType());
      } else {
        isValidEntity = mobList.contains(champion.getLivingEntity().getType());
      }
      return this.enabled && isValidEntity && champion.getServer().getRank().map(
          rank -> rank.getTier() >= this.minTier && (this.maxTier == null
              || rank.getTier() <= this.maxTier)).orElse(false);
    }
  }
}
