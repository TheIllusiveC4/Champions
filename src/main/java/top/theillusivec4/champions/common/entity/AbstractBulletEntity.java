package top.theillusivec4.champions.common.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBulletEntity extends Projectile {

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

  public AbstractBulletEntity(EntityType<? extends AbstractBulletEntity> type, Level world) {
    super(type, world);
    this.noPhysics = true;
  }

  @OnlyIn(Dist.CLIENT)
  public AbstractBulletEntity(
      EntityType<? extends AbstractBulletEntity> type, Level worldIn,
      double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
    this(type, worldIn);
    this.setPos(x, y, z);
    this.setRot(this.getYRot(), this.getXRot());
    this.setDeltaMovement(motionXIn, motionYIn, motionZIn);
  }

  public AbstractBulletEntity(
      EntityType<? extends AbstractBulletEntity> type, Level worldIn,
      LivingEntity ownerIn, Entity targetIn, Direction.Axis direction) {
    this(type, worldIn);
    this.setShooter(ownerIn);
    BlockPos blockpos = ownerIn.blockPosition();
    double d0 = (double) blockpos.getX() + 0.5D;
    double d1 = (double) blockpos.getY() + 0.5D;
    double d2 = (double) blockpos.getZ() + 0.5D;
    this.setPos(d0, d1, d2);
    this.setRot(this.getYRot(), this.getXRot());
    this.target = targetIn;
    this.direction = Direction.UP;
    this.selectNextMoveDirection(direction);
  }

  public void setShooter(@Nullable Entity entityIn) {

    if (entityIn != null) {
      this.ownerUniqueId = entityIn.getUUID();
      this.ownerId = entityIn.getId();
    }
  }

  @NotNull
  @Override
  public SoundSource getSoundSource() {
    return SoundSource.HOSTILE;
  }

  @Override
  protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {

    if (this.ownerUniqueId != null) {
      compound.putUUID("Owner", this.ownerUniqueId);
    }

    if (this.targetUniqueId != null) {
      compound.putUUID("Target", this.targetUniqueId);
    }

    if (this.direction != null) {
      compound.putInt("Dir", this.direction.ordinal());
    }
    compound.putInt("Steps", this.steps);
    compound.putDouble("TXD", this.targetDeltaX);
    compound.putDouble("TYD", this.targetDeltaY);
    compound.putDouble("TZD", this.targetDeltaZ);
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag compound) {
    this.steps = compound.getInt("Steps");
    this.targetDeltaX = compound.getDouble("TXD");
    this.targetDeltaY = compound.getDouble("TYD");
    this.targetDeltaZ = compound.getDouble("TZD");

    if (compound.contains("Dir", 99)) {
      this.direction = Direction.values()[compound.getInt("Dir")];
    }

    if (compound.contains("Owner", 10)) {
      this.ownerUniqueId = compound.getUUID("Owner");
    }

    if (compound.contains("Target", 10)) {
      this.targetUniqueId = compound.getUUID("Target");
    }
  }

  private void setDirection(@Nullable Direction directionIn) {
    this.direction = directionIn;
  }

  private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
    double d0 = 0.5D;
    BlockPos blockpos;

    if (this.target == null) {
      blockpos = this.blockPosition().below();
    } else {
      d0 = (double) this.target.getBbHeight() * 0.5D;
      blockpos = new BlockPos(this.target.position().x, this.target.position().y + d0,
          this.target.position().z);
    }
    double d1 = (double) blockpos.getX() + 0.5D;
    double d2 = (double) blockpos.getY() + d0;
    double d3 = (double) blockpos.getZ() + 0.5D;
    Direction direction = null;

    if (!blockpos.closerThan(this.blockPosition(), 2.0D)) {
      BlockPos blockpos1 = this.blockPosition();
      List<Direction> list = Lists.newArrayList();

      if (axis != Direction.Axis.X) {

        if (blockpos1.getX() < blockpos.getX() &&
            this.level.getBlockState(blockpos1.east()).getBlock() instanceof AirBlock) {
          list.add(Direction.EAST);
        } else if (blockpos1.getX() > blockpos.getX() &&
            this.level.getBlockState(blockpos1.west()).getBlock() instanceof AirBlock) {
          list.add(Direction.WEST);
        }
      }

      if (axis != Direction.Axis.Y) {

        if (blockpos1.getY() < blockpos.getY() &&
            this.level.getBlockState(blockpos1.above()).getBlock() instanceof AirBlock) {
          list.add(Direction.UP);
        } else if (blockpos1.getY() > blockpos.getY() &&
            this.level.getBlockState(blockpos1.below()).getBlock() instanceof AirBlock) {
          list.add(Direction.DOWN);
        }
      }

      if (axis != Direction.Axis.Z) {

        if (blockpos1.getZ() < blockpos.getZ() &&
            this.level.getBlockState(blockpos1.south()).getBlock() instanceof AirBlock) {
          list.add(Direction.SOUTH);
        } else if (blockpos1.getZ() > blockpos.getZ() &&
            this.level.getBlockState(blockpos1.north()).getBlock() instanceof AirBlock) {
          list.add(Direction.NORTH);
        }
      }

      direction = Direction.getRandom(this.random);

      if (list.isEmpty()) {

        for (int i = 5; !(this.level.getBlockState(blockpos1.relative(direction))
            .getBlock() instanceof AirBlock) && i > 0; --i) {
          direction = Direction.getRandom(this.random);
        }
      } else {
        direction = list.get(this.random.nextInt(list.size()));
      }
      d1 = this.position().x + (double) direction.getStepX();
      d2 = this.position().y + (double) direction.getStepY();
      d3 = this.position().z + (double) direction.getStepZ();
    }
    this.setDirection(direction);
    double d6 = d1 - this.position().x;
    double d7 = d2 - this.position().y;
    double d4 = d3 - this.position().z;
    double d5 = Mth.sqrt((float) (d6 * d6 + d7 * d7 + d4 * d4));

    if (d5 == 0.0D) {
      this.targetDeltaX = 0.0D;
      this.targetDeltaY = 0.0D;
      this.targetDeltaZ = 0.0D;
    } else {
      this.targetDeltaX = d6 / d5 * 0.15D;
      this.targetDeltaY = d7 / d5 * 0.15D;
      this.targetDeltaZ = d4 / d5 * 0.15D;
    }
    this.hasImpulse = true;
    this.steps = 10 + this.random.nextInt(5) * 10;
  }

  @Override
  public void tick() {
    super.tick();

    if (!this.level.isClientSide) {

      if (this.target == null && this.targetUniqueId != null) {
        this.target = ((ServerLevel) this.level).getEntity(this.targetUniqueId);

        if (this.target == null) {
          this.targetUniqueId = null;
        }
      }

      if (this.target == null || !this.target.isAlive()
          || this.target instanceof Player && this.target.isSpectator()) {

        if (!this.isNoGravity()) {
          this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
      } else {
        this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
        this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
        this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
        Vec3 vector3d = this.getDeltaMovement();
        this.setDeltaMovement(vector3d
            .add((this.targetDeltaX - vector3d.x) * 0.2D, (this.targetDeltaY - vector3d.y) * 0.2D,
                (this.targetDeltaZ - vector3d.z) * 0.2D));
      }
      HitResult raytraceresult = ProjectileUtil
          .getHitResult(this, this::canCollideWith);

      if (raytraceresult.getType() != HitResult.Type.MISS
          && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
        this.bulletHit(raytraceresult);
      }
    }
    Vec3 vector3d1 = this.getDeltaMovement();
    this.setPos(this.position().x + vector3d1.x, this.position().y + vector3d1.y,
        this.position().z + vector3d1.z);
    ProjectileUtil.rotateTowardsMovement(this, 0.5F);

    if (this.level.isClientSide) {
      this.level.addParticle(this.getParticle(), this.position().x - vector3d1.x,
          this.position().y - vector3d1.y + 0.15D, this.position().z - vector3d1.z, 0.0D, 0.0D,
          0.0D);
    } else if (this.target != null && this.target.isAlive()) {

      if (this.steps > 0) {
        --this.steps;

        if (this.steps == 0) {
          this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
        }
      }

      if (this.direction != null) {
        BlockPos blockpos = this.blockPosition();
        Direction.Axis direction$axis = this.direction.getAxis();

        if (this.level.getBlockState(blockpos.relative(this.direction)).getMaterial().isSolid()) {
          this.selectNextMoveDirection(direction$axis);
        } else {
          BlockPos blockpos1 = this.target.blockPosition();

          if (direction$axis == Direction.Axis.X && blockpos.getX() == blockpos1.getX()
              || direction$axis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ()
              || direction$axis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
            this.selectNextMoveDirection(direction$axis);
          }
        }
      }
    }
  }

  @Override
  public boolean canCollideWith(final Entity entity) {
    if (!entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith()
        && !entity.noPhysics) {
      Entity owner = this.level.getEntity(this.ownerId);
      return owner == null || !owner.isPassengerOfSameVehicle(entity);
    } else {
      return false;
    }
  }

  @Nullable
  private UUID getOwnerUniqueId() {
    if (this.ownerUniqueId != null) {
      return (this.ownerUniqueId);
    } else {
      return this.ownerId != 0 ? this.level.getEntity(this.ownerId).getUUID() : null;
    }
  }

  @Override
  public boolean isOnFire() {
    return false;
  }

  @Override
  public boolean shouldRenderAtSqrDistance(final double distance) {
    return distance < 16384.0D;
  }

  @Override
  public float getBrightness() {
    return 1.0F;
  }

  protected void bulletHit(HitResult result) {
    if (result.getType() == HitResult.Type.ENTITY) {
      Entity entity = ((EntityHitResult) result).getEntity();
      this.bulletEffect(entity);
    } else {
      ((ServerLevel) this.level)
          .sendParticles(ParticleTypes.EXPLOSION, this.position().x, this.position().y,
              this.position().z, 2,
              0.2D, 0.2D, 0.2D, 0.0D);
      this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }
    this.remove(RemovalReason.DISCARDED);
  }

  protected abstract void bulletEffect(Entity target);

  protected abstract ParticleOptions getParticle();

  @Override
  public boolean canBeCollidedWith() {
    return true;
  }

  @Override
  public boolean hurt(@Nonnull DamageSource source, float amount) {
    if (!this.level.isClientSide) {
      this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
      ((ServerLevel) this.level)
          .sendParticles(ParticleTypes.CRIT, this.position().x, this.position().y,
              this.position().z, 15,
              0.2D, 0.2D, 0.2D, 0.0D);
      this.remove(RemovalReason.DISCARDED);
    }
    return true;
  }

  @Override
  protected void defineSynchedData() {

  }

  @Nonnull
  @Override
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
