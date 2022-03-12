package top.theillusivec4.champions.common.affix;

import java.util.Random;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.AffixData;
import top.theillusivec4.champions.common.affix.core.BasicAffix;

public class ShieldingAffix extends BasicAffix {

    public ShieldingAffix() {
        super("shielding", AffixCategory.DEFENSE);
    }

    @Override
    public void onServerUpdate(IChampion champion) {
        AffixData.BooleanData shielding = AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);
        LivingEntity livingEntity = champion.getLivingEntity();

        if (livingEntity.tickCount % 40 == 0 && livingEntity.getRandom().nextFloat() < 0.5F) {
            shielding.mode = !shielding.mode;
            shielding.saveData();
        }
        Random random = livingEntity.getRandom();

        if (shielding.mode) {
            ((ServerLevel) livingEntity.getLevel()).sendParticles(ParticleTypes.ENTITY_EFFECT,
                    livingEntity.position().x + (random.nextFloat() - 0.5D) * livingEntity.getBbWidth(),
                    livingEntity.position().y + random.nextFloat() * livingEntity.getBbHeight(),
                    livingEntity.position().z + (random.nextFloat() - 0.5D) * livingEntity.getBbWidth(),
                    5, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onAttacked(IChampion champion, DamageSource source, float amount) {
        AffixData.BooleanData shielding = AffixData.getData(champion, this.getIdentifier(), AffixData.BooleanData.class);

        if (shielding.mode) {
            champion.getLivingEntity().playSound(SoundEvents.PLAYER_ATTACK_NODAMAGE, 1.0F, 1.0F);
            return false;
        }
        return true;
    }
}
