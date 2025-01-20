package agh.ics.oop.presenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimulationPresenter extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Siema");

        Button startButton = new Button("Start");

        StackPane root = new StackPane();
        root.getChildren().add(startButton);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
