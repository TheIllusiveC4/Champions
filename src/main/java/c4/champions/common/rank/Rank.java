package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class Rank {

    private static final UUID GROWTH = UUID.fromString("e8471143-fb3f-4ec0-b26f-b83794d08f61");
    private static final String GROWTH_NAME = "Growth modifier";

    private final int tier;
    private final int color;
    private final int affixes;
    private final int growthFactor;
    private final float chance;

    public Rank() {
        this(0, 0, 0, 0, 0);
    }

    public Rank(int tier, int affixes, int growthFactor, float chance, int color) {
        this.tier = tier;
        this.affixes = affixes;
        this.growthFactor = growthFactor;
        this.chance = chance;
        this.color = color;
    }

    public int getTier() {
        return tier;
    }

    public int getColor() {
        String[] colors = ConfigHandler.client.colors;
        if (colors.length >= tier && tier > 0) {
            String s = colors[tier - 1];

            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Non-integer in color config! " + s);
                return color;
            }
        }
        return color;
    }

    public int getAffixes() {
        return affixes;
    }

    public int getGrowthFactor() {
        return growthFactor;
    }

    public float getChance() {
        return chance;
    }

    public void applyGrowth(EntityLivingBase entityLivingBase) {
        double oldMax = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
        applyModifierIfExists(entityLivingBase, SharedMonsterAttributes.MAX_HEALTH, ConfigHandler.growth.health, 2);
        double newMax = entityLivingBase.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();

        if (oldMax != newMax) {
            entityLivingBase.setHealth(entityLivingBase.getMaxHealth());
        }

        applyModifierIfExists(entityLivingBase, SharedMonsterAttributes.ATTACK_DAMAGE, ConfigHandler.growth.attackDamage, 2);
        applyModifierIfExists(entityLivingBase, SharedMonsterAttributes.ARMOR, ConfigHandler.growth.armor, 0);
        applyModifierIfExists(entityLivingBase, SharedMonsterAttributes.ARMOR_TOUGHNESS, ConfigHandler.growth.armorToughness, 0);
        applyModifierIfExists(entityLivingBase, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, ConfigHandler.growth.knockbackResist, 0);
    }

    private void applyModifierIfExists(EntityLivingBase entityLivingBase, IAttribute attribute, double amount,
                                       int operation) {
        IAttributeInstance att = entityLivingBase.getEntityAttribute(attribute);
        if (att != null) {
            att.removeModifier(GROWTH);
            att.applyModifier(new AttributeModifier(GROWTH, GROWTH_NAME, amount * growthFactor, operation));
        }
    }
}
