package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;
import de.adrodoc.brigadier.nbt.path.NbtPath;

@Immutable
public class MissingNbtValueException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final NbtPath nbtPath;

  public MissingNbtValueException(String blockType, NbtPath nbtPath) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.nbtPath = requireNonNull(nbtPath, "nbtPath == null!");
  }
}
