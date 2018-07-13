package de.adrodoc.brigadier.nbt.spec;

public class TagNbtSpecNode extends BasicNbtSpecNode {
  @Override
  public NbtType getType() {
    return NbtType.COMPOUND;
  }
}
