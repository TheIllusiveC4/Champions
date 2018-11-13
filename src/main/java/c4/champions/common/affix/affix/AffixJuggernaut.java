package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

import java.util.UUID;

public class AffixJuggernaut extends AffixBase {

    private static final AttributeModifier MODIFIER = new AttributeModifier(UUID.fromString
            ("78ca1d72-a954-4a66-bd6e-f55d5416780b"), "Juggernaut modifier", 2, 0);

    public AffixJuggernaut() {
        super("juggernaut", AffixCategory.CC);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        IAttributeInstance att = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        if (att != null) {
            att.removeModifier(MODIFIER);
            att.applyModifier(MODIFIER);
        }
    }

    @Override
    public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {
        evt.setStrength(evt.getStrength() + 4);
        evt.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        if (!entity.world.isRemote) {
            EntityLivingBase target = entity.getAttackTarget();

            if (target != null) {

                if (ForgeEventFactory.getMobGriefingEvent(entity.world, entity)) {
                    int i1 = MathHelper.floor(entity.posY);
                    int l1 = MathHelper.floor(entity.posX);
                    int i2 = MathHelper.floor(entity.posZ);
                    boolean flag = false;

                    for (int k2 = -1; k2 <= 1; ++k2)
                    {
                        for (int l2 = -1; l2 <= 1; ++l2)
                        {
                            for (int j = 0; j <= 3; ++j)
                            {
                                int i3 = l1 + k2;
                                int k = i1 + j;
                                int l = i2 + l2;
                                BlockPos blockpos = new BlockPos(i3, k, l);
                                IBlockState iblockstate = entity.world.getBlockState(blockpos);
                                Block block = iblockstate.getBlock();

                                if (!block.isAir(iblockstate, entity.world, blockpos) && block.canEntityDestroy
                                        (iblockstate, entity.world, blockpos, entity) && net.minecraftforge.event
                                        .ForgeEventFactory.onEntityDestroyBlock(entity, blockpos, iblockstate))
                                {
                                    flag = entity.world.destroyBlock(blockpos, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        entity.world.playEvent(null, 1022, new BlockPos(entity), 0);
                    }
                }
            }
        }
    }
}
