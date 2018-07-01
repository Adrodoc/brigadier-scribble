package de.adrodoc.brigadier;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.adrodoc.brigadier.argument.type.ArgumentTypeFactory;

public class CommandDispatcherFactory {
  private final ArgumentTypeFactory factory;

  public CommandDispatcherFactory(SuggestionContext context) {
    this.factory = new ArgumentTypeFactory(context);
  }

  public <S> CommandDispatcher<S> createDispatcherFor_1_13_pre5() throws IOException {
    return createDispatcher("1.13-pre5/reports/commands.json");
  }

  public <S> CommandDispatcher<S> createDispatcher(String resourceName) throws IOException {
    try (Reader reader = asCharSource(getResource(resourceName), UTF_8).openStream()) {
      JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
      return createDispatcher(jsonObject);
    }
  }

  public <S> CommandDispatcher<S> createDispatcher(JsonObject jsonObject) {
    return new CommandDispatcher<>(toRootNode(jsonObject));
  }

  private <S> RootCommandNode<S> toRootNode(JsonObject jsonObject) {
    RootCommandNode<S> node = new RootCommandNode<>();
    addChildren(node, jsonObject);
    return node;
  }

  private <S> void addChildren(CommandNode<S> parent, JsonObject jsonObject) {
    JsonObject children = jsonObject.getAsJsonObject("children");
    if (children != null) {
      Collection<CommandNode<S>> nodes = toNodes(children);
      for (CommandNode<S> node : nodes) {
        parent.addChild(node);
      }
    }
  }

  private <S> Collection<CommandNode<S>> toNodes(JsonObject children) {
    Collection<CommandNode<S>> nodes = new ArrayList<>();
    for (Entry<String, JsonElement> entry : children.entrySet()) {
      String name = entry.getKey();
      JsonObject child = entry.getValue().getAsJsonObject();
      CommandNode<S> node = toNode(name, child);
      nodes.add(node);
    }
    return nodes;
  }

  private <S> CommandNode<S> toNode(String name, JsonObject jsonObject) {
    ArgumentBuilder<S, ?> builder = getNodeBuilder(name, jsonObject);
    CommandNode<S> node = builder.build();
    addChildren(node, jsonObject);
    return node;
  }

  private <S> ArgumentBuilder<S, ?> getNodeBuilder(String name, JsonObject jsonObject) {
    String type = jsonObject.get("type").getAsString();
    switch (type) {
      case "literal":
        return LiteralArgumentBuilder.literal(name);
      case "argument":
        // boolean executable = node.get("executable").getAsBoolean();
        String parser = jsonObject.get("parser").getAsString();
        JsonObject properties = jsonObject.getAsJsonObject("properties");
        ArgumentType<?> argumentType = factory.getArgumentType(parser, properties);
        return RequiredArgumentBuilder.argument(name, argumentType);
      default:
        throw new IllegalArgumentException("Unknown node type '" + type + "'");
    }
  }
}
