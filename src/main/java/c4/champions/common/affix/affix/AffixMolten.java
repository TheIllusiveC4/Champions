package c4.champions.common.affix.affix;

import c4.champions.Champions;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.Iterator;

public class AffixMolten extends AffixBase {

    private static final Field IMMUNE_TO_FIRE = ReflectionHelper.findField(Entity.class, "isImmuneToFire",
            "field_70178_ae");

    public AffixMolten() {
        super("molten", AffixCategory.OFFENSE);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {
        entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
        entity.setPathPriority(PathNodeType.WATER, -1.0F);
        entity.setPathPriority(PathNodeType.LAVA, 8.0F);
        entity.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        entity.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);

        Iterator<EntityAITasks.EntityAITaskEntry> iterator = entity.tasks.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

            if (entityaibase instanceof EntityAIFleeSun || entityaibase instanceof EntityAIRestrictSun) {
                iterator.remove();
            }
        }

        if (entity.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) entity.getNavigator()).setAvoidSun(false);
        }
    }

    @Override
    public void onUpdate(EntityLiving entity, IChampionship cap) {

        if (!entity.world.isRemote) {

            if (entity.ticksExisted % 20 == 0) {
                entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
            }

            if (entity.isWet()) {
                entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }
        }

        if (!entity.isImmuneToFire()) {
            try {
                IMMUNE_TO_FIRE.setBoolean(entity, true);
            } catch (IllegalAccessException e) {
                Champions.logger.log(Level.ERROR, "Error setting fire immunity for " + entity.toString());
            }
        }
    }

    @Override
    public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {
        target.setFire(10);
        source.setMagicDamage();
    }
}
