package c4.champions.common.util;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ChampionHelper {

    public static Random rand = new Random();

    public static boolean isValidChampion(final Entity entity) {
        return entity instanceof EntityLiving && entity instanceof IMob;
    }

    public static Rank generateRank(final EntityLiving entityLivingIn) {
        ImmutableMap<Integer, Rank> ranks = RankManager.getRanks();
        int finalTier = 0;

        if (!nearActiveBeacon(entityLivingIn)) {

            for (Integer tier : ranks.keySet()) {

                if (rand.nextFloat() < ranks.get(tier).getChance()) {
                    finalTier = tier;
                } else {
                    break;
                }
            }
        }
        return finalTier == 0 ? RankManager.getEmptyRank() : ranks.get(finalTier);
    }

    public static Set<String> generateAffixes(Rank rank, EntityLiving entityLivingIn) {
        int size = rank.getAffixes();
        int tier = rank.getTier();
        Set<String> affixList = Sets.newHashSet();
        Map<AffixCategory, Set<String>> categoryMap = AffixRegistry.getCategoryMap().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));

        while (!categoryMap.isEmpty() && affixList.size() < size) {
            //Get random category
            AffixCategory[] categories = categoryMap.keySet().toArray(new AffixCategory[0]);
            AffixCategory randomCategory = categories[rand.nextInt(categories.length)];
            //Get all affixes for that category
            Set<String> affixes = categoryMap.get(randomCategory);

            if (!affixes.isEmpty()) {
                //Get random affix
                int element = rand.nextInt(affixes.size());
                Iterator<String> iter = affixes.iterator();

                for (int i = 0; i < element; i++) {
                    iter.next();
                }
                String id = iter.next();
                boolean added = false;

                //Filter through for validity
                AffixBase affix = AffixRegistry.getAffix(id);

                if (affix != null && AffixFilterManager.isValidAffix(affix, entityLivingIn, tier)) {
                    affixList.add(id);
                    added = true;
                }

                //Remove entire category only if the affix was actually added and the category is limited
                if (added && randomCategory != AffixCategory.OFFENSE) {
                    categoryMap.remove(randomCategory);
                } //Otherwise, remove the affix from the running set and then remove the category if it's now empty
                else {
                    affixes.remove(id);

                    if (affixes.isEmpty()) {
                        categoryMap.remove(randomCategory);
                    }
                }
            }
        }
        return affixList;
    }

    public static boolean isElite(Rank rank) {
        return rank != null && rank.getTier() > 0;
    }

    public static void drawNameplate(int color, FontRenderer fontRendererIn, String str, float x, float y, float z, int
            verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking)
        {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        float r = (float)((color>>16)&0xFF)/255f;
        float g = (float)((color>>8)&0xFF)/255f;
        float b = (float)((color)&0xFF)/255f;
        bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(r, g, b, 0.5F)
                .endVertex();
        bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(r, g, b, 0.5F)
                .endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(r, g, b, 0.5F)
                .endVertex();
        bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(r, g, b, 0.5F)
                .endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        if (!isSneaking)
        {
            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
            GlStateManager.enableDepth();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static final Field IS_COMPLETE = ReflectionHelper.findField(TileEntityBeacon.class,
            "isComplete", "field_146015_k");

    private static boolean nearActiveBeacon(final EntityLiving entityLivingIn) {
        BlockPos pos = entityLivingIn.getPosition();
        int xPos = pos.getX();
        int yPos = pos.getY();
        int zPos = pos.getZ();

        for (int x = -24; x <= 24; x++) {

            for (int z = -24; z <= 24; z++) {

                for (int y = -24; y <= 24; y++) {
                    BlockPos blockpos = new BlockPos(xPos + x, yPos + y, zPos + z);
                    TileEntity te = entityLivingIn.world.getTileEntity(blockpos);

                    if (te instanceof TileEntityBeacon) {
                        TileEntityBeacon beacon = (TileEntityBeacon)te;
                        boolean flag = false;

                        try {
                            flag = IS_COMPLETE.getBoolean(beacon);
                        } catch (IllegalAccessException e) {
                            Champions.logger.log(Level.ERROR, "Error reading isComplete from beacon!");
                        }
                        return flag;
                    }
                }
            }
        }
        return false;
    }
}
