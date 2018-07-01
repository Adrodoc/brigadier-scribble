package de.adrodoc.brigadier.argument.type.minecraft;

import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ColorArgumentType extends ExhaustiveExamplesArgumentType<String> {
  private static final ImmutableSet<String> EXAMPLES = ImmutableSet.of(//
      "aqua", //
      "blue", //
      "dark_aqua", //
      "dark_blue", //
      "dark_gray", //
      "dark_green", //
      "dark_purple", //
      "dark_red", //
      "gold", //
      "gray", //
      "green", //
      "light_purple", //
      "red", //
      "reset", //
      "white", //
      "yellow"//
  );

  @Override
  public <S> String parse(StringReader reader) throws CommandSyntaxException {
    return reader.readUnquotedString().toLowerCase();
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
