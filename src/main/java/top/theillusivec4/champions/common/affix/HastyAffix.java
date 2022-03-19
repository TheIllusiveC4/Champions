package top.theillusivec4.champions.common.affix;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class HastyAffix extends BasicAffix {

  public HastyAffix() {
    super("hasty", AffixCategory.OFFENSE);
  }

  @Override
  public void onInitialSpawn(IChampion champion) {
    AttributeInstance speed = champion.getLivingEntity().getAttribute(Attributes.MOVEMENT_SPEED);
    AttributeModifier hastyModifier =
        new AttributeModifier(UUID.fromString("28c606d8-9fdf-40b4-9a02-dca3ec1adb5a"),
            "Hasty affix", ChampionsConfig.hastyMovementBonus,
            AttributeModifier.Operation.ADDITION);

    if (speed != null && !speed.hasModifier(hastyModifier)) {
      speed.addTransientModifier(hastyModifier);
    }
  }

  @Override
  public boolean canApply(IChampion champion) {
    return champion.getLivingEntity().getAttribute(Attributes.MOVEMENT_SPEED) != null;
  }

  @Override
  public void onServerUpdate(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity.tickCount % 20 == 0) {
      onInitialSpawn(champion);
    }
  }
}
