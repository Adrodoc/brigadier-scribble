package de.adrodoc.brigadier.argument.type.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.adrodoc.brigadier.StringReaderUtils;

public class ResourceLocationArgumentType implements ArgumentType<String> {
  @Override
  public <S> String parse(StringReader reader) throws CommandSyntaxException {
    return StringReaderUtils.readId(reader);
  }
}
