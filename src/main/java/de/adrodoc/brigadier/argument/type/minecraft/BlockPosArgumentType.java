package de.adrodoc.brigadier.argument.type.minecraft;

import static de.adrodoc.brigadier.argument.type.minecraft.BlockPos.Value.absolute;
import static de.adrodoc.brigadier.argument.type.minecraft.BlockPos.Value.relative;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.adrodoc.brigadier.StringReaderUtils;
import de.adrodoc.brigadier.argument.type.minecraft.BlockPos.Value;
import de.adrodoc.brigadier.exceptions.MoreExceptions;

public class BlockPosArgumentType extends ExhaustiveExamplesArgumentType<BlockPos> {
  private static final ImmutableSet<String> EXAMPLES = ImmutableSet.of("~", "~ ~", "~ ~ ~");

  @Override
  public <S> BlockPos parse(StringReader reader) throws CommandSyntaxException {
    Value x = readCoordinate(reader);
    StringReaderUtils.nextArgument(reader);
    Value y = readCoordinate(reader);
    StringReaderUtils.nextArgument(reader);
    Value z = readCoordinate(reader);
    return new BlockPos(x, y, z);
  }

  private BlockPos.Value readCoordinate(StringReader reader) throws CommandSyntaxException {
    if (reader.canRead()) {
      if (reader.peek() == '~') {
        reader.skip();
        return relative(StringReaderUtils.readDoubleOrDefault(reader, 0));
      } else {
        return absolute(reader.readInt());
      }
    } else {
      throw MoreExceptions.INSTANCE.expected3Coordinates().createWithContext(reader);
    }
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
