package de.adrodoc.brigadier;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.nbt.path.NbtPath;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

public interface DataContext {
  Set<String> getBlockPropertyKeys(String blockType);

  Set<String> getBlockPropertyValues(String blockType, String propertyName);

  Set<String> getBlockTypes();

  default ImmutableSet<String> getNbtChildNames(String blockType, NbtPath nbtPath) {
    NbtSpecNode node = getNbtSpecNode(blockType, nbtPath);
    if (node != null) {
      return node.getChildNames();
    } else {
      return ImmutableSet.of();
    }
  }

  NbtSpecNode getNbtSpecNode(String blockType, NbtPath nbtPath);
}
