package top.theillusivec4.champions.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.server.command.AffixArgumentType.IAffixProvider;

public class AffixArgumentType implements ArgumentType<IAffixProvider> {

  private static final Collection<String> EXAMPLES = Arrays.asList("molten", "reflecting");
  private static final DynamicCommandExceptionType UNKNOWN_AFFIX = new DynamicCommandExceptionType(
      type -> new TranslationTextComponent("argument.champions.affix.unknown", type));

  public static AffixArgumentType affix() {
    return new AffixArgumentType();
  }

  public static Collection<IAffix> getAffixes(CommandContext<CommandSource> context, String name)
      throws CommandSyntaxException {
    return context.getArgument(name, IAffixProvider.class).getAffixes(context.getSource());
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return ISuggestionProvider
        .suggest(Champions.API.getAffixes().stream().map(IAffix::getIdentifier), builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }

  @Override
  public IAffixProvider parse(StringReader reader) {
    int i = reader.getCursor();

    while (reader.canRead() && reader.peek() != ' ') {
      reader.skip();
    }
    String s = reader.getString().substring(i, reader.getCursor());
    return (source) -> Collections
        .singletonList(Champions.API.getAffix(s).orElseThrow(() -> UNKNOWN_AFFIX.create(s)));
  }

  @FunctionalInterface
  public interface IAffixProvider {

    Collection<IAffix> getAffixes(CommandSource source) throws CommandSyntaxException;
  }
}
