package top.theillusivec4.champions.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.common.entity.AbstractBulletEntity;

public class ColorizedBulletRenderer extends EntityRenderer<AbstractBulletEntity> {

  private static final ResourceLocation GENERIC_SPARK_TEXTURE = new ResourceLocation(
      Champions.MODID, "textures/entity/generic_spark.png");
  private static final RenderType renderType;
  private final ShulkerBulletModel<AbstractBulletEntity> model = new ShulkerBulletModel<>();

  private final int color;

  static {
    renderType = RenderType.getEntityTranslucent(GENERIC_SPARK_TEXTURE);
  }

  public ColorizedBulletRenderer(EntityRendererManager manager, int color) {
    super(manager);
    this.color = color;
  }

  @Override
  protected int getBlockLight(AbstractBulletEntity p_225624_1_, float p_225624_2_) {
    return 15;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void render(AbstractBulletEntity entity, float entityYaw, float partialTicks,
      MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
    matrixStack.push();
    float lvt_7_1_ = MathHelper.rotLerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
    float lvt_8_1_ = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
    float lvt_9_1_ = (float) entity.ticksExisted + partialTicks;
    matrixStack.translate(0.0D, 0.15000000596046448D, 0.0D);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(MathHelper.sin(lvt_9_1_ * 0.1F) * 180.0F));
    matrixStack.rotate(Vector3f.XP.rotationDegrees(MathHelper.cos(lvt_9_1_ * 0.1F) * 180.0F));
    matrixStack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(lvt_9_1_ * 0.15F) * 360.0F));
    float r = (float) ((this.color >> 16) & 0xFF) / 255F;
    float g = (float) ((this.color >> 8) & 0xFF) / 255F;
    float b = (float) ((this.color) & 0xFF) / 255F;
    RenderSystem.color4f(r, g, b, 0.5F);
    matrixStack.scale(-0.5F, -0.5F, 0.5F);
    this.model.setRotationAngles(entity, 0.0F, 0.0F, 0.0F, lvt_7_1_, lvt_8_1_);
    IVertexBuilder lvt_10_1_ = buffer.getBuffer(this.model.getRenderType(GENERIC_SPARK_TEXTURE));
    this.model
        .render(matrixStack, lvt_10_1_, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
            1.0F);
    matrixStack.scale(1.5F, 1.5F, 1.5F);
    IVertexBuilder lvt_11_1_ = buffer.getBuffer(renderType);
    this.model
        .render(matrixStack, lvt_11_1_, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
            0.15F);
    matrixStack.pop();
    super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
  }

  @Nonnull
  @Override
  public ResourceLocation getEntityTexture(@Nonnull AbstractBulletEntity entity) {
    return GENERIC_SPARK_TEXTURE;
  }
}
