package de.adrodoc.brigadier.nbt.path;

import javax.annotation.Nullable;
import de.adrodoc.brigadier.nbt.spec.CompoundNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.ListNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

public class ListElementNbtPathElement implements NbtPathElement {
  public static final NbtPathElement INSTANCE = new ListElementNbtPathElement();

  private ListElementNbtPathElement() {}

  @Override
  public @Nullable NbtSpecNode of(CompoundNbtSpecNode node) {
    return null;
  }

  @Override
  public NbtSpecNode of(ListNbtSpecNode node) {
    return node.getElement();
  }

  @Override
  public String toString() {
    return "[element]";
  }
}
