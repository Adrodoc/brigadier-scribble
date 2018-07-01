package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;
import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class ExpectedValueForPropertyOnBlockException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String propertyName;
  public final String blockType;

  public ExpectedValueForPropertyOnBlockException(String propertyName, String blockType) {
    this.propertyName = requireNonNull(propertyName, "propertyName == null!");
    this.blockType = requireNonNull(blockType, "blockType == null!");
  }
}
