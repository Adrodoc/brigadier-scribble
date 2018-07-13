package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;
import de.adrodoc.brigadier.nbt.path.NbtPath;

@Immutable
public class UnclosedListNbtException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final NbtPath nbtPath;
  public final int size;

  public UnclosedListNbtException(String blockType, NbtPath nbtPath, int size) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.nbtPath = requireNonNull(nbtPath, "nbtPath == null!");
    this.size = requireNonNull(size, "size == null!");
  }
}
