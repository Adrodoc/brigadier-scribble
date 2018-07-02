package de.adrodoc.brigadier.argument.type.minecraft.block.state;

import static de.adrodoc.brigadier.exceptions.MoreExceptions.MORE_EXCEPTIONS;
import static java.util.Objects.requireNonNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.adrodoc.brigadier.DataContext;
import de.adrodoc.brigadier.StringReaderUtils;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.BlockDoesNotAcceptValueForPropertyException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.BlockDoesNotHavePropertyException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedCharException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedClosingCurlyBracketException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedClosingSquareBracketException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedKeyException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedValueException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedValueForPropertyOnBlockException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.UnknownBlockTypeException;
import de.adrodoc.brigadier.exceptions.ParseException;

public class BlockStateArgumentType implements ArgumentType<Void> {
  private final DataContext data;

  public BlockStateArgumentType(DataContext data) {
    this.data = requireNonNull(data, "data == null!");
  }

  @Override
  public <S> Void parse(StringReader reader) throws CommandSyntaxException {
    try {
      parseInternal(reader);
    } catch (BlockDoesNotAcceptValueForPropertyException e) {
      throw MORE_EXCEPTIONS.blockDoesNotAcceptValueForProperty().create(e.blockType,
          e.propertyValue, e.propertyName);
    } catch (BlockDoesNotHavePropertyException e) {
      throw MORE_EXCEPTIONS.blockDoesNotHaveProperty().createWithContext(reader, e.blockType,
          e.propertyName);
    } catch (ExpectedClosingSquareBracketException e) {
      throw MORE_EXCEPTIONS.expectedClosingSquareBracket().createWithContext(reader);
    } catch (ExpectedValueForPropertyOnBlockException e) {
      throw MORE_EXCEPTIONS.expectedValueForPropertyOnBlock().createWithContext(reader,
          e.propertyName, e.blockType);
    } catch (UnknownBlockTypeException e) {
      throw MORE_EXCEPTIONS.unknownBlockType().createWithContext(reader, e.blockType);
    } catch (ExpectedCharException e) {
      throw MORE_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, e.c);
    } catch (ExpectedKeyException e) {
      throw MORE_EXCEPTIONS.expectedKey().createWithContext(reader);
    } catch (ExpectedValueException e) {
      throw MORE_EXCEPTIONS.expectedValue().createWithContext(reader);
    }
    return null;
  }

  private void parseInternal(StringReader reader) throws CommandSyntaxException,
      BlockDoesNotAcceptValueForPropertyException, BlockDoesNotHavePropertyException,
      ExpectedClosingSquareBracketException, ExpectedCharException, ExpectedKeyException,
      ExpectedValueException, UnknownBlockTypeException {
    String blockType = StringReaderUtils.readId(reader);
    if (!data.getBlockTypes().contains(blockType)) {
      throw new UnknownBlockTypeException(blockType);
    }
    Map<String, String> blockProperties = readBlockProperties(reader, blockType);
    JsonObject nbt = readNbt(reader);
  }

  private Map<String, String> readBlockProperties(StringReader reader, String blockType)
      throws CommandSyntaxException, BlockDoesNotAcceptValueForPropertyException,
      BlockDoesNotHavePropertyException, ExpectedClosingSquareBracketException,
      ExpectedValueForPropertyOnBlockException {
    if (!reader.canRead() || reader.peek() != '[') {
      return Collections.emptyMap();
    }
    reader.skip();
    reader.skipWhitespace();

    Map<String, String> result = new HashMap<>();
    while (reader.canRead() && reader.peek() != ']') {
      String propertyName = reader.readString();
      Set<String> possibleProperties = data.getBlockProperties(blockType);
      if (!possibleProperties.contains(propertyName)) {
        throw new BlockDoesNotHavePropertyException(blockType, propertyName);
      }
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != '=') {
        throw new ExpectedValueForPropertyOnBlockException(propertyName, blockType);
      }
      reader.skip();
      reader.skipWhitespace();
      String propertyValue = reader.readString();
      Set<String> possibleValues = data.getBlockPropertyValues(blockType, propertyName);
      if (!possibleValues.contains(propertyValue)) {
        throw new BlockDoesNotAcceptValueForPropertyException(blockType, propertyValue,
            propertyName);
      }
      result.put(propertyName, propertyValue);
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != ',') {
        break;
      }
      reader.skip();
      reader.skipWhitespace();
    }
    if (!reader.canRead() || reader.peek() != ']') {
      throw new ExpectedClosingSquareBracketException(blockType, result.keySet());
    }
    reader.skip();
    return result;
  }

  private JsonObject readNbt(StringReader reader) throws CommandSyntaxException,
      ExpectedCharException, ExpectedKeyException, ExpectedValueException {
    if (!reader.canRead() || reader.peek() != '{') {
      return new JsonObject();
    } else {
      return readObject(reader);
    }
  }

  private JsonElement readJson(StringReader reader) throws CommandSyntaxException,
      ExpectedCharException, ExpectedKeyException, ExpectedValueException {
    if (!reader.canRead()) {
      throw new ExpectedValueException();
    }
    char peek = reader.peek();
    switch (peek) {
      case '{':
        return readObject(reader);
      case '[':
        return readArray(reader);
      default:
        String string = reader.readString();
        if (string.isEmpty()) {
          throw new ExpectedValueException();
        }
        return new JsonPrimitive(string);
    }
  }

  private JsonObject readObject(StringReader reader) throws CommandSyntaxException,
      ExpectedCharException, ExpectedKeyException, ExpectedValueException {
    if (!reader.canRead() || reader.peek() != '{') {
      throw new ExpectedValueException();
    }
    reader.skip();
    reader.skipWhitespace();

    JsonObject result = new JsonObject();
    while (reader.canRead() && reader.peek() != '}') {
      String key = reader.readString();
      if (key.isEmpty()) {
        throw new ExpectedKeyException();
      }
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != ':') {
        throw new ExpectedCharException(':');
      }
      reader.skip();
      reader.skipWhitespace();
      JsonElement value = readJson(reader);
      result.add(key, value);
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != ',') {
        break;
      }
      reader.skip();
      reader.skipWhitespace();
    }
    if (!reader.canRead() || reader.peek() != '}') {
      throw new ExpectedClosingCurlyBracketException();
    }
    reader.skip();
    return result;
  }

  private JsonElement readArray(StringReader reader) throws CommandSyntaxException,
      ExpectedCharException, ExpectedKeyException, ExpectedValueException {
    if (!reader.canRead() || reader.peek() != '[') {
      throw new ExpectedValueException();
    }
    reader.skip();
    reader.skipWhitespace();

    JsonArray result = new JsonArray();
    while (reader.canRead() && reader.peek() != ']') {
      JsonElement element = readJson(reader);
      result.add(element);
      reader.skipWhitespace();
      if (!reader.canRead() || reader.peek() != ',') {
        break;
      }
      reader.skip();
      reader.skipWhitespace();
    }
    if (!reader.canRead() || reader.peek() != ']') {
      throw new ExpectedCharException(']');
    }
    reader.skip();
    return result;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    String remaining = builder.getRemaining();
    StringReader reader = new StringReader(remaining);
    try {
      parseInternal(reader);
    } catch (CommandSyntaxException e) {
      return builder.buildFuture();
    } catch (ParseException pe) {
      if (reader.canRead()) {
        return builder.buildFuture();
      } else {
        try {
          throw pe;
        } catch (BlockDoesNotAcceptValueForPropertyException e) {
          Set<String> possibleValues = data.getBlockPropertyValues(e.blockType, e.propertyName);
          suggestValuesStartingWith(builder, e.propertyValue, possibleValues);
        } catch (BlockDoesNotHavePropertyException e) {
          Set<String> possibleProperties = data.getBlockProperties(e.blockType);
          suggestValuesStartingWith(builder, e.propertyName, possibleProperties);
        } catch (ExpectedClosingCurlyBracketException e) {
          builder.suggest(String.valueOf(e.c));
          if (!remaining.trim().endsWith(",")) {
            builder.suggest(",");
          } else {
            builder.suggest("key");
            // TODO: handle exception
          }
        } catch (ExpectedClosingSquareBracketException e) {
          builder.suggest(String.valueOf(e.c));
          Set<String> possibleProperties = data.getBlockProperties(e.blockType);
          SetView<String> unusedProperties = Sets.difference(possibleProperties, e.blockProperties);
          if (!e.blockProperties.isEmpty() && !unusedProperties.isEmpty()
              && !remaining.trim().endsWith(",")) {
            builder.suggest(",");
          } else {
            suggestValues(builder, unusedProperties);
          }
        } catch (ExpectedCharException e) {
          builder.suggest(String.valueOf(e.c));
        } catch (ExpectedKeyException e) {
          builder.suggest("key");
        } catch (ExpectedValueException e) {
          builder.suggest("value");
        } catch (UnknownBlockTypeException e) {
          suggestValuesStartingWith(builder, e.blockType, data.getBlockTypes());
        }
      }
    }
    return builder.buildFuture();
  }

  private void suggestValuesStartingWith(SuggestionsBuilder builder, String input,
      Iterable<String> possibleSuggestions) {
    String prefix = input.toLowerCase();
    Iterable<String> suggestions =
        Iterables.filter(possibleSuggestions, possibleValue -> possibleValue.startsWith(prefix));
    suggestValues(builder, suggestions);
  }

  private void suggestValues(SuggestionsBuilder builder, Iterable<String> suggestions) {
    for (String suggestion : suggestions) {
      builder.suggest(suggestion);
    }
  }
}
