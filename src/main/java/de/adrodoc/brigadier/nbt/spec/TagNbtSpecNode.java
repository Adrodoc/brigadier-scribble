package de.adrodoc.brigadier.nbt.spec;

import de.adrodoc.brigadier.nbt.path.NbtPathElement;

public class TagNbtSpecNode extends BasicNbtSpecNode {
  @Override
  public NbtType getType() {
    return NbtType.COMPOUND;
  }

  @Override
  public NbtSpecNode get(NbtPathElement pathElement) {
    return new TagChildNbtSpecNode();
  }
}
