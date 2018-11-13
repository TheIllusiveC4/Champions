package c4.champions.common.entity;

import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

public class EntityJail extends Entity {

    private int duration;
    private EntityLivingBase prisoner;
    private UUID prisonerUniqueId;

    public EntityJail(World world) {
        super(world);
        this.duration = 60;
        this.noClip = true;
        this.isImmuneToFire = true;
        this.width = 0;
        this.height = 0;
    }

    public EntityJail(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            float f5 = (float)Math.PI;

            for (int k1 = 0; (float)k1 < f5; ++k1)
            {
                float f6 = this.rand.nextFloat() * ((float)Math.PI * 2F);
                float f7 = MathHelper.sqrt(this.rand.nextFloat());
                float f8 = MathHelper.cos(f6) * f7;
                float f9 = MathHelper.sin(f6) * f7;
                int l1 = Color.red.getRGB();
                int i2 = l1 >> 16 & 255;
                int j2 = l1 >> 8 & 255;
                int j1 = l1 & 255;
                this.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), this.posX + (double)f8, this.posY, this.posZ + (double)f9, (double)((float)i2 / 255.0F), (double)((float)j2 / 255.0F), (double)((float)j1 / 255.0F));
            }
        } else {

            if (prisoner == null || prisoner.getDistance(this) > 1 || this.ticksExisted > this.duration) {
                this.setDead();
                return;
            }
            prisoner.addPotionEffect(new PotionEffect(ChampionsRegistry.jailed, 5, 0, false, false));
        }
    }

    @Override
    protected void entityInit() {}

    public void setPrisoner(@Nullable EntityLivingBase ownerIn) {
        this.prisoner = ownerIn;
        this.prisonerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
    }

    @Nullable
    public EntityLivingBase getPrisoner() {
        if (this.prisoner == null && this.prisonerUniqueId != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.prisonerUniqueId);

            if (entity instanceof EntityLivingBase)
            {
                this.prisoner = (EntityLivingBase)entity;
            }
        }

        return this.prisoner;
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        this.duration = compound.getInteger("Duration");
        this.prisonerUniqueId = compound.getUniqueId("PrisonerUUID");
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("Duration", this.duration);

        if (this.prisonerUniqueId != null)
        {
            compound.setUniqueId("PrisonerUUID", this.prisonerUniqueId);
        }
    }

    @Override
    @Nonnull
    public EnumPushReaction getPushReaction()
    {
        return EnumPushReaction.IGNORE;
    }
}
