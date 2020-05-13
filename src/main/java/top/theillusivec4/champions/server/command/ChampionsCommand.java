package top.theillusivec4.champions.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import top.theillusivec4.champions.common.rank.RankManager;

public class ChampionsCommand {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    final int opPermissionLevel = ServerLifecycleHooks.getCurrentServer().getOpPermissionLevel();
    LiteralArgumentBuilder<CommandSource> championsCommand = Commands.literal("champions")
        .requires(player -> player.hasPermissionLevel(opPermissionLevel));

    championsCommand.then(Commands.literal("egg").then(
        Commands.argument("entity", EntitySummonArgument.entitySummon())
            .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).then(Commands.argument("tier",
            IntegerArgumentType.integer(RankManager.getLowestTier(), RankManager.getHighestTier()))
            .executes(context -> createEgg(context.getSource(),
                EntitySummonArgument.getEntityId(context, "entity"),
                IntegerArgumentType.getInteger(context, "tier"))))));
  }

  private static int createEgg(CommandSource source, ResourceLocation resourceLocation, int tier) {
    return Command.SINGLE_SUCCESS;
  }
}
