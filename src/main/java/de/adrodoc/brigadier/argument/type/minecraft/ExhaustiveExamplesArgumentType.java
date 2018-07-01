package de.adrodoc.brigadier.argument.type.minecraft;

import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public abstract class ExhaustiveExamplesArgumentType<T> implements ArgumentType<T> {
  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    for (String example : getExamples()) {
      if (example.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
        builder.suggest(example);
      }
    }
    return builder.buildFuture();
  }
}
