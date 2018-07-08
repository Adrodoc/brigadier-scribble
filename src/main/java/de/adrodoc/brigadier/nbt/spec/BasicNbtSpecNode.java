package de.adrodoc.brigadier.nbt.spec;

import com.google.common.collect.ImmutableMap;

public class BasicNbtSpecNode implements NbtSpecNode {
  @Override
  public ImmutableMap<String, NbtSpecNode> getChildren() {
    return ImmutableMap.of();
  }

  @Override
  public NbtType getType() {
    return NbtType.PRIMITIVE;
  }
}
