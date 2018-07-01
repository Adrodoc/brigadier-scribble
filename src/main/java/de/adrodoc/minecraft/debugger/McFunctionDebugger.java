package de.adrodoc.minecraft.debugger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class McFunctionDebugger extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/application.fxml"));
    Scene scene = new Scene(root, 1000, 500);
    stage.setScene(scene);
    stage.show();
  }
}
