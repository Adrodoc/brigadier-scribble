package de.adrodoc.brigadier;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.adrodoc.brigadier.argument.type.minecraft.nbt.NbtPath;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

public interface DataContext {
  Set<String> getBlockPropertyKeys(String blockType);

  Set<String> getBlockPropertyValues(String blockType, String propertyName);

  Set<String> getBlockTypes();

  NbtSpecNode getNbtSpecNode(String blockType, NbtPath nbtPath);

  default ImmutableMap<String, NbtSpecNode> getNbtChildren(String blockType, NbtPath nbtPath) {
    NbtSpecNode node = getNbtSpecNode(blockType, nbtPath);
    return node == null ? ImmutableMap.of() : node.getChildren();
  }

  default ImmutableSet<String> getNbtChildNames(String blockType, NbtPath nbtPath) {
    return getNbtChildren(blockType, nbtPath).keySet();
  }
}
