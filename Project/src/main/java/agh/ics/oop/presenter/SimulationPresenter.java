package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationPresenter extends Application {
    private SimulationParameters parameters;
    private Stage primaryStage;
    private Simulation simulation;
    private Animal selectedAnimal;
    private Animal previousSelectedAnimal;

    // Declare labels at the class level
    private Label energyLabel = new Label("Energy: ");
    private Label genomeLabel = new Label("Genome: ");
    private Label genomeNumberLabel = new Label("Genome Number: ");
    private Label plantsEatenLabel = new Label("Plants eaten: ");
    private Label childrenLabel = new Label("Children: ");
    private Label descendantsLabel = new Label("Descendants: ");
    private Label ageLabel = new Label("Age: ");
    private Label deathDayLabel = new Label("Death day: ");
    private Label mostPopularGenLabel = new Label("Most popular genType: "); // Initialize the label

    private CSVWriter csvWriter;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Simulation");

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

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
        mutationVariant.getItems().addAll("Full randomness", "Replacement");
        mutationVariant.setValue("Full randomness");
        grid.add(mutationVariant, 1, 14);



        CheckBox saveDataCheckBox = new CheckBox("Save data to file");
        grid.add(saveDataCheckBox, 1, 15);

        CheckBox LoadConfiguration = new CheckBox("Loaded Configuration from entrydata");
        grid.add(LoadConfiguration, 1, 16);


        Button submitButton = new Button("Start");

        submitButton.setOnAction(e -> {
            if (LoadConfiguration.isSelected() ){
                try {
                    Scanner scanner = new Scanner(new File("src/main/java/agh/ics/oop/resources/entrydata"));
                    mapHeight.setText(scanner.nextLine().trim());
                    mapWidth.setText(scanner.nextLine().trim());
                    initialPlants.setText(scanner.nextLine().trim());
                    energyPerPlant.setText(scanner.nextLine().trim());
                    plantsPerDay.setText(scanner.nextLine().trim());
                    initialAnimals.setText(scanner.nextLine().trim());
                    initialEnergy.setText(scanner.nextLine().trim());
                    energyLost.setText(scanner.nextLine().trim());
                    energyToBeFed.setText(scanner.nextLine().trim());
                    energyUsedByParents.setText(scanner.nextLine().trim());
                    minMutations.setText(scanner.nextLine().trim());
                    maxMutations.setText(scanner.nextLine().trim());
                    genomeLength.setText(scanner.nextLine().trim());
                    scanner.close();
                } catch (FileNotFoundException ex) {
                    System.err.println("Error: File not found - " + ex.getMessage());
                }
            }
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
                        saveDataCheckBox.isSelected()
                );

                initializeSimulation();
                new Thread(() -> {
                    simulation.run();
                    Platform.runLater(this::drawMap);
                }).start();
            }
        });

        grid.add(submitButton, 1, 17);
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
        if (parameters.isSaveDataToFile()) {
            List<String> headers = List.of("Day", "Total Animals", "Total Plants", "Average Energy", "Number of Free Cells", "Average Age of Dead Animals", "Most popular genType");
            try {
                csvWriter = new CSVWriter("simulation_data.csv", headers);
                csvWriter.clearFile();
            } catch (IOException e) {
                System.err.println("Failed to initialize CSV writer: " + e.getMessage());
            }
        }

        List<Vector2d> positions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < parameters.getInitialAnimals(); i++) {
            positions.add(new Vector2d(random.nextInt(parameters.getMapWidth()), random.nextInt(parameters.getMapHeight())));
        }
        WorldMap map = new Map(parameters);

        map.growPlant(parameters.getInitialPlants());
        simulation = new Simulation(positions, map, parameters.getInitialEnergy(), parameters.getGenomeLength(), parameters.getEnergyPerPlant(), parameters.getPlantsPerDay(), this, parameters);
    }

    private void highlightSelectedAnimal(GridPane mapGrid) {
        if (selectedAnimal != null) {
            Vector2d position = selectedAnimal.getPosition();
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                cell.setFill(Color.PURPLE);
            }
        }
    }



    public void drawMap() {
        ArrayList<Integer> mostPopularGen = simulation.mostPopularGen();
        GridPane mapGrid = new GridPane();
        mapGrid.setGridLinesVisible(true);
        mapGrid.setAlignment(Pos.CENTER);
        int mapHeight = parameters.getMapHeight();
        int mapWidth = parameters.getMapWidth();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        int screenWidth = (int)bounds.getWidth();
        int screenHeight = (int)bounds.getHeight();

        int size = Math.min((screenWidth/2) / (mapWidth + 2), (screenHeight / 2) / (mapHeight + 2));


        int equatorWidth = mapHeight * 20 / 100;
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Rectangle cell = new Rectangle(size, size);
                cell.setStroke(Color.BLACK);
                if (!simulation.running() && i >= (int)mapHeight/2 - (int)equatorWidth/2 && i <= (int)mapHeight/2 + (int)equatorWidth/2){
                    cell.setFill(Color.LIGHTYELLOW);
                }
                else {
                    cell.setFill(Color.GREEN);
                }
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
                if (!simulation.running() && animal.getGenType().equals(mostPopularGen)) {
                    cell.setFill(Color.ORANGE);
                } else {
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
                }

                cell.setOnMouseClicked(event -> {
                    if (previousSelectedAnimal != null) {
                        resetAnimalColor(previousSelectedAnimal, mapGrid);
                    }
                    selectedAnimal = animal;
                    previousSelectedAnimal = animal;
                    updateAnimalStatistics();
                    highlightSelectedAnimal(mapGrid);
                });
            }
        }

        highlightSelectedAnimal(mapGrid);

        int numberOfPlants = simulation.getMap().getPlantsPositions().size();
        Label plantCountLabel = new Label("Number of plants: " + numberOfPlants);

        int numberOfAnimals = simulation.getAnimals().size();
        Label animalCountLabel = new Label("Number of animals: " + numberOfAnimals);

        Set<Vector2d> free = new HashSet<>();
        free.addAll(simulation.getMap().getPlantsPositions());
        free.addAll(simulation.getMap().getAnimals().keySet());
        int numberOfFreeFields = Math.max(0, mapWidth * mapHeight - free.size());
        Label freeFieldsLabel = new Label("Number of free fields: " + numberOfFreeFields);

        int totalEnergy = simulation.getAnimals().stream().mapToInt(Animal::getEnergy).sum();
        double averageEnergy = numberOfAnimals > 0 ? Math.round((double) totalEnergy / numberOfAnimals*100.0)/100.0 : 0;
        Label averageEnergyLabel = new Label("Average energy level: " + averageEnergy);

        int totalDeadAge = simulation.getAllDeadAnimals().stream().mapToInt(Animal::getAge).sum();
        int numberOfDeadAnimals = simulation.getAllDeadAnimals().size();
        double averageDeadAge = numberOfDeadAnimals > 0 ? Math.round((double) totalDeadAge / numberOfDeadAnimals*100.0)/100.0 : 0;
        Label averageDeadAgeLabel = new Label("Average age of dead animals: " + averageDeadAge);
        int totalNumberChildren = simulation.getAnimals().stream().mapToInt(Animal::getChildren).sum();
        int numberOfLivingAnimals = simulation.getAnimals().size();
        double averageNumberOfChildren = totalNumberChildren > 0 ? Math.round((double) totalNumberChildren / numberOfLivingAnimals*100.0)/100.0 : 0;
        mostPopularGenLabel.setText("Most popular genType: " + simulation.mostPopularGen().toString());
        Label averageChildNumberLabel = new Label("Average number of children " + averageNumberOfChildren);

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> {
            simulation.stop();
            drawMap();
        });

        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(e -> {
            simulation.resume();
            synchronized (simulation) {
                simulation.notify();
            }
            new Thread(() -> {
                while (simulation.running()) {
                    Platform.runLater(this::updateAnimalStatistics);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        });

        VBox leftStats = new VBox(10, plantCountLabel, animalCountLabel, freeFieldsLabel, averageEnergyLabel, averageDeadAgeLabel,averageChildNumberLabel, mostPopularGenLabel);
        leftStats.setAlignment(Pos.CENTER);

        VBox rightStats = new VBox(10, genomeLabel, genomeNumberLabel, energyLabel, plantsEatenLabel, childrenLabel, ageLabel, deathDayLabel, stopButton, resumeButton);
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

    private void resetAnimalColor(Animal animal, GridPane mapGrid) {
        Vector2d position = animal.getPosition();
        Rectangle cell = (Rectangle) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
        if (cell != null) {
            if (animal.getAge() == 1) {
                cell.setFill(Color.BLUE);
            }
            else if (animal.getGenType().equals(simulation.mostPopularGen())) {
                cell.setFill(Color.ORANGE);
            }
            else {
                int maxEnergy = parameters.getInitialEnergy();
                int energy = Math.max(0, Math.min(animal.getEnergy(), maxEnergy));
                double intensity = (double) energy / maxEnergy;
                int redValue = (int) (255 * intensity);
                Color dynamicColor = Color.rgb(redValue, 0, 0);
                cell.setFill(dynamicColor);
            }
        }
    }

    private void updateAnimalStatistics() {
        if (selectedAnimal != null) {
            genomeLabel.setText("Genome: " + selectedAnimal.getGenType().toString());
            genomeNumberLabel.setText("Genome Number: " + selectedAnimal.getGenNumber());
            energyLabel.setText("Energy: " + selectedAnimal.getEnergy());
            plantsEatenLabel.setText("Plants eaten: " + selectedAnimal.getPlantsEaten());
            childrenLabel.setText("Children: " + selectedAnimal.getChildren());
            ageLabel.setText("Age: " + (selectedAnimal.getEnergy() > 0 ? selectedAnimal.getAge() : "N/A"));
            if (selectedAnimal.getEnergy() <= 0 && selectedAnimal.getDeathDay() == null){
                selectedAnimal.setDeathDay(simulation.getDay());
            }
            deathDayLabel.setText("Death day: " + (selectedAnimal.getEnergy() <= 0 ? selectedAnimal.getDeathDay() : "N/A"));
        }
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