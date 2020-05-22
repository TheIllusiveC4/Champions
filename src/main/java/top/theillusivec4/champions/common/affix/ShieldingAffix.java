package top.theillusivec4.champions.common.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;

public class ShieldingAffix extends BasicAffix {

  public ShieldingAffix() {
    super("shielding", AffixCategory.DEFENSE);
  }

  @Override
  public void onUpdate(IChampion champion) {
    AffixData.BooleanData shielding = AffixData
        .getData(champion, this.getIdentifier(), AffixData.BooleanData.class);
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity.ticksExisted % 40 == 0 && livingEntity.getRNG().nextFloat() < 0.5F) {
      shielding.mode = !shielding.mode;
      shielding.saveData();
    }

    if (shielding.mode) {
      ((ServerWorld) livingEntity.getEntityWorld()).spawnParticle(ParticleTypes.ENTITY_EFFECT,
          livingEntity.posX + (livingEntity.getRNG().nextFloat() - 0.5D) * livingEntity.getWidth(),
          livingEntity.posY + livingEntity.getRNG().nextFloat() * livingEntity.getHeight(),
          livingEntity.posZ + (livingEntity.getRNG().nextFloat() - 0.5D) * livingEntity.getWidth(),
          0, 1.0F, 1.0F, 1.0F, 1.0F);
    }
  }

  @Override
  public boolean onAttacked(IChampion champion, DamageSource source, float amount) {
    AffixData.BooleanData shielding = AffixData
        .getData(champion, this.getIdentifier(), AffixData.BooleanData.class);

    if (shielding.mode) {
      champion.getLivingEntity().playSound(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0F, 1.0F);
      return false;
    }
    return true;
  }
}
