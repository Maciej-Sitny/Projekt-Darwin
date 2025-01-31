package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class SimulationPresenter extends Application {
    private SimulationParameters parameters;
    private Stage primaryStage;
    private Simulation simulation;

    // Declare labels at the class level
    private Label genomeLabel;
    private Label energyLabel;
    private Label plantsEatenLabel;
    private Label childrenLabel;
    private Label descendantsLabel;
    private Label ageLabel;
    private Label deathDayLabel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Simulation Parameters");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField mapHeight = createLabeledTextField(grid, "Map height:", "20", 0);
        TextField mapWidth = createLabeledTextField(grid, "Map width:", "20", 1);
        TextField initialPlants = createLabeledTextField(grid, "Starting number of plants:", "100", 2);
        TextField energyPerPlant = createLabeledTextField(grid, "Energy on eating a plant:", "50", 3);
        TextField plantsPerDay = createLabeledTextField(grid, "Number of plants grow per day:", "15", 4);
        TextField initialAnimals = createLabeledTextField(grid, "Starting number of Animal:", "40", 5);
        TextField initialEnergy = createLabeledTextField(grid, "Starting energy of animal:", "500", 6);
        TextField energyLost = createLabeledTextField(grid, "Amount of energy animal loses per day:", "40", 7);
        TextField energyToBeFed = createLabeledTextField(grid, "Energy required for animal to be fed (ready for reproduction):", "300", 8);
        TextField energyUsedByParents = createLabeledTextField(grid, "Energy given to child from parent:", "150", 9);
        TextField minMutations = createLabeledTextField(grid, "Minimal number of mutations:", "1", 10);
        TextField maxMutations = createLabeledTextField(grid, "Maximal number of mutations:", "2", 11);
        TextField genomeLength = createLabeledTextField(grid, "Genome Length:", "2", 12);

        grid.add(new Label("Map Type:"), 0, 13);
        ComboBox<String> mapType = new ComboBox<>();
        mapType.getItems().addAll("Round Globe", "Poles");
        mapType.setValue("Round Globe");
        grid.add(mapType, 1, 13);

        grid.add(new Label("Mutation Variant:"), 0, 14);
        ComboBox<String> mutationVariant = new ComboBox<>();
        mutationVariant.getItems().addAll("Pełna losowość", "Podmianka");
        mutationVariant.setValue("Pełna losowość");
        grid.add(mutationVariant, 1, 14);

        CheckBox saveDataCheckBox = new CheckBox("Save data to file");
        grid.add(saveDataCheckBox, 1, 15);
        Button submitButton = new Button("Start");
        submitButton.setOnAction(e -> {
            if (validateInput(mapHeight, mapWidth, initialPlants, energyPerPlant, plantsPerDay,
                    initialAnimals, initialEnergy, energyLost, energyToBeFed, energyUsedByParents,
                    minMutations, maxMutations, genomeLength)) {

                parameters = new SimulationParameters(
                        Integer.parseInt(mapHeight.getText()),
                        Integer.parseInt(mapWidth.getText()),
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
                        Integer.parseInt(genomeLength.getText()),
                        Integer.parseInt(energyLost.getText()),
                        mapType.getValue(),
                        saveDataCheckBox.isSelected() // Pass the value of the CheckBox
                );

                initializeSimulation();
                new Thread(() -> {
                    simulation.run();
                    Platform.runLater(this::drawMap);
                }).start();
            }
        });

        grid.add(submitButton, 1, 16);
        StackPane root = new StackPane(grid);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            if (simulation != null) {
                simulation.stop();
            }
            System.exit(0);
        });

        primaryStage.show();
    }

    private TextField createLabeledTextField(GridPane grid, String label, String defaultValue, int row) {
        grid.add(new Label(label), 0, row);
        TextField textField = new TextField(defaultValue);
        grid.add(textField, 1, row);
        return textField;
    }

    private boolean validateInput(TextField... fields) {
        try {
            int minMut = Integer.parseInt(fields[10].getText());
            int maxMut = Integer.parseInt(fields[11].getText());
            int genomeLen = Integer.parseInt(fields[12].getText());
            int energyToBeFed = Integer.parseInt(fields[8].getText());
            int energyUsedByParents = Integer.parseInt(fields[9].getText());

            for (int i = 0; i < fields.length - 1; i++) {
                int value = Integer.parseInt(fields[i].getText());
                if (value <= 0) {
                    String fieldName = getFieldName(i);
                    showAlert(fieldName + " must be greater than 0.");
                    return false;
                }
            }

            if (minMut > maxMut) {
                showAlert("Minimal number of mutations cannot be greater than maximal number of mutations.");
                return false;
            }

            if (maxMut > genomeLen) {
                showAlert("Maximal number of mutations cannot be greater than Genome Length.");
                return false;
            }

            if (energyToBeFed <= energyUsedByParents) {
                showAlert("Energy required for reproduction must be greater than energy given to child from parent.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.");
            return false;
        }
    }

    private String getFieldName(int index) {
        String[] fieldNames = {
                "Map height", "Map width", "Starting number of plants",
                "Energy on eating a plant", "Number of plants grow per day",
                "Starting number of animals", "Starting energy of animal",
                "Amount of energy animal loses per day",
                "Energy required for animal to be fed (ready for reproduction)",
                "Energy given to child from parent",
                "Minimal number of mutations", "Maximal number of mutations",
                "Genome Length"
        };
        return fieldNames[index];
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initializeSimulation() {
        List<Vector2d> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < parameters.getInitialAnimals(); i++) {
            positions.add(new Vector2d(random.nextInt(parameters.getMapWidth()), random.nextInt(parameters.getMapHeight())));
        }
        WorldMap map = new Map(parameters);

        map.growPlant(parameters.getInitialPlants());
        simulation = new Simulation(positions, map, parameters.getInitialEnergy(), parameters.getGenomeLength(), parameters.getEnergyPerPlant(), parameters.getPlantsPerDay(), this, parameters);
    }

    public void drawMap() {
        GridPane mapGrid = new GridPane();
        mapGrid.setGridLinesVisible(true);
        mapGrid.setAlignment(Pos.CENTER);

        int mapHeight = parameters.getMapHeight();
        int mapWidth = parameters.getMapWidth();

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Rectangle cell = new Rectangle(20, 20);
                cell.setStroke(Color.BLACK);
                cell.setFill(Color.GREEN);
                cell.getStyleClass().add("cell");
                mapGrid.add(cell, j, i);
            }
        }

        for (Vector2d plantPosition : simulation.getMap().getPlantsPositions()) {
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(plantPosition.getY(), plantPosition.getX(), mapGrid);
            if (cell != null) {
                cell.setFill(Color.YELLOW);
            }
        }

        for (Animal animal : simulation.getAnimals()) {
            Vector2d position = animal.getPosition();
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                if (animal.getAge() == 1) {
                    cell.setFill(Color.BLUE);
                } else {
                    int maxEnergy = parameters.getInitialEnergy();
                    int energy = Math.max(0, Math.min(animal.getEnergy(), maxEnergy));
                    double intensity = (double) energy / maxEnergy;
                    int redValue = (int) (255 * intensity);
                    Color dynamicColor = Color.rgb(redValue, 0, 0);
                    cell.setFill(dynamicColor);
                }

                cell.setOnMouseClicked(event -> {
                    genomeLabel.setText("Genome: " + animal.getGenType().toString());
                    energyLabel.setText("Energy: " + animal.getEnergy());
                    plantsEatenLabel.setText("Plants eaten: " + animal.getPlantsEaten());
                    childrenLabel.setText("Children: " + animal.getChildren());
//                    descendantsLabel.setText("Descendants: " + animal.getDescendants());
                    ageLabel.setText("Age: " + (animal.getEnergy() > 0 ? animal.getAge() : "N/A"));
//                    deathDayLabel.setText("Death day: " + (animal.getEnergy() <= 0 ? animal.getDeathDay() : "N/A"));
                });
            }
        }

        genomeLabel = new Label("Genome: ");
        energyLabel = new Label("Energy: ");
        plantsEatenLabel = new Label("Plants eaten: ");
        childrenLabel = new Label("Children: ");
        descendantsLabel = new Label("Descendants: ");
        ageLabel = new Label("Age: ");
        deathDayLabel = new Label("Death day: ");

        int numberOfPlants = simulation.getMap().getPlantsPositions().size();
        Label plantCountLabel = new Label("Number of plants: " + numberOfPlants);

        int numberOfAnimals = simulation.getAnimals().size();
        Label animalCountLabel = new Label("Number of animals: " + numberOfAnimals);

        int numberOfFreeFields = mapHeight * mapWidth - numberOfPlants - numberOfAnimals;
        Label freeFieldsLabel = new Label("Number of free fields: " + numberOfFreeFields);

        int totalEnergy = simulation.getAnimals().stream().mapToInt(Animal::getEnergy).sum();
        double averageEnergy = numberOfAnimals > 0 ? Math.round((double) totalEnergy / numberOfAnimals*100.0)/100.0 : 0;
        Label averageEnergyLabel = new Label("Average energy level: " + averageEnergy);

        int totalDeadAge = simulation.getAllDeadAnimals().stream().mapToInt(Animal::getAge).sum();
        int numberOfDeadAnimals = simulation.getAllDeadAnimals().size();
        double averageDeadAge = numberOfDeadAnimals > 0 ? Math.round((double) totalDeadAge / numberOfDeadAnimals*100.0)/100.0 : 0;
        Label averageDeadAgeLabel = new Label("Average age of dead animals: " + averageDeadAge);

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> simulation.stop());

        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(e -> {
            simulation.resume();
            synchronized (simulation) {
                simulation.notify();
            }
        });

        VBox leftStats = new VBox(10, plantCountLabel, animalCountLabel, freeFieldsLabel, averageEnergyLabel, averageDeadAgeLabel);
        leftStats.setAlignment(Pos.CENTER);

        VBox rightStats = new VBox(10, genomeLabel, energyLabel, plantsEatenLabel, childrenLabel, descendantsLabel, ageLabel, deathDayLabel, stopButton, resumeButton);
        rightStats.setAlignment(Pos.CENTER);

        HBox statsLayout = new HBox(20, leftStats, rightStats);
        statsLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(20, mapGrid, statsLayout);
        mainLayout.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(mainLayout);
        Scene mapScene = new Scene(root);

        primaryStage.setScene(mapScene);
        primaryStage.setWidth(mapGrid.getWidth() + 40);
        primaryStage.setHeight(mapGrid.getHeight() + 200);
        primaryStage.sizeToScene();
    }

    private Rectangle getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);

            if (rowIndex == null) rowIndex = 0;
            if (columnIndex == null) columnIndex = 0;

            if (rowIndex == row && columnIndex == column) {
                if (node instanceof Rectangle) {
                    return (Rectangle) node;
                } else {
                    System.err.println("Node at row " + row + " and column " + column + " is not a Rectangle.");
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}