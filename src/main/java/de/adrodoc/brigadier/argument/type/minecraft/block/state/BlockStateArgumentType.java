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
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedCharException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedKeyException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedValueException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedValueForPropertyOnBlockException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.IllegalBlockPropertyKeyException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.IllegalBlockPropertyValueException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.UnclosedBlockPropertiesException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.UnclosedCompoundNbtException;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.UnknownBlockTypeException;
import de.adrodoc.brigadier.argument.type.minecraft.nbt.NbtPath;
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
    } catch (ExpectedValueForPropertyOnBlockException e) {
      throw MORE_EXCEPTIONS.expectedValueForPropertyOnBlock().createWithContext(reader,
          e.propertyName, e.blockType);
    } catch (ExpectedCharException e) {
      throw MORE_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, e.c);
    } catch (ExpectedKeyException e) {
      throw MORE_EXCEPTIONS.expectedKey().createWithContext(reader);
    } catch (ExpectedValueException e) {
      throw MORE_EXCEPTIONS.expectedValue().createWithContext(reader);
    } catch (IllegalBlockPropertyKeyException e) {
      throw MORE_EXCEPTIONS.blockDoesNotHaveProperty().createWithContext(reader, e.blockType,
          e.propertyName);
    } catch (IllegalBlockPropertyValueException e) {
      throw MORE_EXCEPTIONS.blockDoesNotAcceptValueForProperty().create(e.blockType,
          e.propertyValue, e.propertyName);
    } catch (UnclosedBlockPropertiesException e) {
      throw MORE_EXCEPTIONS.expectedClosingSquareBracket().createWithContext(reader);
    } catch (UnclosedCompoundNbtException e) {
      throw MORE_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, '}');
    } catch (UnknownBlockTypeException e) {
      throw MORE_EXCEPTIONS.unknownBlockType().createWithContext(reader, e.blockType);
    }
    return null;
  }

  private void parseInternal(StringReader reader) throws CommandSyntaxException, //
      ExpectedCharException, //
      ExpectedKeyException, //
      ExpectedValueException, //
      IllegalBlockPropertyKeyException, //
      IllegalBlockPropertyValueException, //
      UnclosedBlockPropertiesException, //
      UnclosedCompoundNbtException, //
      UnknownBlockTypeException //
  {
    String blockType = StringReaderUtils.readId(reader);
    if (!data.getBlockTypes().contains(blockType)) {
      throw new UnknownBlockTypeException(blockType);
    }
    Map<String, String> blockProperties = readBlockProperties(reader, blockType);
    JsonObject nbt = readNbt(reader, blockType);
  }

  private Map<String, String> readBlockProperties(StringReader reader, String blockType)
      throws CommandSyntaxException, //
      ExpectedValueForPropertyOnBlockException, //
      IllegalBlockPropertyKeyException, //
      IllegalBlockPropertyValueException, //
      UnclosedBlockPropertiesException //
  {
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
        throw new IllegalBlockPropertyKeyException(blockType, propertyName, result.keySet());
      }
      try {
        readSeperator(reader, '=');
      } catch (ExpectedCharException e) {
        throw new ExpectedValueForPropertyOnBlockException(propertyName, blockType);
      }
      String propertyValue = reader.readString();
      Set<String> possibleValues = data.getBlockPropertyValues(blockType, propertyName);
      if (!possibleValues.contains(propertyValue)) {
        throw new IllegalBlockPropertyValueException(blockType, propertyName, propertyValue);
      }
      result.put(propertyName, propertyValue);
      if (!tryReadSeperator(reader, ',')) {
        break;
      }
    }
    if (!reader.canRead() || reader.peek() != ']') {
      throw new UnclosedBlockPropertiesException(blockType, result.keySet());
    }
    reader.skip();
    return result;
  }

  private JsonObject readNbt(StringReader reader, String blockType) throws CommandSyntaxException, //
      ExpectedCharException, //
      ExpectedKeyException, //
      ExpectedValueException, //
      UnclosedCompoundNbtException //
  {
    if (!reader.canRead() || reader.peek() != '{') {
      return new JsonObject();
    } else {
      return readObject(reader, blockType, new NbtPath());
    }
  }

  private JsonElement readJson(StringReader reader, String blockType, NbtPath nbtPath)
      throws CommandSyntaxException, //
      ExpectedCharException, //
      ExpectedKeyException, //
      ExpectedValueException, //
      UnclosedCompoundNbtException //
  {
    if (!reader.canRead()) {
      throw new ExpectedValueException();
    }
    char peek = reader.peek();
    switch (peek) {
      case '{':
        return readObject(reader, blockType, nbtPath);
      case '[':
        return readArray(reader, blockType, nbtPath);
      default:
        String string = reader.readString();
        if (string.isEmpty()) {
          throw new ExpectedValueException();
        }
        return new JsonPrimitive(string);
    }
  }

  private JsonObject readObject(StringReader reader, String blockType, NbtPath nbtPath)
      throws CommandSyntaxException, //
      ExpectedCharException, //
      ExpectedKeyException, //
      ExpectedValueException, //
      UnclosedCompoundNbtException //
  {
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
      readSeperator(reader, ':');
      JsonElement value = readJson(reader, blockType, nbtPath.with(key));
      result.add(key, value);
      if (!tryReadSeperator(reader, ',')) {
        break;
      }
    }
    if (!reader.canRead() || reader.peek() != '}') {
      throw new UnclosedCompoundNbtException(blockType, nbtPath, result.keySet());
    }
    reader.skip();
    return result;
  }

  private JsonElement readArray(StringReader reader, String blockType, NbtPath nbtPath)
      throws CommandSyntaxException, //
      ExpectedCharException, //
      ExpectedKeyException, //
      ExpectedValueException, //
      UnclosedCompoundNbtException //
  {
    if (!reader.canRead() || reader.peek() != '[') {
      throw new ExpectedValueException();
    }
    reader.skip();
    reader.skipWhitespace();

    JsonArray result = new JsonArray();
    while (reader.canRead() && reader.peek() != ']') {
      JsonElement element = readJson(reader, blockType, nbtPath.with("[]"));
      result.add(element);
      if (!tryReadSeperator(reader, ',')) {
        break;
      }
    }
    if (!reader.canRead() || reader.peek() != ']') {
      throw new ExpectedCharException(']');
    }
    reader.skip();
    return result;
  }

  private void readSeperator(StringReader reader, char seperator) throws ExpectedCharException {
    if (!tryReadSeperator(reader, seperator)) {
      throw new ExpectedCharException(seperator);
    }
  }

  private boolean tryReadSeperator(StringReader reader, char seperator) {
    reader.skipWhitespace();
    if (reader.canRead() && reader.peek() == seperator) {
      reader.skip();
      reader.skipWhitespace();
      return true;
    } else {
      return false;
    }
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
        } catch (ExpectedCharException e) {
          builder.suggest(String.valueOf(e.c));
        } catch (ExpectedKeyException e) {
          builder.suggest("key");
        } catch (ExpectedValueException e) {
          builder.suggest("value");
        } catch (IllegalBlockPropertyKeyException e) {
          Set<String> possibleProperties = data.getBlockProperties(e.blockType);
          SetView<String> unusedProperties = Sets.difference(possibleProperties, e.usedKeys);
          suggestValuesStartingWith(builder, e.propertyName, unusedProperties);
        } catch (IllegalBlockPropertyValueException e) {
          Set<String> possibleValues = data.getBlockPropertyValues(e.blockType, e.propertyName);
          suggestValuesStartingWith(builder, e.propertyValue, possibleValues);
        } catch (UnclosedBlockPropertiesException e) {
          builder.suggest("]");
          Set<String> possibleProperties = data.getBlockProperties(e.blockType);
          SetView<String> unusedProperties = Sets.difference(possibleProperties, e.usedKeys);
          if (!e.usedKeys.isEmpty() && !unusedProperties.isEmpty()
              && !remaining.trim().endsWith(",")) {
            builder.suggest(",");
          } else {
            suggestValues(builder, unusedProperties);
          }
        } catch (UnclosedCompoundNbtException e) {
          builder.suggest("}");
          Set<String> possibleNbtNames = data.getNbtNames(e.blockType, e.nbtPath);
          SetView<String> unusedNbtNames = Sets.difference(possibleNbtNames, e.usedKeys);
          if (!e.usedKeys.isEmpty() && !unusedNbtNames.isEmpty()
              && !remaining.trim().endsWith(",")) {
            builder.suggest(",");
          } else {
            suggestValues(builder, unusedNbtNames);
          }
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
