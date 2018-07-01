package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;
import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class UnknownBlockTypeException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final String blockType;

  public UnknownBlockTypeException(String blockType) {
    this.blockType = requireNonNull(blockType, "blockType == null!");
  }
}
