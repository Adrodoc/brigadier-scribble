package de.adrodoc.minecraft.debugger;

import static com.google.common.collect.Collections2.transform;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import de.adrodoc.brigadier.CommandDispatcherFactory;
import de.adrodoc.brigadier.SuggestionContext;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;

public class ApplicationController {
  @FXML
  private Parent root;
  @FXML
  private TextArea text;
  @FXML
  private TextArea suggestions;
  @FXML
  private TextArea exceptions;

  private CommandDispatcher<Object> dispatcher;

  @FXML
  public void initialize() throws IOException {
    CommandDispatcherFactory factory = new CommandDispatcherFactory(new SuggestionContext() {});
    dispatcher = factory.createDispatcherFor_1_13_pre5();
    text.textProperty().addListener((observable, oldValue, newValue) -> {
      onChange(newValue);
    });
  }

  private void onChange(String newValue) {
    ParseResults<Object> parsed = dispatcher.parse(newValue, null);

    Joiner lineJoiner = Joiner.on('\n');
    exceptions.setText(
        lineJoiner.join(transform(parsed.getExceptions().values(), Throwable::getMessage)));

    CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(parsed);
    future.thenAccept(s -> {
      suggestions.setText(lineJoiner.join(transform(s.getList(), Suggestion::getText)));
    });
  }
}
