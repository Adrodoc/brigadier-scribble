package de.adrodoc.brigadier;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
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
  private final SuggestionContext context;

  public CommandDispatcherFactory(SuggestionContext context) {
    this.context = requireNonNull(context, "context == null!");
  }

  public <S> CommandDispatcher<S> createDispatcherFor_1_13_pre5() throws IOException {
    return createDispatcher("1.13-pre5/reports/commands.json");
  }

  public <S> CommandDispatcher<S> createDispatcher(String resourceName) throws IOException {
    try (Reader reader = asCharSource(getResource(resourceName), UTF_8).openStream()) {
      JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
      DataContext data = DataLoader.load("1.13-pre5/reports/blocks.json");
      return createDispatcher(jsonObject, data);
    }
  }

  public <S> CommandDispatcher<S> createDispatcher(JsonObject jsonObject, DataContext data) {
    ArgumentTypeFactory factory = new ArgumentTypeFactory(data, context);
    return new CommandDispatcher<>(toRootNode(jsonObject, factory));
  }

  private <S> RootCommandNode<S> toRootNode(JsonObject jsonObject, ArgumentTypeFactory factory) {
    RootCommandNode<S> node = new RootCommandNode<>();
    addChildren(node, jsonObject, factory);
    return node;
  }

  private <S> void addChildren(CommandNode<S> parent, JsonObject jsonObject,
      ArgumentTypeFactory factory) {
    JsonObject children = jsonObject.getAsJsonObject("children");
    if (children != null) {
      Collection<CommandNode<S>> nodes = toNodes(children, factory);
      for (CommandNode<S> node : nodes) {
        parent.addChild(node);
      }
    }
  }

  private <S> Collection<CommandNode<S>> toNodes(JsonObject children, ArgumentTypeFactory factory) {
    Collection<CommandNode<S>> nodes = new ArrayList<>();
    for (Entry<String, JsonElement> entry : children.entrySet()) {
      String name = entry.getKey();
      JsonObject child = entry.getValue().getAsJsonObject();
      CommandNode<S> node = toNode(name, child, factory);
      nodes.add(node);
    }
    return nodes;
  }

  private <S> CommandNode<S> toNode(String name, JsonObject jsonObject,
      ArgumentTypeFactory factory) {
    ArgumentBuilder<S, ?> builder = getNodeBuilder(name, jsonObject, factory);
    CommandNode<S> node = builder.build();
    addChildren(node, jsonObject, factory);
    return node;
  }

  private <S> ArgumentBuilder<S, ?> getNodeBuilder(String name, JsonObject jsonObject,
      ArgumentTypeFactory factory) {
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
