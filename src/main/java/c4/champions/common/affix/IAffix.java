package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public interface IAffix {

    String getIdentifier();

    AffixCategory getCategory();

    void onSpawn(EntityLiving entity, IChampionship cap);

    void onUpdate(EntityLiving entity, IChampionship cap);

    void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount,
                  LivingAttackEvent evt);

    void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt);

    float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount);

    float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount);

    void onDeath(EntityLiving entity, IChampionship cap, DamageSource source, LivingDeathEvent evt);

    void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt);

    boolean canApply(EntityLiving entity);

    boolean isCompatibleWith(IAffix affix);

    int getTier();
}
