/*
 * Copyright (C) 2018  C4
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

package c4.champions.common.util;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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

    private static Set<Integer> dimensions = Sets.newHashSet();
    private static Set<ResourceLocation> mobs = Sets.newHashSet();

    public static boolean isValidChampion(final Entity entity) {
        return entity instanceof EntityLiving && entity instanceof IMob && isValidEntity(entity);
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

    public static String generateRandomName() {
        int randomPrefix = rand.nextInt(16);
        int randomSuffix = rand.nextInt(16);
        String header = Champions.MODID + ".%s.%d";
        String prefix = new TextComponentTranslation(String.format(header, "prefix", randomPrefix)).getFormattedText();
        String suffix = new TextComponentTranslation(String.format(header, "suffix", randomSuffix)).getFormattedText();
        return prefix + suffix;
    }

    public static Set<String> generateAffixes(Rank rank, EntityLiving entityLivingIn, String... presets) {
        int size = rank.getAffixes();
        int tier = rank.getTier();
        Set<String> affixList = Sets.newHashSet();
        Map<AffixCategory, Set<String>> categoryMap = AffixRegistry.getCategoryMap().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));

        Set<String> curatedPresets = Sets.newHashSet(presets);
        curatedPresets.addAll(AffixFilterManager.getPresetAffixesForEntity(entityLivingIn));

        //Handle any preset affixes
        if (curatedPresets.size() > 0) {

            for (String s : curatedPresets) {
                AffixBase aff = AffixRegistry.getAffix(s);

                if (aff != null) {
                    AffixCategory cat = aff.getCategory();
                    Set<String> availableAffixes = categoryMap.get(cat);

                    if (availableAffixes.contains(s)) {
                        availableAffixes.remove(s);
                        boolean added = false;
                        AffixBase affix = AffixRegistry.getAffix(s);
                        if (affix != null && affix.canApply(entityLivingIn) && AffixFilterManager.isValidAffix(affix,
                                entityLivingIn, tier)) {
                            boolean flag = true;
                            //Check for incompatible affixes
                            for (String s1 : affixList) {

                                if (!affix.isCompatibleWith(AffixRegistry.getAffix(s1))) {
                                    flag = false;
                                    break;
                                }
                            }

                            if (flag) {
                                affixList.add(s);
                                added = true;
                            }
                        }

                        if (added && (availableAffixes.isEmpty() || cat != AffixCategory.OFFENSE)) {
                            categoryMap.remove(cat);
                        }
                    }
                }
            }
        }

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

                if (affix != null && affix.canApply(entityLivingIn) && AffixFilterManager.isValidAffix(affix,
                        entityLivingIn, tier)) {
                    boolean flag = true;
                    //Check for incompatible affixes
                    for (String s : affixList) {

                        if (!affix.isCompatibleWith(AffixRegistry.getAffix(s))) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        affixList.add(id);
                        added = true;
                    }
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

    public static boolean isValidEntity(Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);

        if (rl == null) {
            return false;
        } else if (mobs.isEmpty()) {
            return true;
        } else if (ConfigHandler.mobPermission == ConfigHandler.PermissionMode.BLACKLIST) {
            return !mobs.contains(rl);
        } else {
            return mobs.contains(rl);
        }
    }

    public static boolean isValidDimension(int dim) {

        if (dimensions.isEmpty()) {
            return true;
        } else if (ConfigHandler.dimensionPermission == ConfigHandler.PermissionMode.BLACKLIST) {
            return !dimensions.contains(dim);
        } else {
            return dimensions.contains(dim);
        }
    }

    public static void parseConfigs() {

        if (ConfigHandler.dimensionList.length > 0) {

            for (String s : ConfigHandler.dimensionList) {

                try {
                    dimensions.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    Champions.logger.log(Level.ERROR, "Non-integer found in dimension config! " + s);
                }
            }
        }

        if (ConfigHandler.mobList.length > 0) {

            for (String s : ConfigHandler.mobList) {
                ResourceLocation rl = new ResourceLocation(s);

                if (EntityList.getEntityNameList().contains(rl)) {
                    mobs.add(rl);
                } else {
                    Champions.logger.log(Level.ERROR, "Invalid entity found in mob config! " + s);
                }
            }
        }
    }
}
