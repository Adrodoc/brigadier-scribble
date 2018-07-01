package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;
import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class BlockDoesNotAcceptValueForPropertyException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final String propertyValue;
  public final String propertyName;

  public BlockDoesNotAcceptValueForPropertyException(String blockType, String propertyValue,
      String propertyName) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.propertyValue = requireNonNull(propertyValue, "propertyValue == null!");
    this.propertyName = requireNonNull(propertyName, "propertyName == null!");
  }
}
