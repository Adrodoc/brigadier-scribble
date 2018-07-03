package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class IllegalBlockPropertyValueException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final String propertyName;
  public final String propertyValue;

  public IllegalBlockPropertyValueException(String blockType, String propertyName,
      String propertyValue) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.propertyName = requireNonNull(propertyName, "propertyName == null!");
    this.propertyValue = requireNonNull(propertyValue, "propertyValue == null!");
  }
}
