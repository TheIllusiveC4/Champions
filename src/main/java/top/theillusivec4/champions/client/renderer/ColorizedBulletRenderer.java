package top.theillusivec4.champions.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.entity.AbstractBulletEntity;

public class ColorizedBulletRenderer extends EntityRenderer<AbstractBulletEntity> {

  private static final ResourceLocation GENERIC_SPARK_TEXTURE = new ResourceLocation(
      Champions.MODID, "textures/entity/generic_spark.png");
  private final ShulkerBulletModel<AbstractBulletEntity> model = new ShulkerBulletModel<>();

  private final int color;

  public ColorizedBulletRenderer(EntityRendererManager manager, int color) {
    super(manager);
    this.color = color;
  }

  private float rotLerp(float p_188347_1_, float p_188347_2_, float p_188347_3_) {
    float f = p_188347_2_ - p_188347_1_;

    while (f < -180.0F) {
      f += 360.0F;
    }

    while (f >= 180.0F) {
      f -= 360.0F;
    }
    return p_188347_1_ + p_188347_3_ * f;
  }

  @Override
  public void doRender(AbstractBulletEntity entity, double x, double y, double z, float entityYaw,
      float partialTicks) {
    GlStateManager.pushMatrix();
    float f = this.rotLerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
    float f1 = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
    float f2 = (float) entity.ticksExisted + partialTicks;
    GlStateManager.translatef((float) x, (float) y + 0.15F, (float) z);
    GlStateManager.rotatef(MathHelper.sin(f2 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotatef(MathHelper.cos(f2 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotatef(MathHelper.sin(f2 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
    float f3 = 0.03125F;
    GlStateManager.enableRescaleNormal();
    GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
    this.bindEntityTexture(entity);
    this.model.render(entity, 0.0F, 0.0F, 0.0F, f, f1, 0.03125F);
    GlStateManager.enableBlend();
    float r = (float) ((this.color >> 16) & 0xFF) / 255F;
    float g = (float) ((this.color >> 8) & 0xFF) / 255F;
    float b = (float) ((this.color) & 0xFF) / 255F;
    GlStateManager.color4f(r, g, b, 0.5F);
    GlStateManager.scalef(1.5F, 1.5F, 1.5F);
    this.model.render(entity, 0.0F, 0.0F, 0.0F, f, f1, 0.03125F);
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }

  @Override
  protected ResourceLocation getEntityTexture(@Nonnull AbstractBulletEntity entity) {
    return GENERIC_SPARK_TEXTURE;
  }
}
