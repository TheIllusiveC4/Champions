package c4.champions.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCinderSpark extends AbstractEntitySpark {

  public EntityCinderSpark(World worldIn) {
    super(worldIn);
  }

  @SideOnly(Side.CLIENT)
  public EntityCinderSpark(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
    super(worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
  }

  public EntityCinderSpark(World worldIn, EntityLivingBase ownerIn, Entity targetIn, EnumFacing.Axis directionIn) {
    super(worldIn, ownerIn, targetIn, directionIn);
  }

  @Override
  protected EnumParticleTypes getParticleType() {
    return EnumParticleTypes.FLAME;
  }

  @Override
  protected void bulletHit(RayTraceResult result) {
    if (result.entityHit == null) {
      ((WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 2, 0.2D, 0.2D, 0.2D, 0.0D);
      this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
    } else {
      if (result.entityHit instanceof EntityLivingBase) {
        EntityLivingBase hit = ((EntityLivingBase)result.entityHit);
        hit.attackEntityFrom(new EntityDamageSourceIndirect("cinderSpark", this.getOwner(), this).setFireDamage().setMagicDamage(), 1);
        hit.setFire(8);
      }
    }
    this.setDead();
  }
}
