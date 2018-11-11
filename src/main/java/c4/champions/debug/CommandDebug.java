package c4.champions.debug;

import c4.champions.Champions;
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
