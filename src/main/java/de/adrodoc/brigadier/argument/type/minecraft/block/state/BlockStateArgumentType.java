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
import de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions.ExpectedClosingSquareBracketException;
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
    }
    return null;
  }

  private void parseInternal(StringReader reader)
      throws CommandSyntaxException, UnknownBlockTypeException,
      ExpectedClosingSquareBracketException, BlockDoesNotHavePropertyException,
      ExpectedValueForPropertyOnBlockException, BlockDoesNotAcceptValueForPropertyException {
    String blockType = StringReaderUtils.readId(reader);
    if (!data.getBlockTypes().contains(blockType)) {
      throw new UnknownBlockTypeException(blockType);
    }
    Map<String, String> blockProperties = readBlockProperties(reader, blockType);

    // readNbt(reader, id);
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

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    String remaining = builder.getRemaining();
    StringReader reader = new StringReader(remaining);
    try {
      try {
        parseInternal(reader);
      } catch (ParseException e) {
        if (reader.canRead()) {
          return builder.buildFuture();
        } else {
          throw e;
        }
      }
    } catch (BlockDoesNotAcceptValueForPropertyException e) {
      Set<String> possibleValues = data.getBlockPropertyValues(e.blockType, e.propertyName);
      suggestValuesStartingWith(builder, e.propertyValue, possibleValues);
    } catch (BlockDoesNotHavePropertyException e) {
      Set<String> possibleProperties = data.getBlockProperties(e.blockType);
      suggestValuesStartingWith(builder, e.propertyName, possibleProperties);
    } catch (ExpectedClosingSquareBracketException e) {
      builder.suggest("]");
      Set<String> possibleProperties = data.getBlockProperties(e.blockType);
      SetView<String> unusedProperties = Sets.difference(possibleProperties, e.blockProperties);
      if (!e.blockProperties.isEmpty() && !unusedProperties.isEmpty()
          && !remaining.trim().endsWith(",")) {
        builder.suggest(",");
      } else {
        suggestValues(builder, unusedProperties);
      }
    } catch (ExpectedValueForPropertyOnBlockException e) {
      builder.suggest("=");
    } catch (UnknownBlockTypeException e) {
      suggestValuesStartingWith(builder, e.blockType, data.getBlockTypes());
    } catch (CommandSyntaxException e) {
      return builder.buildFuture();
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
