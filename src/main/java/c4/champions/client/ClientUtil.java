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
import c4.champions.common.config.ConfigHandler;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

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
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                Vec3d vec3d1 = entity.getLook(partialTicks);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
                RayTraceResult objectMouseOver = rayTraceBlocks(entity.world, vec3d, vec3d2, false, false, true);
                int i = 3;
                double d1 = distance;

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3d);
                }
                Entity pointedEntity = null;
                Vec3d vec3d3 = null;
                float f = 1.0F;
                List<Entity> list = client.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand
                        (vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance).grow(1.0D, 1.0D, 1.0D), Predicates.and
                        (EntitySelectors.NOT_SPECTATING, (entity1) -> entity1 != null && entity1.canBeCollidedWith()));
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
        int xOffset = ConfigHandler.client.xOffset;
        int yOffset = ConfigHandler.client.yOffset;
        int color = chp.getRank().getColor();
        float r = (float)((color>>16)&0xFF)/255f;
        float g = (float)((color>>8)&0xFF)/255f;
        float b = (float)((color)&0xFF)/255f;
        GlStateManager.color(r, g, b, 1.0F);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
        render(xOffset + k, yOffset + j, living.getHealth() / living.getMaxHealth());
        client.getTextureManager().bindTexture(GUI_STAR);
        int num = chp.getRank().getTier();

        if (num <= 18) {
            int startX = xOffset + i / 2 - 5 - 5 * (num - 1);

            for (int tier = 0; tier < num; tier++) {
                Gui.drawModalRectWithCustomSizedTexture(startX, yOffset + 1, 0, 0, 9, 9, 9, 9);
                startX += 10;
            }
        } else {
            int startX = xOffset + i / 2 - 5;
            String count = "x" + num;
            Gui.drawModalRectWithCustomSizedTexture(startX - client.fontRenderer.getStringWidth(count) / 2, yOffset + 1, 0, 0,
                    9, 9, 9, 9);
            client.fontRenderer.drawStringWithShadow(count, startX + 10 - client.fontRenderer.getStringWidth(count) / 2.0f,
                    2, 16777215);

        }
        String s = living.hasCustomName() ? living.getDisplayName().getFormattedText() : chp.getName();
        client.fontRenderer.drawStringWithShadow(s, xOffset + (float)(i / 2 - client.fontRenderer.getStringWidth(s) / 2),
                yOffset + (float)(j - 9), color);
        GlStateManager.color(1, 1, 1, 1);
        StringBuilder builder = new StringBuilder();

        for (String aff : chp.getAffixes()) {
            builder.append(I18n.format(Champions.MODID + ".affix." + aff));
            builder.append(" ");
        }
        String affixes = builder.toString().trim();
        client.fontRenderer.drawStringWithShadow(affixes, xOffset + (float)(i / 2 - client.fontRenderer.getStringWidth(affixes) / 2),
                yOffset + (float)(j + 6), 16777215);
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

    @Nullable
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {

        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {

            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.getRenderLayer() == BlockRenderLayer.SOLID && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }
                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {

                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }
                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double)l + 1.0D;
                    } else if (i < l) {
                        d0 = (double)l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double)i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double)i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double)j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double)j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }
                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }
                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }
                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {

                        if (block1.getRenderLayer() == BlockRenderLayer.SOLID && block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if (raytraceresult1 != null) {
                                return raytraceresult1;
                            }
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
