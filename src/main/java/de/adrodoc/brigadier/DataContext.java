package de.adrodoc.brigadier;

import java.util.Set;
import de.adrodoc.brigadier.argument.type.minecraft.nbt.NbtPath;

public interface DataContext {
  Set<String> getBlockProperties(String blockType);

  Set<String> getBlockPropertyValues(String blockType, String propertyName);

  Set<String> getBlockTypes();

  Set<String> getNbtNames(String blockType, NbtPath nbtPath);
}
