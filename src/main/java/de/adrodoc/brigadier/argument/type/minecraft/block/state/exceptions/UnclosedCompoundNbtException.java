package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import de.adrodoc.brigadier.argument.type.minecraft.nbt.NbtPath;
import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class UnclosedCompoundNbtException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final NbtPath nbtPath;
  public final ImmutableSet<String> usedKeys;

  public UnclosedCompoundNbtException(String blockType, NbtPath nbtPath, Set<String> usedKeys) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.nbtPath = requireNonNull(nbtPath, "nbtPath == null!");
    this.usedKeys = ImmutableSet.copyOf(usedKeys);
  }
}
