package de.adrodoc.brigadier.nbt.spec;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Resources.asCharSource;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map.Entry;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NbtSpecLoader {
  private static CompoundNbtSpecNode loadNbtSpec(JsonElement ref, String resourceName)
      throws IOException {
    return loadNbtSpec(resourceName + "/../" + ref.getAsString());
  }

  public static CompoundNbtSpecNode loadNbtSpec(String resourceName) throws IOException {
    URL resource = NbtSpecLoader.class.getClassLoader().getResource(resourceName);
    if (resource == null) {
      return CompoundNbtSpecNode.EMPTY;
    }
    try (Reader reader = asCharSource(resource, UTF_8).openStream()) {
      JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
      String type = jsonObject.get("type").getAsString();
      checkState("compound".equals(type), "Expected compound");
      return toCompoundNbtSpec(jsonObject, resourceName);
    }
  }

  private static CompoundNbtSpecNode toCompoundNbtSpec(JsonObject jsonObject, String resourceName)
      throws IOException {
    ImmutableMap.Builder<String, NbtSpecNode> childrenBuilder = ImmutableMap.builder();
    JsonObject children = jsonObject.getAsJsonObject("children");
    if (children != null) {
      for (Entry<String, JsonElement> entry : children.entrySet()) {
        String key = entry.getKey();
        JsonObject value = entry.getValue().getAsJsonObject();
        NbtSpecNode child = toNbtSpec(value, resourceName);
        childrenBuilder.put(key, child);
      }
    }

    JsonArray child_refs = jsonObject.getAsJsonArray("child_ref");
    if (child_refs != null) {
      for (JsonElement child_ref : child_refs) {
        CompoundNbtSpecNode ref = loadNbtSpec(child_ref, resourceName);
        childrenBuilder.putAll(ref.getChildren());
      }
    }
    return new CompoundNbtSpecNode(childrenBuilder.build());
  }

  private static NbtSpecNode toNbtSpec(JsonObject jsonObject, String resourceName)
      throws IOException {
    JsonElement ref = jsonObject.get("ref");
    if (ref != null) {
      return loadNbtSpec(ref, resourceName);
    } else {
      JsonElement jsonElement = jsonObject.get("type");
      if (jsonElement == null) {
        return new TagNbtSpecNode();
      }
      String type = jsonElement.getAsString();
      switch (type) {
        case "compound":
          return toCompoundNbtSpec(jsonObject, resourceName);
        case "list":
          JsonObject item = jsonObject.getAsJsonObject("item");
          NbtSpecNode child = toNbtSpec(item, resourceName);
          return new ListNbtSpecNode(child);
        case "short":
          return new ShortNbtSpecNode();
        case "byte":
          return new ByteNbtSpecNode();
        case "string":
          return new StringNbtSpecNode();
        case "int":
          return new IntNbtSpecNode();
        case "long":
          return new LongNbtSpecNode();
        default:
          throw new IllegalArgumentException("Unsupported type: " + type);
      }
    }
  }
}
