package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ExpectedClosingCurlyBracketException extends ExpectedCharException {
  private static final long serialVersionUID = 1L;

  public ExpectedClosingCurlyBracketException() {
    super('}');
  }
}
