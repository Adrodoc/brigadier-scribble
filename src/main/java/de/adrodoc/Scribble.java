package de.adrodoc;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import de.adrodoc.brigadier.CommandDispatcherFactory;
import de.adrodoc.brigadier.SuggestionContext;

public class Scribble {

  public static void main(String[] args) throws IOException {
    CommandDispatcherFactory factory = new CommandDispatcherFactory(new SuggestionContext() {
      @Override
      public Set<String> getTeams() {
        return ImmutableSet.of("bla", "Bubub");
      }
    });
    CommandDispatcher<Object> dispatcher = factory.createDispatcherFor_1_13_pre5();
    String command = "fill 114 69 72 114 69 72 stone replace #minecraft:log:";
    CompletableFuture<Suggestions> future =
        dispatcher.getCompletionSuggestions(dispatcher.parse(command, null));
    future.thenAccept(suggestions -> {
      System.out.println("Suggestions:");
      for (Suggestion suggestion : suggestions.getList()) {
        System.out.println(suggestion);
      }
    });

    ParseResults<Object> result = dispatcher.parse(command, null);
    CommandContextBuilder<Object> context = result.getContext();
    Map<String, ParsedArgument<Object, ?>> arguments = context.getArguments();
    System.out.println("Arguments:");
    for (Entry<String, ParsedArgument<Object, ?>> entry : arguments.entrySet()) {
      System.out.print(entry.getKey());
      System.out.print('=');
      System.out.println(entry.getValue().getResult());
    }
    Map<CommandNode<Object>, StringRange> nodes = context.getNodes();
    System.out.println("Nodes:");
    for (Entry<CommandNode<Object>, StringRange> entry : nodes.entrySet()) {
      System.out.print(entry.getKey().getName());
      System.out.print(' ');
    }
    System.out.println();
    Map<CommandNode<Object>, CommandSyntaxException> exceptions = result.getExceptions();
    for (Entry<CommandNode<Object>, CommandSyntaxException> entry : exceptions.entrySet()) {
      System.out.println(entry.getKey());
      System.out.println(entry.getValue());
      entry.getValue().printStackTrace();
    }
  }
}
