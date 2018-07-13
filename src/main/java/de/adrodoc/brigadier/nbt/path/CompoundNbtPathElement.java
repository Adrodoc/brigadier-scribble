package de.adrodoc.brigadier.nbt.path;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import de.adrodoc.brigadier.nbt.spec.CompoundNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.ListNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

@Immutable
public class CompoundNbtPathElement implements NbtPathElement {
  private final String key;

  public CompoundNbtPathElement(String key) {
    this.key = requireNonNull(key, "key == null!");
  }

  public String getName() {
    return key;
  }

  @Override
  public @Nullable NbtSpecNode of(CompoundNbtSpecNode node) {
    return node.getChild(key);
  }

  @Override
  public @Nullable NbtSpecNode of(ListNbtSpecNode node) {
    return null;
  }

  @Override
  public String toString() {
    return key;
  }
}
