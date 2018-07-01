package de.adrodoc.brigadier;

import java.util.Set;

public interface DataContext {
  Set<String> getBlockProperties(String blockType);

  Set<String> getBlockPropertyValues(String blockType, String propertyName);

  Set<String> getBlockTypes();
}
