package top.theillusivec4.champions.common.affix;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.champions.api.AffixCategory;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.affix.core.BasicAffix;
import top.theillusivec4.champions.common.config.ChampionsConfig;

public class ReflectiveAffix extends BasicAffix {
    private static final String REFLECTION_DAMAGE = "reflection";

    public ReflectiveAffix() {
        super("reflective", AffixCategory.OFFENSE, true);
    }

    @SubscribeEvent
    public void onDamageEvent(LivingDamageEvent evt) {
        if (!ChampionsConfig.reflectiveLethal && evt.getSource().getMsgId().equals(REFLECTION_DAMAGE)) {
            LivingEntity living = evt.getEntityLiving();
            float currentDamage = evt.getAmount();

            if (currentDamage >= living.getHealth()) {
                evt.setAmount(living.getHealth() - 1);
            }
        }
    }

    @Override
    public float onDamage(IChampion champion, DamageSource source, float amount, float newAmount) {
        if (source.getDirectEntity() instanceof LivingEntity) {
            LivingEntity sourceEntity = (LivingEntity) source.getDirectEntity();

            if (source.getMsgId().equals(REFLECTION_DAMAGE) || (source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns())) {
                return newAmount;
            }
            DamageSource newSource = new DamageSource(REFLECTION_DAMAGE);

            if (source instanceof IndirectEntityDamageSource && source.getDirectEntity() != null) {
                newSource = new IndirectEntityDamageSource(REFLECTION_DAMAGE, source.getDirectEntity(),
                        source.getDirectEntity());
                ((IndirectEntityDamageSource) newSource).isThorns();
            } else if (source instanceof EntityDamageSource) {
                newSource = new EntityDamageSource(REFLECTION_DAMAGE, source.getDirectEntity());
                ((EntityDamageSource) newSource).isThorns();
            }
            float min = (float) ChampionsConfig.reflectiveMinPercent;

            if (source.isFire()) {
                newSource.setIsFire();
            }

            if (source.isProjectile()) {
                newSource.setProjectile();
            }

            if (source.isExplosion()) {
                newSource.setExplosion();
            }

            if (source.isMagic()) {
                newSource.setMagic();
            }

            if (source.isDamageHelmet()) {
                newSource.damageHelmet();
            }

            if (source.isBypassArmor()) {
                newSource.bypassArmor();
            }

            if (source.scalesWithDifficulty()) {
                newSource.setScalesWithDifficulty();
            }

            if (source.isBypassInvul()) {
                newSource.bypassInvul();
            }
            float damage = (float) Math.min(
                    amount * (sourceEntity.getRandom().nextFloat() * (ChampionsConfig.reflectiveMaxPercent - min)
                            + min), ChampionsConfig.reflectiveMax);
            sourceEntity.hurt(newSource, damage);
        }
        return newAmount;
    }
}
