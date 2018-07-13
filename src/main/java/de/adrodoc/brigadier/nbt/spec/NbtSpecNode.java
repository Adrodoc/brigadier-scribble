package de.adrodoc.brigadier.nbt.spec;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.nbt.path.NbtPath;
import de.adrodoc.brigadier.nbt.path.NbtPathElement;

public interface NbtSpecNode {
  NbtType getType();

  @Nullable
  NbtSpecNode get(NbtPathElement pathElement);

  default @Nullable NbtSpecNode get(NbtPath nbtPath) {
    NbtSpecNode node = this;
    for (NbtPathElement pathElement : nbtPath) {
      node = node.get(pathElement);
      if (node == null) {
        return null;
      }
    }
    return node;
  }

  ImmutableSet<String> getChildNames();
}
