/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.client;

import c4.champions.Champions;
import c4.champions.common.capability.IChampionship;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ClientUtil {

    private static final Minecraft client = FMLClientHandler.instance().getClient();
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private static final ResourceLocation GUI_STAR = new ResourceLocation(Champions.MODID, "textures/gui/staricon" +
            ".png");

    public static RayTraceResult getMouseOver(float partialTicks, float distance) {
        Entity entity = client.getRenderViewEntity();

        if (entity != null)
        {
            if (client.world != null)
            {
                RayTraceResult objectMouseOver = entity.rayTrace(distance, partialTicks);
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                int i = 3;
                double d1 = distance;

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
                Entity pointedEntity = null;
                Vec3d vec3d3 = null;
                float f = 1.0F;
                List<Entity> list = client.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand
                        (vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance).grow(1.0D, 1.0D, 1.0D), Predicates.and
                        (EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(@Nullable Entity p_apply_1_)
                    {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j)
                {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)
                            entity1.getCollisionBorderSize() + 0.5d);
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if (axisalignedbb.contains(vec3d))
                    {
                        if (d2 >= 0.0D)
                        {
                            pointedEntity = entity1;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (raytraceresult != null)
                    {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                        if (d3 < d2 || d2 == 0.0D)
                        {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
                            {
                                if (d2 == 0.0D)
                                {
                                    pointedEntity = entity1;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            }
                            else
                            {
                                pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (pointedEntity != null && vec3d.distanceTo(vec3d3) > distance)
                {
                    pointedEntity = null;
                    objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, EnumFacing.UP, new BlockPos(vec3d3));
                }

                if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
                {
                    objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
                }
                return objectMouseOver;
            }
        }
        return null;
    }

    public static void renderChampionHealth(EntityLiving living, IChampionship chp) {
        ScaledResolution scaledresolution = new ScaledResolution(client);
        int i = scaledresolution.getScaledWidth();
        int k = i / 2 - 91;
        int j = 21;
        int color = chp.getRank().getColor();
        float r = (float)((color>>16)&0xFF)/255f;
        float g = (float)((color>>8)&0xFF)/255f;
        float b = (float)((color)&0xFF)/255f;
        GlStateManager.color(r, g, b, 1.0F);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
        render(k, j, living.getHealth() / living.getMaxHealth());
        client.getTextureManager().bindTexture(GUI_STAR);
        int num = chp.getRank().getTier();

        if (num <= 18) {
            int startX = i / 2 - 5 - 5 * (num - 1);
            for (int tier = 0; tier < num; tier++) {
                Gui.drawModalRectWithCustomSizedTexture(startX, 1, 0, 0, 9, 9, 9, 9);
                startX += 10;
            }
        } else {
            int startX = i / 2 - 5;
            String count = "x" + num;
            Gui.drawModalRectWithCustomSizedTexture(startX - client.fontRenderer.getStringWidth(count) / 2, 1, 0, 0,
                    9, 9, 9, 9);
            client.fontRenderer.drawStringWithShadow(count, startX + 10 - client.fontRenderer.getStringWidth(count) /
                    2, 2, 16777215);

        }
        String s = living.hasCustomName() ? living.getDisplayName().getFormattedText() : chp.getName();
        client.fontRenderer.drawStringWithShadow(s, (float)(i / 2 - client.fontRenderer.getStringWidth(s) / 2),
                (float)(j - 9), color);
        GlStateManager.color(1, 1, 1, 1);
        StringBuilder builder = new StringBuilder();

        for (String aff : chp.getAffixes()) {
            builder.append(I18n.format(Champions.MODID + ".affix." + aff));
            builder.append(" ");
        }
        String affixes = builder.toString().trim();
        client.fontRenderer.drawStringWithShadow(affixes, (float)(i / 2 - client.fontRenderer.getStringWidth(affixes) / 2),
                (float)(j + 6), 16777215);
        GlStateManager.disableBlend();
    }

    private static void render(int x, int y, float percentHealth)
    {
        drawTexturedModalRect(x, y, 0, 60, 182, 5);
        int i = (int)(percentHealth * 183.0F);

        if (i > 0) {
            drawTexturedModalRect(x, y, 0, 65, i, 5);
        }
    }

    private static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float u = 0.00390625F;
        float v = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x), (double)(y + height), 0).tex((double)((float)(textureX) * u)
                , (double)((float)(textureY + height) * v)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0).tex((double)((float)(textureX + width) *
                u), (double)((float)(textureY + height) * v)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y), 0).tex((double)((float)(textureX + width) *
                u), (double)((float)(textureY) * v)).endVertex();
        bufferbuilder.pos((double)(x), (double)(y), 0).tex((double)((float)(textureX) * u),
                (double)((float)(textureY) * v)).endVertex();
        tessellator.draw();
    }
}
