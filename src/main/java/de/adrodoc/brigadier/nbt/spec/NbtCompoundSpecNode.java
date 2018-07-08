package de.adrodoc.brigadier.nbt.spec;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class NbtCompoundSpecNode implements NbtSpecNode {
  private final ImmutableMap<String, NbtSpecNode> children;

  public NbtCompoundSpecNode(Map<String, NbtSpecNode> children) {
    this.children = ImmutableMap.copyOf(children);
  }

  @Override
  public ImmutableMap<String, NbtSpecNode> getChildren() {
    return children;
  }

  @Override
  public NbtType getType() {
    return NbtType.COMPOUND;
  }
}
