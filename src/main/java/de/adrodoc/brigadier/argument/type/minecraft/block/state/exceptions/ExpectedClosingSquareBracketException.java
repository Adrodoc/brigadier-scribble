package de.adrodoc.brigadier.argument.type.minecraft.block.state.exceptions;

import static java.util.Objects.requireNonNull;
import java.util.Set;
import javax.annotation.concurrent.Immutable;
import com.google.common.collect.ImmutableSet;

@Immutable
public class ExpectedClosingSquareBracketException extends ExpectedCharException {
  private static final long serialVersionUID = 1L;
  public final String blockType;
  public final ImmutableSet<String> blockProperties;

  public ExpectedClosingSquareBracketException(String blockType, Set<String> blockProperties) {
    super(']');
    this.blockType = requireNonNull(blockType, "blockType == null!");
    this.blockProperties = ImmutableSet.copyOf(blockProperties);
  }
}
