package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.exceptions.ParseException;
import de.adrodoc.brigadier.nbt.path.NbtPath;

@Immutable
public class IllegalNbtKeyException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final NbtPath nbtPath;
  public final String key;
  public final ImmutableSet<String> usedKeys;

  public IllegalNbtKeyException(String blockType, NbtPath nbtPath, String key,
      Set<String> usedKeys) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.nbtPath = requireNonNull(nbtPath, "nbtPath == null!");
    this.key = requireNonNull(key, "key == null!");
    this.usedKeys = ImmutableSet.copyOf(usedKeys);
  }
}
