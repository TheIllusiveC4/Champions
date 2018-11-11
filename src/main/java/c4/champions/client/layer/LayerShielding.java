package c4.champions.client.layer;

import c4.champions.common.affix.affix.Affixes;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class LayerShielding implements LayerRenderer<EntityLiving>
{
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final RenderLiving renderLiving;
    private final ModelBase model;

    public <T extends ModelBase> LayerShielding(RenderLiving renderLiving, ModelBase model) {
        this.renderLiving = renderLiving;
        this.model = model;
    }

    public void doRenderLayer(@Nonnull EntityLiving entitylivingIn, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IChampionship cap = CapabilityChampionship.getChampionship(entitylivingIn);

        if (cap != null) {
            String identifier = Affixes.shielding.getIdentifier();

            if (cap.getAffixes().contains(Affixes.shielding.getIdentifier())) {
                AffixNBT.Boolean shielding = AffixNBT.getData(cap, identifier, AffixNBT.Boolean.class);

                if (shielding.mode) {
                    boolean flag = entitylivingIn.isInvisible();
                    GlStateManager.depthMask(!flag);
                    this.renderLiving.bindTexture(LIGHTNING_TEXTURE);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    float f = (float) entitylivingIn.ticksExisted + partialTicks;
                    GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
                    GlStateManager.matrixMode(5888);
                    GlStateManager.enableBlend();
                    float f1 = 0.5F;
                    GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                    this.model.setModelAttributes(this.renderLiving.getMainModel());
                    Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                    this.model.render(entitylivingIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                            scale);

                    Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.matrixMode(5888);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.depthMask(flag);
                }
            }
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}