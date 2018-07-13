package de.adrodoc.brigadier.nbt.spec;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.nbt.path.NbtPathElement;

@Immutable
public class ListNbtSpecNode implements NbtSpecNode {
  private final NbtSpecNode element;

  public ListNbtSpecNode(NbtSpecNode element) {
    this.element = requireNonNull(element, "element == null!");
  }

  @Override
  public NbtType getType() {
    return NbtType.LIST;
  }

  @Override
  public @Nullable NbtSpecNode get(NbtPathElement pathElement) {
    return pathElement.of(this);
  }

  public NbtSpecNode getElement() {
    return element;
  }

  @Override
  public ImmutableSet<String> getChildNames() {
    return ImmutableSet.of();
  }
}
