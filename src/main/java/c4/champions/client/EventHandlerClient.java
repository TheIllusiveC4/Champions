package c4.champions.client;

import c4.champions.Champions;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class EventHandlerClient {

    @SubscribeEvent
    public void renderAffixedName(RenderLivingEvent.Specials.Pre evt) {
        EntityLivingBase entity = evt.getEntity();

        if (ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);

            if (chp != null) {
                ImmutableSet<String> affixes = chp.getAffixes();

                if (!affixes.isEmpty() && this.canRenderName(entity)) {
                    double d0 = entity.getDistanceSq(Minecraft.getMinecraft().getRenderManager().renderViewEntity);
                    float f = entity.isSneaking() ? RenderLivingBase.NAME_TAG_RANGE_SNEAK : RenderLivingBase.NAME_TAG_RANGE;


                    if (d0 < (double) (f * f)) {
                        String s = buildAffixedName(affixes, entity.getDisplayName().getFormattedText());
                        GlStateManager.alphaFunc(516, 0.1F);
                        renderAffixLabel(chp.getRank().getColor(), entity, s, evt.getX(), evt.getY(), evt.getZ(),
                                64);
                    }
                }
            }
        }
    }

    private String buildAffixedName(Set<String> affixes, String entityName) {
        StringBuilder builder = new StringBuilder();

        for (String s : affixes) {
            builder.append(I18n.format(Champions.MODID + ".affix." + s));
            builder.append(" ");
        }
        builder.append(entityName);
        return builder.toString();
    }

    private boolean canRenderName(EntityLivingBase entity) {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        return Minecraft.isGuiEnabled() && entity != Minecraft.getMinecraft().getRenderManager().renderViewEntity
                && entityplayersp.canEntityBeSeen(entity) && !entity.isInvisibleToPlayer(entityplayersp)
                && !entity.isBeingRidden() && !entity.hasCustomName();
    }

    private void renderAffixLabel(int color, EntityLivingBase entityIn, String str, double x, double y, double z,
                                  int maxDistance) {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        double d0 = entityIn.getDistanceSq(manager.renderViewEntity);

        if (d0 <= (double)(maxDistance * maxDistance)) {
            boolean flag = entityIn.isSneaking();
            float f = manager.playerViewY;
            float f1 = manager.playerViewX;
            boolean flag1 = manager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
            int i = "deadmau5".equals(str) ? -10 : 0;
            ChampionHelper.drawNameplate(color, manager.getFontRenderer(), str, (float)x, (float)y + f2, (float)z, i, f,
                    f1, flag1, flag);
        }
    }
}
