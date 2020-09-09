package top.theillusivec4.champions.common.affix;

import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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
    ModifiableAttributeInstance speed = champion.getLivingEntity()
        .getAttribute(Attributes.MOVEMENT_SPEED);

    if (speed != null) {
      speed.applyNonPersistentModifier(
          new AttributeModifier(UUID.fromString("28c606d8-9fdf-40b4-9a02-dca3ec1adb5a"),
              "Hasty affix", ChampionsConfig.hastyMovementBonus, Operation.ADDITION));
    }
  }

  @Override
  public boolean canApply(IChampion champion) {
    return champion.getLivingEntity().getAttribute(Attributes.MOVEMENT_SPEED) != null;
  }
}
