package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class BlockDoesNotHavePropertyException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final String propertyName;
  public final ImmutableSet<String> usedKeys;

  public BlockDoesNotHavePropertyException(String blockType, String propertyName,
      Set<String> usedKeys) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.propertyName = requireNonNull(propertyName, "propertyName == null!");
    this.usedKeys = ImmutableSet.copyOf(usedKeys);
  }
}
