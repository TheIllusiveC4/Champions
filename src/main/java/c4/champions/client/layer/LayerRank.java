package c4.champions.client.layer;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.Rank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class LayerRank implements LayerRenderer<EntityLiving>
{
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final RenderLiving renderLiving;
    private final ModelBase model;

    public <T extends ModelBase> LayerRank(RenderLiving renderLiving, ModelBase model) {
        this.renderLiving = renderLiving;
        this.model = model;
    }

    public void doRenderLayer(@Nonnull EntityLiving entitylivingIn, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IChampionship cap = CapabilityChampionship.getChampionship(entitylivingIn);

        if (cap != null && cap.getRank() != null) {
            Rank rank = cap.getRank();

            if (rank.getTier() > 0) {
                renderRankedGlint(renderLiving, entitylivingIn, model, limbSwing, limbSwingAmount,
                        partialTicks, ageInTicks, netHeadYaw, headPitch, scale, rank.getColor());
            }
        }
    }

    public static void renderRankedGlint(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase model,
                                         float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_,
                                         float p_188364_7_, float p_188364_8_, float p_188364_9_, int color) {
        float f = (float)p_188364_1_.ticksExisted + p_188364_5_;
        p_188364_0_.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            float r = (float)((color>>16)&0xFF)/255f;
            float g = (float)((color>>8)&0xFF)/255f;
            float b = (float)((color)&0xFF)/255f;
            GlStateManager.color(r, g, b, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334F;
            GlStateManager.scale(f3, f3, f3);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            model.render(p_188364_1_, p_188364_3_, p_188364_4_, p_188364_6_, p_188364_7_, p_188364_8_, p_188364_9_);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
