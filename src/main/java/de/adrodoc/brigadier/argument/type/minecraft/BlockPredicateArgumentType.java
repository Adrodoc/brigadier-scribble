package de.adrodoc.brigadier.argument.type.minecraft;

import static java.util.Objects.requireNonNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.SetMultimap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.adrodoc.brigadier.StringReaderUtils;
import de.adrodoc.brigadier.SuggestionContext;
import de.adrodoc.brigadier.exceptions.MoreExceptions;

public class BlockPredicateArgumentType implements ArgumentType<Void> {
  private final SuggestionContext context;

  public BlockPredicateArgumentType(SuggestionContext context) {
    this.context = requireNonNull(context, "context == null!");
  }

  @Override
  public <S> Void parse(StringReader reader) throws CommandSyntaxException {
    String id;
    if (reader.canRead() && reader.peek() == '#') {
      reader.skip();
      id = StringReaderUtils.readId(reader);
      if (!context.getBlockTags().contains(id)) {
        throw MoreExceptions.INSTANCE.unknownBlockTag().createWithContext(reader, id);
      }
    } else {
      id = StringReaderUtils.readId(reader);
      if (!context.getBlockTypes().contains(id)) {
        throw MoreExceptions.INSTANCE.unknownBlockType().createWithContext(reader, id);
      }
    }
    Map<String, String> blockProperties = readBlockProperties(reader, id);

//    readNbt(reader, id);

    return null;
  }

  private Map<String, String> readBlockProperties(StringReader reader, String id)
      throws CommandSyntaxException {
    if (!reader.canRead() || reader.peek() != '[') {
      return Collections.emptyMap();
    }
    reader.skip();
    reader.skipWhitespace();

    Map<String, String> result = new HashMap<>();
    while (reader.canRead() && reader.peek() != ']') {
      String key = reader.readString();
      SetMultimap<String, String> possibleProperties = context.getPossibleBlockProperties(id);
      if (!possibleProperties.containsKey(key)) {
        throw MoreExceptions.INSTANCE.blockDoesNotHaveProperty().createWithContext(reader, id, key);
      }
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != '=') {
        throw MoreExceptions.INSTANCE.expectedValueForProperty().createWithContext(reader, key, id);
      }
      reader.skip();
      reader.skipWhitespace();
      String value = reader.readString();
      Set<String> possibleValues = possibleProperties.get(key);
      if (!possibleValues.contains(value)) {
        throw MoreExceptions.INSTANCE.blockDoesNotAcceptValue().create(id, value, key);
      }
      result.put(key, value);
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != ',') {
        break;
      }
      reader.skip();
      reader.skipWhitespace();
    }
    if (!reader.canRead() || reader.peek() != ']') {
      throw MoreExceptions.INSTANCE.expectedClosingSquareBracket().createWithContext(reader);
    }
    return result;
  }
}
