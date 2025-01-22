package agh.ics.oop.presenter;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
        grid.add(new Label("Długość genomu"), 0, 14);
        TextField genomeLength = new TextField();
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
            initializeSimulation();
            drawMap();
            new Thread(simulation).start(); // Start the simulation in a new thread
        });
        grid.add(submitButton, 1, 16);

        StackPane root = new StackPane(grid);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeSimulation() {
        List<Vector2d> positions = new ArrayList<>();
        for (int i = 0; i < parameters.getInitialAnimals(); i++) {
            positions.add(new Vector2d((int) (Math.random() * parameters.getMapWidth()), (int) (Math.random() * parameters.getMapHeight())));
        }
        WorldMap map = new Map(parameters.getMapHeight(), parameters.getMapWidth());

        for (int i = 0; i < parameters.getInitialPlants(); i++) {
            map.growPlant(1);
        }
        simulation = new Simulation(positions, map, parameters.getInitialEnergy(), parameters.getGenomeLength(), parameters.getEnergyPerPlant(), parameters.getPlantsPerDay(), this);
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
                cell.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-border-width: 1;"); // Set default background color to green and border color to black
                mapGrid.add(cell, j, i);
            }
        }

        // Display animals
        for (Animal animal : simulation.getAnimals()) {
            Vector2d position = animal.getPosition();
            Pane cell = (Pane) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                cell.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-border-width: 1;"); // Set background color to red for cells with animals and border color to black
            }
        }

        // Display plants
        for (Vector2d plantPosition : simulation.getMap().getPlantsPositions()) {
            Pane cell = (Pane) getNodeByRowColumnIndex(plantPosition.getY(), plantPosition.getX(), mapGrid);
            if (cell != null) {
                cell.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1;"); // Set background color to yellow for cells with plants and border color to black
            }
        }

        StackPane root = new StackPane(mapGrid);
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