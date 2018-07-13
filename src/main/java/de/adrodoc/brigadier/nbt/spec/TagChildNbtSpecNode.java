package de.adrodoc.brigadier.nbt.spec;

import de.adrodoc.brigadier.nbt.path.NbtPathElement;

public class TagChildNbtSpecNode extends BasicNbtSpecNode {
  @Override
  public NbtSpecNode get(NbtPathElement pathElement) {
    return new TagChildNbtSpecNode();
  }
}
