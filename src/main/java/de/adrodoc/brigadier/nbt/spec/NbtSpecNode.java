package de.adrodoc.brigadier.nbt.spec;

import com.google.common.collect.ImmutableMap;

public interface NbtSpecNode {
  ImmutableMap<String, NbtSpecNode> getChildren();

  default NbtSpecNode getChild(String name) {
    return getChildren().get(name);
  }

  NbtType getType();
}
