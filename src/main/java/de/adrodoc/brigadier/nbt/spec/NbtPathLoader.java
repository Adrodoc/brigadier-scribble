package de.adrodoc.brigadier.nbt.spec;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NbtPathLoader {
  public static void main(String[] args) throws IOException {
    NbtCompoundSpecNode result = loadNbtPaths("block/furnace.json");
    ImmutableMap<String, NbtSpecNode> children = result.getChildren();
    System.out.println(children);
  }

  public static NbtCompoundSpecNode loadNbtPaths(String resourceName) throws IOException {
    try (Reader reader = asCharSource(getResource(resourceName), UTF_8).openStream()) {
      JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
      String type = jsonObject.get("type").getAsString();
      checkState("compound".equals(type), "Expected compound");
      return compound(jsonObject, resourceName);
    }
  }

  private static NbtCompoundSpecNode compound(JsonObject jsonObject, String resourceName)
      throws IOException {
    ImmutableMap.Builder<String, NbtSpecNode> childrenBuilder = ImmutableMap.builder();
    JsonObject children = jsonObject.getAsJsonObject("children");
    for (Entry<String, JsonElement> entry : children.entrySet()) {
      String key = entry.getKey();
      JsonObject value = entry.getValue().getAsJsonObject();
      NbtSpecNode child = child(value, resourceName);
      childrenBuilder.put(key, child);
    }

    JsonArray child_refs = jsonObject.getAsJsonArray("child_ref");
    if (child_refs != null) {
      for (JsonElement child_ref : child_refs) {
        NbtCompoundSpecNode ref = resolveRef(child_ref, resourceName);
        childrenBuilder.putAll(ref.getChildren());
      }
    }
    return new NbtCompoundSpecNode(childrenBuilder.build());
  }

  private static NbtSpecNode child(JsonObject jsonObject, String resourceName) throws IOException {
    JsonElement ref = jsonObject.get("ref");
    if (ref != null) {
      return resolveRef(ref, resourceName);
    } else {
      JsonElement jsonElement = jsonObject.get("type");
      if (jsonElement == null) {
        return new TagNbtSpecNode();
      }
      String type = jsonElement.getAsString();
      switch (type) {
        case "compound":
          return compound(jsonObject, resourceName);
        case "list":
          JsonObject item = jsonObject.getAsJsonObject("item");
          NbtSpecNode child = child(item, resourceName);
          return new ListNbtSpecNode(child);
        case "short":
          return new ShortNbtSpecNode();
        case "byte":
          return new ByteNbtSpecNode();
        case "string":
          return new StringNbtSpecNode();
        case "int":
          return new IntNbtSpecNode();
        default:
          throw new IllegalArgumentException("Unsupported type: " + type);
      }
    }
  }

  private static NbtCompoundSpecNode resolveRef(JsonElement ref, String resourceName)
      throws IOException {
    return loadNbtPaths(resourceName + "/../" + ref.getAsString());
  }
}
