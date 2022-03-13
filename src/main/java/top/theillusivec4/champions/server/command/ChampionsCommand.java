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

import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.impl.ChampionsApiImpl;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.item.ChampionEggItem;
import top.theillusivec4.champions.common.registry.ChampionsRegistry;
import top.theillusivec4.champions.common.util.ChampionBuilder;

public class ChampionsCommand {

  public static final SuggestionProvider<CommandSourceStack> AFFIXES = SuggestionProviders
      .register(new ResourceLocation(Champions.MODID, "affices"),
          (context, builder) -> SharedSuggestionProvider.suggest(
            ChampionsApiImpl.getInstance().getAffixes().stream().map(IAffix::getIdentifier), builder));

    public static final SuggestionProvider<CommandSourceStack> MONSTER_ENTITIES = SuggestionProviders
      .register(new ResourceLocation(Champions.MODID, "monster_entities"),
        (context, builder) -> SharedSuggestionProvider.suggestResource(
          ForgeRegistries.ENTITIES.getValues().stream()
            .filter(type -> type.getCategory() == MobCategory.MONSTER),
          builder, EntityType::getKey,
          (type) -> new TranslatableComponent(Util.makeDescriptionId("entity", EntityType.getKey(type)))));


    private static final DynamicCommandExceptionType UNKNOWN_ENTITY = new DynamicCommandExceptionType(
      type -> new TranslatableComponent("command.champions.egg.unknown_entity", type));

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    final int opPermissionLevel = 2;
    LiteralArgumentBuilder<CommandSourceStack> championsCommand = Commands.literal("champions")
        .requires(player -> player.hasPermission(opPermissionLevel));

    championsCommand.then(Commands.literal("egg").then(
        Commands.argument("entity", EntitySummonArgument.id()).suggests(MONSTER_ENTITIES)
            .then(Commands.argument("tier", IntegerArgumentType.integer()).executes(
                context -> createEgg(context.getSource(),
                    EntitySummonArgument.getSummonableEntity(context, "entity"),
                    IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).suggests(AFFIXES).executes(
                    context -> createEgg(context.getSource(),
                        EntitySummonArgument.getSummonableEntity(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes")))))));

    championsCommand.then(Commands.literal("summon").then(
        Commands.argument("entity", EntitySummonArgument.id()).suggests(MONSTER_ENTITIES)
            .then(Commands.argument("tier", IntegerArgumentType.integer()).executes(
                context -> summon(context.getSource(),
                    EntitySummonArgument.getSummonableEntity(context, "entity"),
                    IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                Commands.argument("affixes", AffixArgument.affix()).suggests(AFFIXES).executes(
                    context -> summon(context.getSource(),
                        EntitySummonArgument.getSummonableEntity(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"),
                        AffixArgument.getAffixes(context, "affixes")))))));

    championsCommand.then(Commands.literal("summonpos").then(
        Commands.argument("pos", BlockPosArgument.blockPos()).then(
            Commands.argument("entity", EntitySummonArgument.id())
                .suggests(MONSTER_ENTITIES).then(
                Commands.argument("tier", IntegerArgumentType.integer()).executes(
                    context -> summon(context.getSource(),
                        BlockPosArgument.getSpawnablePos(context, "pos"),
                        EntitySummonArgument.getSummonableEntity(context, "entity"),
                        IntegerArgumentType.getInteger(context, "tier"), new ArrayList<>())).then(
                    Commands.argument("affixes", AffixArgument.affix()).suggests(AFFIXES).executes(
                        context -> summon(context.getSource(),
                            BlockPosArgument.getSpawnablePos(context, "pos"),
                            EntitySummonArgument.getSummonableEntity(context, "entity"),
                            IntegerArgumentType.getInteger(context, "tier"),
                            AffixArgument.getAffixes(context, "affixes"))))))));

    dispatcher.register(championsCommand);
  }

  private static int summon(CommandSourceStack source, ResourceLocation resourceLocation, int tier,
      Collection<IAffix> affixes) throws CommandSyntaxException {
    return summon(source, null, resourceLocation, tier, affixes);
  }

  private static int summon(CommandSourceStack source, @Nullable BlockPos pos,
      ResourceLocation resourceLocation, int tier, Collection<IAffix> affixes)
      throws CommandSyntaxException {
    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(resourceLocation);


        if (entityType == null)
        {
            throw UNKNOWN_ENTITY.create(resourceLocation);
        }
        else
        {
            final Entity sourceEntity = source.getEntity();
            Entity entity = entityType.create((ServerLevel) sourceEntity.getLevel(), null, null, null,
              pos != null ? pos : new BlockPos(sourceEntity.blockPosition()), MobSpawnType.COMMAND, false, false);

            if (entity instanceof LivingEntity)
            {
                ChampionCapability.getCapability((LivingEntity) entity).ifPresent(
                  champion -> ChampionBuilder.spawnPreset(champion, tier, new ArrayList<>(affixes)));
               source.getLevel().addFreshEntity(entity);
                source.sendSuccess(new TranslatableComponent("commands.champions.summon.success",
                  new TranslatableComponent("rank.champions.title." + tier).getString() + " " + entity
                    .getDisplayName().getString()), false);
            }
        }

    return Command.SINGLE_SUCCESS;
  }

  private static int createEgg(CommandSourceStack source, ResourceLocation resourceLocation, int tier,
      Collection<IAffix> affixes) throws CommandSyntaxException {
    EntityType<?> entity = ForgeRegistries.ENTITIES.getValue(resourceLocation);

    if (entity == null) {
      throw UNKNOWN_ENTITY.create(resourceLocation);
    } else if (source.getEntity() instanceof ServerPlayer) {
      ServerPlayer playerEntity = (ServerPlayer) source.getEntity();
      ItemStack egg = new ItemStack(ChampionsRegistry.EGG);
      ChampionEggItem.write(egg, resourceLocation, tier, affixes);
      ItemHandlerHelper.giveItemToPlayer(playerEntity, egg, 1);
      source.sendSuccess(new TranslatableComponent("commands.champions.egg.success", egg.getDisplayName()), false);
    }
    return Command.SINGLE_SUCCESS;
  }
}
