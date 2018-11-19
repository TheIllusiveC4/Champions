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

package c4.champions.debug;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Sets;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandDebug extends CommandBase {

    public CommandDebug() {}

    @Override
    @Nonnull
    public String getName() {
        return Champions.MODID;
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return Champions.MODID + " <entity> <tier> <affixes...>";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
            throws CommandException {

        if (args.length < 2) {
            return;
        }
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(args[0]), sender.getEntityWorld());

        if (entity == null || !(entity instanceof EntityLiving)) {
            return;
        }

        EntityLiving living = (EntityLiving)entity;

        int tier = Integer.parseInt(args[1]);
        Set<String> argAffix = Sets.newHashSet();

        for (int i = 2; i < args.length; i++) {
            argAffix.add(args[i]);
        }

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)sender;
            living.setPosition(player.posX + 1, player.posY, player.posZ);
            IChampionship chp = CapabilityChampionship.getChampionship(living);

            if (chp != null) {
                Rank rank = RankManager.getRankForTier(tier);
                chp.setRank(rank);

                if (rank.getTier() > 0) {

                    if (argAffix.isEmpty()) {
                        Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                        chp.setAffixes(affixes);
                    } else {
                        chp.setAffixes(argAffix);
                    }
                    chp.setName(ChampionHelper.generateRandomName());
                    chp.getRank().applyGrowth(living);

                    for (String s : chp.getAffixes()) {
                        IAffix affix = AffixRegistry.getAffix(s);

                        if (affix != null) {
                            affix.onInitialSpawn(living, chp);
                        }
                    }
                }
            }
            player.world.spawnEntity(living);
        }

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
