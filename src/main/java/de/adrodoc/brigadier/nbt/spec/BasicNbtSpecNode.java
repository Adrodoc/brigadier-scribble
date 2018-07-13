package de.adrodoc.brigadier.nbt.spec;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.nbt.path.NbtPathElement;

public class BasicNbtSpecNode implements NbtSpecNode {
  @Override
  public NbtType getType() {
    return NbtType.PRIMITIVE;
  }

  @Override
  public @Nullable NbtSpecNode get(NbtPathElement pathElement) {
    return null;
  }

  @Override
  public ImmutableSet<String> getChildNames() {
    return ImmutableSet.of();
  }
}
