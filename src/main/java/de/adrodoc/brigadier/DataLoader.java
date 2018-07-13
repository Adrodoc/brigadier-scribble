package de.adrodoc.brigadier;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.adrodoc.brigadier.nbt.path.NbtPath;
import de.adrodoc.brigadier.nbt.spec.CompoundNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.NbtSpecLoader;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

public class DataLoader {
  private static final String MINECRAFT_NAMESPACE_PREFIX = "minecraft:";

  public static DataContext load(String resourceName) throws IOException {
    Map<String, SetMultimap<String, String>> blockProperties = loadBlockProperties(resourceName);
    return new DataContext() {
      private final Map<String, CompoundNbtSpecNode> nbtSpecCache = new HashMap<>();

      @Override
      public Set<String> getBlockPropertyKeys(String blockType) {
        SetMultimap<String, String> properties = blockProperties.get(blockType);
        return properties == null ? Collections.emptySet() : properties.keySet();
      }

      @Override
      public Set<String> getBlockPropertyValues(String blockType, String propertyName) {
        SetMultimap<String, String> properties = blockProperties.get(blockType);
        return properties == null ? Collections.emptySet() : properties.get(propertyName);
      }

      @Override
      public Set<String> getBlockTypes() {
        return blockProperties.keySet();
      }

      @Override
      public @Nullable NbtSpecNode getNbtSpecNode(String blockType, NbtPath nbtPath) {
        CompoundNbtSpecNode spec = getNbtSpec(blockType);
        return spec.get(nbtPath);
      }

      private CompoundNbtSpecNode getNbtSpec(String blockType) {
        return nbtSpecCache.computeIfAbsent(blockType, this::loadNbtSpec);
      }

      private CompoundNbtSpecNode loadNbtSpec(String blockType) {
        if (blockType.startsWith(MINECRAFT_NAMESPACE_PREFIX)) {
          blockType = blockType.substring(MINECRAFT_NAMESPACE_PREFIX.length());
        }
        try {
          return NbtSpecLoader.loadNbtSpec("block/" + blockType + ".json");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private static Map<String, SetMultimap<String, String>> loadBlockProperties(String resourceName)
      throws IOException {
    try (Reader reader = asCharSource(getResource(resourceName), UTF_8).openStream()) {
      JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
      Set<Entry<String, JsonElement>> blocks = jsonObject.entrySet();
      return getProperties(blocks);
    }
  }

  private static Map<String, SetMultimap<String, String>> getProperties(
      Set<Entry<String, JsonElement>> blocks) {
    ImmutableMap.Builder<String, SetMultimap<String, String>> result = ImmutableMap.builder();
    for (Entry<String, JsonElement> entry : blocks) {
      String blockId = entry.getKey();
      JsonObject block = entry.getValue().getAsJsonObject();
      ImmutableSetMultimap<String, String> properties = getProperties(block);
      result.put(blockId, properties);
    }
    return result.build();
  }

  private static ImmutableSetMultimap<String, String> getProperties(JsonObject block) {
    ImmutableSetMultimap.Builder<String, String> result = ImmutableSetMultimap.builder();
    JsonObject properties = block.getAsJsonObject("properties");
    if (properties != null) {
      for (Entry<String, JsonElement> entry : properties.entrySet()) {
        String propertyName = entry.getKey();
        JsonArray propertyValues = entry.getValue().getAsJsonArray();
        for (JsonElement propertyValue : propertyValues) {
          result.put(propertyName, propertyValue.getAsString());
        }
      }
    }
    return result.build();
  }
}
