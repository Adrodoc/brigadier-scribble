package de.adrodoc.brigadier.nbt.spec;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;

public class ListNbtSpecNode implements NbtSpecNode {
  public static final String LIST_CHILD_KEY = "[]";
  private final NbtSpecNode child;

  public ListNbtSpecNode(NbtSpecNode child) {
    this.child = requireNonNull(child, "child == null!");
  }

  @Override
  public NbtType getType() {
    return NbtType.LIST;
  }

  @Override
  public NbtSpecNode getChild(String name) {
    return LIST_CHILD_KEY.equals(name) ? child : null;
  }

  @Override
  public ImmutableMap<String, NbtSpecNode> getChildren() {
    return ImmutableMap.of();
  }
}
