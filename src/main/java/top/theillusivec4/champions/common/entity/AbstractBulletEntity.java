package top.theillusivec4.champions.common.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractBulletEntity extends Entity {

  private LivingEntity owner;
  private Entity target;
  @Nullable
  private Direction direction;
  private int steps;
  private double targetDeltaX;
  private double targetDeltaY;
  private double targetDeltaZ;
  @Nullable
  private UUID ownerUniqueId;
  private int ownerId;
  @Nullable
  private UUID targetUniqueId;

  public AbstractBulletEntity(EntityType<? extends AbstractBulletEntity> type, World world) {
    super(type, world);
    this.noClip = true;
  }

  @OnlyIn(Dist.CLIENT)
  public AbstractBulletEntity(EntityType<? extends AbstractBulletEntity> type, World worldIn,
      double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
    this(type, worldIn);
    this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
    this.setMotion(motionXIn, motionYIn, motionZIn);
  }

  public AbstractBulletEntity(EntityType<? extends AbstractBulletEntity> type, World worldIn,
      LivingEntity ownerIn, Entity targetIn, Direction.Axis direction) {
    this(type, worldIn);
    this.owner = ownerIn;
    BlockPos blockpos = owner.func_233580_cy_();
    double d0 = (double) blockpos.getX() + 0.5D;
    double d1 = (double) blockpos.getY() + 0.5D;
    double d2 = (double) blockpos.getZ() + 0.5D;
    this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
    this.target = targetIn;
    this.direction = Direction.UP;
    this.selectNextMoveDirection(direction);
  }

  @Nonnull
  @Override
  public SoundCategory getSoundCategory() {
    return SoundCategory.HOSTILE;
  }

  @Override
  protected void writeAdditional(@Nonnull CompoundNBT compound) {

    if (this.ownerUniqueId != null) {
      compound.putUniqueId("Owner", this.ownerUniqueId);
    }

    if (this.targetUniqueId != null) {
      compound.putUniqueId("Target", this.targetUniqueId);
    }

    if (this.direction != null) {
      compound.putInt("Dir", this.direction.getIndex());
    }
    compound.putInt("Steps", this.steps);
    compound.putDouble("TXD", this.targetDeltaX);
    compound.putDouble("TYD", this.targetDeltaY);
    compound.putDouble("TZD", this.targetDeltaZ);
  }

  @Override
  protected void readAdditional(CompoundNBT compound) {
    this.steps = compound.getInt("Steps");
    this.targetDeltaX = compound.getDouble("TXD");
    this.targetDeltaY = compound.getDouble("TYD");
    this.targetDeltaZ = compound.getDouble("TZD");

    if (compound.contains("Dir", 99)) {
      this.direction = Direction.byIndex(compound.getInt("Dir"));
    }

    if (compound.contains("Owner", 10)) {
      CompoundNBT compoundnbt = compound.getCompound("Owner");
      this.ownerUniqueId = NBTUtil.readUniqueId(compoundnbt);
    }

    if (compound.contains("Target", 10)) {
      CompoundNBT compoundnbt1 = compound.getCompound("Target");
      this.targetUniqueId = NBTUtil.readUniqueId(compoundnbt1);
    }
  }

  @Override
  protected void registerData() {
  }

  private void setDirection(@Nullable Direction directionIn) {
    this.direction = directionIn;
  }

  private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
    double d0 = 0.5D;
    BlockPos blockpos;

    if (this.target == null) {
      blockpos = this.func_233580_cy_().down();
    } else {
      d0 = (double) this.target.getHeight() * 0.5D;
      blockpos = new BlockPos(this.target.getPosX(), this.target.getPosY() + d0,
          this.target.getPosZ());
    }
    double d1 = (double) blockpos.getX() + 0.5D;
    double d2 = (double) blockpos.getY() + d0;
    double d3 = (double) blockpos.getZ() + 0.5D;
    Direction direction = null;

    if (!blockpos.withinDistance(this.getPositionVec(), 2.0D)) {
      BlockPos blockpos1 = this.func_233580_cy_();
      List<Direction> list = Lists.newArrayList();
      if (axis != Direction.Axis.X) {

        if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
          list.add(Direction.EAST);
        } else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
          list.add(Direction.WEST);
        }
      }

      if (axis != Direction.Axis.Y) {

        if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
          list.add(Direction.UP);
        } else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
          list.add(Direction.DOWN);
        }
      }

      if (axis != Direction.Axis.Z) {

        if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
          list.add(Direction.SOUTH);
        } else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
          list.add(Direction.NORTH);
        }
      }
      direction = Direction.func_239631_a_(this.rand);

      if (list.isEmpty()) {

        for (int i = 5; !this.world.isAirBlock(blockpos1.offset(direction)) && i > 0; --i) {
          direction = Direction.func_239631_a_(this.rand);
        }
      } else {
        direction = list.get(this.rand.nextInt(list.size()));
      }
      d1 = this.getPosX() + (double) direction.getXOffset();
      d2 = this.getPosY() + (double) direction.getYOffset();
      d3 = this.getPosZ() + (double) direction.getZOffset();
    }
    this.setDirection(direction);
    double d6 = d1 - this.getPosX();
    double d7 = d2 - this.getPosY();
    double d4 = d3 - this.getPosZ();
    double d5 = MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);

    if (d5 == 0.0D) {
      this.targetDeltaX = 0.0D;
      this.targetDeltaY = 0.0D;
      this.targetDeltaZ = 0.0D;
    } else {
      this.targetDeltaX = d6 / d5 * 0.15D;
      this.targetDeltaY = d7 / d5 * 0.15D;
      this.targetDeltaZ = d4 / d5 * 0.15D;
    }
    this.isAirBorne = true;
    this.steps = 10 + this.rand.nextInt(5) * 10;
  }

  @Override
  public void tick() {
    super.tick();

    if (!this.world.isRemote) {

      if (this.target == null && this.targetUniqueId != null) {
        this.target = ((ServerWorld) this.world).getEntityByUuid(this.targetUniqueId);

        if (this.target == null) {
          this.targetUniqueId = null;
        }
      }

      if (this.target == null || !this.target.isAlive()
          || this.target instanceof PlayerEntity && this.target.isSpectator()) {

        if (!this.hasNoGravity()) {
          this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }
      } else {
        this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
        this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
        this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d
            .add((this.targetDeltaX - vector3d.x) * 0.2D, (this.targetDeltaY - vector3d.y) * 0.2D,
                (this.targetDeltaZ - vector3d.z) * 0.2D));
      }
      RayTraceResult raytraceresult = ProjectileHelper
          .func_234618_a_(this, this::func_230298_a_, RayTraceContext.BlockMode.COLLIDER);

      if (raytraceresult.getType() != RayTraceResult.Type.MISS
          && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
        this.bulletHit(raytraceresult);
      }
    }
    Vector3d vector3d1 = this.getMotion();
    this.setPosition(this.getPosX() + vector3d1.x, this.getPosY() + vector3d1.y,
        this.getPosZ() + vector3d1.z);
    ProjectileHelper.rotateTowardsMovement(this, 0.5F);

    if (this.world.isRemote) {
      this.world.addParticle(this.getParticle(), this.getPosX() - vector3d1.x,
          this.getPosY() - vector3d1.y + 0.15D, this.getPosZ() - vector3d1.z, 0.0D, 0.0D, 0.0D);
    } else if (this.target != null && this.target.isAlive()) {

      if (this.steps > 0) {
        --this.steps;

        if (this.steps == 0) {
          this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
        }
      }

      if (this.direction != null) {
        BlockPos blockpos = this.func_233580_cy_();
        Direction.Axis direction$axis = this.direction.getAxis();

        if (this.world.isTopSolid(blockpos.offset(this.direction), this)) {
          this.selectNextMoveDirection(direction$axis);
        } else {
          BlockPos blockpos1 = this.target.func_233580_cy_();

          if (direction$axis == Direction.Axis.X && blockpos.getX() == blockpos1.getX()
              || direction$axis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ()
              || direction$axis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
            this.selectNextMoveDirection(direction$axis);
          }
        }
      }
    }
  }

  protected boolean func_230298_a_(Entity p_230298_1_) {

    if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.canBeCollidedWith()
        && !this.noClip) {
      Entity entity = this.func_234616_v_();
      return entity == null || !entity.isRidingSameEntity(p_230298_1_);
    } else {
      return false;
    }
  }

  public Entity func_234616_v_() {

    if (this.ownerUniqueId != null && this.world instanceof ServerWorld) {
      return ((ServerWorld) this.world).getEntityByUuid(this.ownerUniqueId);
    } else {
      return this.ownerId != 0 ? this.world.getEntityByID(this.ownerId) : null;
    }
  }

  @Override
  public boolean isBurning() {
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean isInRangeToRenderDist(double distance) {
    return distance < 16384.0D;
  }

  @Override
  public float getBrightness() {
    return 1.0F;
  }

  protected void bulletHit(RayTraceResult result) {

    if (result.getType() == RayTraceResult.Type.ENTITY) {
      Entity entity = ((EntityRayTraceResult) result).getEntity();
      this.bulletEffect(entity);
    } else {
      ((ServerWorld) this.world)
          .spawnParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosY(), this.getPosZ(), 2,
              0.2D, 0.2D, 0.2D, 0.0D);
      this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }
    this.remove();
  }

  protected abstract void bulletEffect(Entity target);

  protected abstract IParticleData getParticle();

  @Override
  public boolean canBeCollidedWith() {
    return true;
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {

    if (!this.world.isRemote) {
      this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
      ((ServerWorld) this.world)
          .spawnParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(), 15,
              0.2D, 0.2D, 0.2D, 0.0D);
      this.remove();
    }
    return true;
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  public LivingEntity getOwner() {
    return owner;
  }
}
