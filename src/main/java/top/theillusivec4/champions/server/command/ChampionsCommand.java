package top.theillusivec4.champions.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.rank.RankManager;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.util.ChampionBuilder;

public class ChampionsCommand {

  public static final SuggestionProvider<CommandSource> MONSTER_ENTITIES = SuggestionProviders
      .register(new ResourceLocation(Champions.MODID, "monster_entities"),
          (context, builder) -> ISuggestionProvider.func_201725_a(
              ForgeRegistries.ENTITIES.getValues().stream()
                  .filter(type -> type.getClassification() == EntityClassification.MONSTER),
              builder, EntityType::getKey, (type) -> new TranslationTextComponent(
                  Util.makeTranslationKey("entity", EntityType.getKey(type)))));

  private static final DynamicCommandExceptionType UNKNOWN_ENTITY = new DynamicCommandExceptionType(
      type -> new TranslationTextComponent("command.champions.egg.unknown_entity", type));

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    final int opPermissionLevel = ServerLifecycleHooks.getCurrentServer().getOpPermissionLevel();
    LiteralArgumentBuilder<CommandSource> championsCommand = Commands.literal("champions")
        .requires(player -> player.hasPermissionLevel(opPermissionLevel));

    championsCommand.then(Commands.literal("egg").then(
        Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(MONSTER_ENTITIES)
            .then(Commands.argument("tier", IntegerArgumentType
                .integer(RankManager.getLowestRank().getTier(),
                    RankManager.getHighestRank().getTier())).executes(
                context -> createEgg(context.getSource(),
                    EntitySummonArgument.getEntityId(context, "entity"),
                    IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).executes(
                    context -> createEgg(context.getSource(),
                        EntitySummonArgument.getEntityId(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes")))))));

    championsCommand.then(Commands.literal("summon").then(
        Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(MONSTER_ENTITIES)
            .then(Commands.argument("tier", IntegerArgumentType
                .integer(RankManager.getLowestRank().getTier(),
                    RankManager.getHighestRank().getTier())).executes(
                context -> summon(context.getSource(),
                    EntitySummonArgument.getEntityId(context, "entity"),
                    IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).executes(
                    context -> summon(context.getSource(),
                        EntitySummonArgument.getEntityId(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes")))))));

    championsCommand.then(Commands.literal("summonpos").then(
        Commands.argument("pos", BlockPosArgument.blockPos()).then(
            Commands.argument("entity", EntitySummonArgument.entitySummon())
                .suggests(MONSTER_ENTITIES).then(Commands.argument("tier", IntegerArgumentType
                .integer(RankManager.getLowestRank().getTier(),
                    RankManager.getHighestRank().getTier())).executes(
                context -> summon(context.getSource(), BlockPosArgument.getBlockPos(context, "pos"),
                    EntitySummonArgument.getEntityId(context, "entity"),
                    IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).executes(
                    context -> summon(context.getSource(),
                        BlockPosArgument.getBlockPos(context, "pos"),
                        EntitySummonArgument.getEntityId(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes"))))))));

    dispatcher.register(championsCommand);
  }

  private static int summon(CommandSource source, ResourceLocation resourceLocation, int tier,
      Collection<IAffix> affixes) throws CommandSyntaxException {
    return summon(source, null, resourceLocation, tier, affixes);
  }

  private static int summon(CommandSource source, @Nullable BlockPos pos,
      ResourceLocation resourceLocation, int tier, Collection<IAffix> affixes)
      throws CommandSyntaxException {
    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(resourceLocation);

    if (entityType == null) {
      throw UNKNOWN_ENTITY.create(resourceLocation);
    } else {
      Entity entity = entityType.create(source.func_197023_e(), null, null, null,
          pos != null ? pos : new BlockPos(source.getPos()), SpawnReason.COMMAND, false, false);

      if (entity instanceof LivingEntity) {
        ChampionCapability.getCapability((LivingEntity) entity).ifPresent(
            champion -> ChampionBuilder.spawnPreset(champion, tier, new ArrayList<>(affixes)));
        source.func_197023_e().addEntity(entity);
        source.sendFeedback(new TranslationTextComponent("commands.champions.summon.success",
            new TranslationTextComponent("rank.champions.title." + tier).getString() + " " + entity
                .getDisplayName().getString()), true);
      }
    }
    return Command.SINGLE_SUCCESS;
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
          new TranslationTextComponent("commands.champions.egg.success", egg.getDisplayName()),
          true);
    }
    return Command.SINGLE_SUCCESS;
  }
}
