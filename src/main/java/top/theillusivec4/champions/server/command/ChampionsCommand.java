package top.theillusivec4.champions.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;

public class ChampionsCommand {

  private static final DynamicCommandExceptionType UNKNOWN_ENTITY = new DynamicCommandExceptionType(
      type -> new TranslationTextComponent("command.champions.egg.unknown_entity", type));

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
                IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).executes(
                    context -> createEgg(context.getSource(),
                        EntitySummonArgument.getEntityId(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes")))))));

    dispatcher.register(championsCommand);
  }

  private static int createEgg(CommandSource source, ResourceLocation resourceLocation, int tier,
      Collection<IAffix> affixes) throws CommandSyntaxException {
    EntityType<?> entity = ForgeRegistries.ENTITIES.getValue(resourceLocation);

    if (entity == null) {
      throw UNKNOWN_ENTITY.create(resourceLocation);
    } else if (source.getEntity() instanceof ServerPlayerEntity) {
      ServerPlayerEntity playerEntity = (ServerPlayerEntity) source.getEntity();
      ItemStack egg = new ItemStack(ChampionsRegistry.EGG);
      ChampionEggItem.write(egg, resourceLocation, tier, affixes);
      ItemHandlerHelper.giveItemToPlayer(playerEntity, egg, playerEntity.inventory.currentItem);
      source.sendFeedback(
          new TranslationTextComponent("commands.champions.egg.success", entity.getName()), true);
    }
    return Command.SINGLE_SUCCESS;
  }
}
