package c4.champions.common.affix.core;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Random;

public abstract class AffixBase implements IAffix {

    protected static final Random rand = new Random();

    private final String identifier;
    private final AffixCategory category;
    private final int tier;

    public AffixBase(String identifier, AffixCategory category) {
        this(identifier, category, 1);
    }

    public AffixBase(String identifier, AffixCategory category, int tier) {
        this.identifier = identifier;
        this.category = category;
        this.tier = tier;
        AffixRegistry.registerAffix(identifier, this);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AffixCategory getCategory() {
        return category;
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

    }

    @Override
    public void onAttacked(EntityLiving entity, IChampionship cap, DamageSource source, float amount, LivingAttackEvent evt) {

    }

    @Override
    public float onHurt(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return newAmount;
    }

    @Override
    public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
        return newAmount;
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return true;
    }

    @Override
    public int getTier() {
        return tier;
    }
}
