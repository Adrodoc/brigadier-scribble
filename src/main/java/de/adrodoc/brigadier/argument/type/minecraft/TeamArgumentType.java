package de.adrodoc.brigadier.argument.type.minecraft;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.adrodoc.brigadier.SuggestionContext;

public class TeamArgumentType extends ExhaustiveExamplesArgumentType<String> {
  private final SuggestionContext context;

  public TeamArgumentType(SuggestionContext context) {
    this.context = requireNonNull(context, "context == null!");
  }

  @Override
  public <S> String parse(StringReader reader) throws CommandSyntaxException {
    return reader.readUnquotedString();
  }

  @Override
  public Collection<String> getExamples() {
    return context.getTeams();
  }
}
