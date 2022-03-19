package top.theillusivec4.champions.common.affix;

import java.util.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;

public class ShieldingAffix extends BasicAffix {

  public ShieldingAffix() {
    super("shielding", AffixCategory.DEFENSE);
  }

  @Override
  public void onClientUpdate(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();
    AffixData.BooleanData shielding =
        AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);
    Random random = livingEntity.getRandom();

    if (shielding.mode) {
      livingEntity.getLevel().addParticle(ParticleTypes.ENTITY_EFFECT,
          livingEntity.position().x + (random.nextFloat() - 0.5D) * livingEntity.getBbWidth(),
          livingEntity.position().y + random.nextFloat() * livingEntity.getBbHeight(),
          livingEntity.position().z + (random.nextFloat() - 0.5D) * livingEntity.getBbWidth(),
          1.0F, 1.0F, 1.0F);
    }
  }

  @Override
  public void onServerUpdate(IChampion champion) {
    LivingEntity livingEntity = champion.getLivingEntity();

    if (livingEntity.tickCount % 40 == 0 && livingEntity.getRandom().nextFloat() < 0.5F) {
      AffixData.BooleanData shielding =
          AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);
      shielding.mode = !shielding.mode;
      shielding.saveData();
      this.sync(champion);
    }
  }

  @Override
  public void readSyncTag(IChampion champion, CompoundTag tag) {
    AffixData.BooleanData shielding =
        AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);
    shielding.readFromNBT(tag);
    shielding.saveData();
  }

  @Override
  public CompoundTag writeSyncTag(IChampion champion) {
    return AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class)
        .writeToNBT();
  }

  @Override
  public boolean onAttacked(IChampion champion, DamageSource source, float amount) {
    AffixData.BooleanData shielding =
        AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);

    if (shielding.mode) {
      champion.getLivingEntity().playSound(SoundEvents.PLAYER_ATTACK_NODAMAGE, 1.0F, 1.0F);
      return false;
    }
    return true;
  }
}
