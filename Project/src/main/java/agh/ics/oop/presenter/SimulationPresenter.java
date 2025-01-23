package agh.ics.oop.presenter;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationPresenter extends Application {

    private SimulationParameters parameters;
    private Stage primaryStage;
    private Simulation simulation;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Simulation Parameters");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Map height and width
        grid.add(new Label("Wysokość mapy:"), 0, 0);
        TextField mapHeight = new TextField();
        grid.add(mapHeight, 1, 0);

        grid.add(new Label("Szerokość mapy:"), 0, 1);
        TextField mapWidth = new TextField();
        grid.add(mapWidth, 1, 1);

        // Map variant
        grid.add(new Label("Wariant mapy:"), 0, 2);
        ComboBox<String> mapVariant = new ComboBox<>();
        mapVariant.getItems().addAll("Kula ziemska", "Bieguny");
        grid.add(mapVariant, 1, 2);

        // Initial number of plants
        grid.add(new Label("Początkowa liczba roślin:"), 0, 3);
        TextField initialPlants = new TextField();
        grid.add(initialPlants, 1, 3);

        // Energy provided by eating one plant
        grid.add(new Label("Energia rośliny:"), 0, 4);
        TextField energyPerPlant = new TextField();
        grid.add(energyPerPlant, 1, 4);

        // Number of plants growing each day
        grid.add(new Label("Liczba roślin rosnących każdego dnia:"), 0, 5);
        TextField plantsPerDay = new TextField();
        grid.add(plantsPerDay, 1, 5);

        // Initial number of animals
        grid.add(new Label("Początkowa liczba zwierząt:"), 0, 7);
        TextField initialAnimals = new TextField();
        grid.add(initialAnimals, 1, 7);

        // Initial energy of animals
        grid.add(new Label("Początkowa energia zwierząt:"), 0, 8);
        TextField initialEnergy = new TextField();
        grid.add(initialEnergy, 1, 8);

        // Energy required for an animal to be considered fed
        grid.add(new Label("Energia, żeby uznać zwierzę za najedzone (gotowe do rozmnażania):"), 0, 9);
        TextField energyToBeFed = new TextField();
        grid.add(energyToBeFed, 1, 9);

        // Energy used by parents to create offspring
        grid.add(new Label("Energia zużywana przez rodziców dla dziecka:"), 0, 10);
        TextField energyUsedByParents = new TextField();
        grid.add(energyUsedByParents, 1, 10);

        // Minimum and maximum number of mutations
        grid.add(new Label("Minimalna liczba mutacji:"), 0, 11);
        TextField minMutations = new TextField();
        grid.add(minMutations, 1, 11);

        grid.add(new Label("Maksymalna liczba mutacji:"), 0, 12);
        TextField maxMutations = new TextField();
        grid.add(maxMutations, 1, 12);

        // Mutation variant
        grid.add(new Label("Wariant mutacji:"), 0, 13);
        ComboBox<String> mutationVariant = new ComboBox<>();
        mutationVariant.getItems().addAll("Pełna losowość", "Podmianka");
        grid.add(mutationVariant, 1, 13);

        // Length of animal genome
        grid.add(new Label("Długość genomu (>=4)"), 0, 14);
        TextField genomeLength = new TextField();
        genomeLength.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*") && (change.getControlNewText().isEmpty() || Integer.parseInt(change.getControlNewText()) >= 4)) {
                return change;
            }
            return null;
        }));
        grid.add(genomeLength, 1, 14);

        // Submit button
        Button submitButton = new Button("OK");
        submitButton.setOnAction(e -> {
            parameters = new SimulationParameters(Integer.parseInt(mapHeight.getText()),
                    Integer.parseInt(mapWidth.getText()),
                    mapVariant.getValue(),
                    Integer.parseInt(initialPlants.getText()),
                    Integer.parseInt(energyPerPlant.getText()),
                    Integer.parseInt(plantsPerDay.getText()),
                    Integer.parseInt(initialAnimals.getText()),
                    Integer.parseInt(initialEnergy.getText()),
                    Integer.parseInt(energyToBeFed.getText()),
                    Integer.parseInt(energyUsedByParents.getText()),
                    Integer.parseInt(minMutations.getText()),
                    Integer.parseInt(maxMutations.getText()),
                    mutationVariant.getValue(),
                    Integer.parseInt(genomeLength.getText()));
            parameters = new SimulationParameters(10,10,"Kula ziemska",10,10,3,20,100,1000,1000,2,7,"Podmianka",9);
            initializeSimulation();
            new Thread(() -> {
                simulation.run();
                System.out.println(simulation.getMap().getPlantsPositions().size());
                Platform.runLater(() -> {
                    drawMap();
                });
            }).start();
        });
        grid.add(submitButton, 1, 16);

        StackPane root = new StackPane(grid);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeSimulation() {
        List<Vector2d> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < parameters.getInitialAnimals(); i++) {
            positions.add(new Vector2d(random.nextInt(parameters.getMapWidth()+1), random.nextInt(parameters.getMapHeight()+1)));
        }
        WorldMap map = new Map(parameters);

        map.growPlant(parameters.getInitialPlants());
        simulation = new Simulation(positions, map, parameters.getInitialEnergy(), parameters.getGenomeLength(), parameters.getEnergyPerPlant(), parameters.getPlantsPerDay(), this, parameters);
    }

    public void drawMap() {
        GridPane mapGrid = new GridPane();
        mapGrid.setGridLinesVisible(true);

        int mapHeight = parameters.getMapHeight();
        int mapWidth = parameters.getMapWidth();

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Pane cell = new Pane();
                cell.setPrefSize(20, 20);
                cell.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-border-width: 1;");
                mapGrid.add(cell, j, i);
            }
        }

        // Display plants
        for (Vector2d plantPosition : simulation.getMap().getPlantsPositions()) {
            Pane cell = (Pane) getNodeByRowColumnIndex(plantPosition.getY(), plantPosition.getX(), mapGrid);
            if (cell != null) {
                cell.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1;");
            }
        }

        // Display animals
        for (Animal animal : simulation.getAnimals()) {
            Vector2d position = animal.getPosition();
            Pane cell = (Pane) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                cell.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-border-width: 1;");
            }
        }

        // Create a label to display the number of plants
        int numberOfPlants = simulation.getMap().getPlantsPositions().size();
        Label plantCountLabel = new Label("Number of plants: " + numberOfPlants);

        // Create a label to display the number of animals
        int numberOfAnimals = simulation.getAnimals().size();
        Label animalCountLabel = new Label("Number of animals: " + numberOfAnimals);

        // Create a layout to hold the map and the labels
        VBox layout = new VBox(10, mapGrid, plantCountLabel, animalCountLabel);
        layout.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(layout);
        Scene mapScene = new Scene(root, 600, 600);
        primaryStage.setScene(mapScene);
    }

    private Pane getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (rowIndex == null) rowIndex = 0;
            if (columnIndex == null) columnIndex = 0;
            if (rowIndex == row && columnIndex == column) {
                if (node instanceof Pane) {
                    return (Pane) node;
                } else {
                    System.err.println("Node at row " + row + " and column " + column + " is not a Pane.");
                    return null;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}