package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.exceptions.ParseException;

@Immutable
public class ExpectedCharException extends ParseException {
  private static final long serialVersionUID = 1L;
  public final char c;

  public ExpectedCharException(char c) {
    this.c = c;
  }
}
