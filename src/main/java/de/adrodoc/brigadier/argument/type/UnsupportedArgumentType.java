package de.adrodoc.brigadier.argument.type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class UnsupportedArgumentType implements ArgumentType<Void> {
  @Override
  public <S> Void parse(StringReader reader) throws CommandSyntaxException {
    throw new UnsupportedOperationException("Unsupported argument type");
  }
}
