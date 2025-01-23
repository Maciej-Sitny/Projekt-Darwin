package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.observers.Observer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

        // Initial number of plants
        grid.add(new Label("Początkowa liczba roślin:"), 0, 2);
        TextField initialPlants = new TextField();
        grid.add(initialPlants, 1, 2);

        // Energy provided by eating one plant
        grid.add(new Label("Energia rośliny:"), 0, 3);
        TextField energyPerPlant = new TextField();
        grid.add(energyPerPlant, 1, 3);

        // Number of plants growing each day
        grid.add(new Label("Liczba roślin rosnących każdego dnia:"), 0, 4);
        TextField plantsPerDay = new TextField();
        grid.add(plantsPerDay, 1, 4);

        // Initial number of animals
        grid.add(new Label("Początkowa liczba zwierząt:"), 0, 5);
        TextField initialAnimals = new TextField();
        grid.add(initialAnimals, 1, 5);

        // Initial energy of animals
        grid.add(new Label("Początkowa energia zwierząt:"), 0, 6);
        TextField initialEnergy = new TextField();
        grid.add(initialEnergy, 1, 6);

        // Energy lost by an animal each day
        grid.add(new Label("Ile energii traci zwierzak jednego dnia:"), 0, 7);
        TextField energyLost = new TextField();
        grid.add(energyLost, 1, 7);

        // Energy required for an animal to be considered fed
        grid.add(new Label("Energia, żeby uznać zwierzę za najedzone (gotowe do rozmnażania):"), 0, 8);
        TextField energyToBeFed = new TextField();
        grid.add(energyToBeFed, 1, 8);

        // Energy used by parents to create offspring
        grid.add(new Label("Energia zużywana przez rodziców dla dziecka:"), 0, 9);
        TextField energyUsedByParents = new TextField();
        grid.add(energyUsedByParents, 1, 9);

        // Minimum and maximum number of mutations
        grid.add(new Label("Minimalna liczba mutacji:"), 0, 10);
        TextField minMutations = new TextField();
        grid.add(minMutations, 1, 10);

        grid.add(new Label("Maksymalna liczba mutacji:"), 0, 11);
        TextField maxMutations = new TextField();
        grid.add(maxMutations, 1, 11);

        // Mutation variant
        grid.add(new Label("Wariant mutacji:"), 0, 12);
        ComboBox<String> mutationVariant = new ComboBox<>();
        mutationVariant.getItems().addAll("Pełna losowość", "Podmianka");
        grid.add(mutationVariant, 1, 12);

        // Length of animal genome
        grid.add(new Label("Długość genomu (>=4)"), 0, 13);
        TextField genomeLength = new TextField();
        genomeLength.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*") && (change.getControlNewText().isEmpty() || Integer.parseInt(change.getControlNewText()) >= 4)) {
                return change;
            }
            return null;
        }));
        grid.add(genomeLength, 1, 13);

        grid.add(new Label("Odległość bieguna od równika:"), 0, 14);
        TextField poleDistance = new TextField();
        grid.add(poleDistance, 1, 14);

        // Submit button
        Button submitButton = new Button("OK");
        submitButton.setOnAction(e -> {
            parameters = new SimulationParameters(Integer.parseInt(mapHeight.getText()),
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
                    Integer.parseInt(poleDistance.getText()));
//            parameters = new SimulationParameters(10,10,10,10,2,20,100,1000,20,2,7,"Podmianka",9,1,5);
            initializeSimulation();
            new Thread(() -> {
                simulation.run();
                System.out.println(simulation.getMap().getPlantsPositions().size());
                Platform.runLater(() -> {
                    drawMap();
                });
            }).start();
        });
        grid.add(submitButton, 1, 15);

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
        mapGrid.setAlignment(Pos.CENTER);
        Animal currentAnimal;
        int mapHeight = parameters.getMapHeight();
        int mapWidth = parameters.getMapWidth();

        // Tworzymy tylko Rectangle w komórkach
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Rectangle cell = new Rectangle(20, 20); // Tworzymy prostokąt o szerokości 20px i wysokości 20px
                cell.setStroke(Color.BLACK); // Ustawiamy obramowanie
                cell.setFill(Color.GREEN); // Ustawiamy kolor tła na zielony

                // Obsługa zdarzenia najechania na komórkę
                cell.setOnMouseEntered(event -> {
                    cell.setFill(Color.LIGHTBLUE); // Zmieniamy kolor na podświetlenie
                });

                // Obsługa zdarzenia opuszczenia komórki
                cell.setOnMouseExited(event -> {
                    cell.setFill(Color.GREEN); // Przywracamy oryginalny kolor
                });

                mapGrid.add(cell, j, i); // Dodajemy prostokąt do siatki
            }
        }

        // Display plants
        for (Vector2d plantPosition : simulation.getMap().getPlantsPositions()) {
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(plantPosition.getY(), plantPosition.getX(), mapGrid);
            if (cell != null) {
                cell.setFill(Color.YELLOW); // Ustawiamy kolor rośliny na żółty
                cell.setOnMouseEntered(event -> {
                    cell.setFill(Color.LIGHTYELLOW); // Zmieniamy kolor na podświetlenie
                });

                // Obsługa zdarzenia opuszczenia komórki
                cell.setOnMouseExited(event -> {
                    cell.setFill(Color.YELLOW); // Przywracamy oryginalny kolor
                });
            }
        }


        Label genomeLabel = new Label("Genome: ");
        Label energyLabel = new Label("Energy: ");
        Label plantsEatenLabel = new Label("Plants eaten: ");
        Label childrenLabel = new Label("Children: ");
        Label descendantsLabel = new Label("Descendants: ");
        Label ageLabel = new Label("Age: ");
        Label deathDayLabel = new Label("Death day: ");

        // Display animals
        for (Animal animal : simulation.getAnimals()) {
            Vector2d position = animal.getPosition();
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                if (animal.getAge() == 1) { // Check if the animal is a child
                    cell.setFill(Color.BLUE); // Set the fill color to blue
                    cell.setOnMouseEntered(event -> {
                        cell.setFill(Color.LIGHTBLUE); // Zmieniamy kolor na podświetlenie
                    });

                    // Obsługa zdarzenia opuszczenia komórki
                    cell.setOnMouseExited(event -> {
                        cell.setFill(Color.BLUE); // Przywracamy oryginalny kolor
                    });
                } else {
                    int maxEnergy = parameters.getInitialEnergy(); // Assuming initial energy is the maximum energy
                    int energy = Math.max(0, Math.min(animal.getEnergy(), maxEnergy)); // Clamp energy between 0 and maxEnergy
                    double intensity = (double) energy / maxEnergy; // Normalize energy to [0, 1]
                    int redValue = (int) (255 * intensity); // Map intensity to [0, 255] for red channel
//                int greenValue = (int) (255 * (1 - intensity)); // Map remaining intensity to green channel (optional)

                    // Use JavaFX Color API to dynamically set fill color
                    Color dynamicColor = Color.rgb(redValue, 0, 0); // RGB with no blue component
                    cell.setFill(dynamicColor); // Set the fill color dynamically
                    cell.setOnMouseEntered(event -> {
                        cell.setFill(Color.PINK); // Zmieniamy kolor na podświetlenie
                    });

                    // Obsługa zdarzenia opuszczenia komórki
                    cell.setOnMouseExited(event -> {
                        cell.setFill(dynamicColor); // Przywracamy oryginalny kolor
                    });
                }
//                cell.setOnMouseClicked(event -> {
////                    Observer obs =animal.getObserver();
////                    genomeLabel.setText("Genome: " + obs.getGenType().toString());
////                    energyLabel.setText("Energy: " + obs.getEnergy());
////                    plantsEatenLabel.setText("Plants eaten: " + obs.getPlantsEaten());
////                    childrenLabel.setText("Children: " + obs.getChildren());
////                    ageLabel.setText("Age: " + (obs.getEnergy() > 0 ? obs.getAge() : "N/A"));
//                    currentAnimal = animal;
//                });
            }
        }

        // Create a label to display the number of plants
        int numberOfPlants = simulation.getMap().getPlantsPositions().size();
        Label plantCountLabel = new Label("Number of plants: " + numberOfPlants);

        // Create a label to display the number of animals
        int numberOfAnimals = simulation.getAnimals().size();
        Label animalCountLabel = new Label("Number of animals: " + numberOfAnimals);

        int numberOfFreeCells = mapHeight * mapWidth - numberOfPlants - numberOfAnimals;
        Label freeCellCountLabel = new Label("Number of free cells: " + numberOfFreeCells);

        int totalEnergy = simulation.getAnimals().stream().mapToInt(Animal::getEnergy).sum();
        double averageEnergy = numberOfAnimals > 0 ? (int) totalEnergy / numberOfAnimals : 0;
        Label averageEnergyLabel = new Label("Average energy level: " + averageEnergy);

        int totalDeadAge = simulation.getAllDeadAnimals().stream().mapToInt(Animal::getAge).sum();
        int numberOfDeadAnimals = simulation.getAllDeadAnimals().size();
        double averageDeadAge = numberOfDeadAnimals > 0 ? (int) totalDeadAge / numberOfDeadAnimals : 0;
        Label averageDeadAgeLabel = new Label("Average age of dead animals: " + averageDeadAge);

//        genomeLabel.setText("Genome: " + currentAnimal.getGenType().toString());
//        energyLabel.setText("Energy: " + currentAnimal.getEnergy());
//        plantsEatenLabel.setText("Plants eaten: " + currentAnimal.getPlantsEaten());
//        childrenLabel.setText("Children: " + currentAnimal.getChildren());
//        ageLabel.setText("Age: " + (currentAnimal.getEnergy() > 0 ? currentAnimal.getAge() : "N/A"));


        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> simulation.stop());

        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(e -> {
            simulation.resume();
            synchronized (simulation) {
                simulation.notify();
            }
        });

        // Create a layout to hold the map and the labels
//        VBox layout = new VBox(10, mapGrid, plantCountLabel, animalCountLabel, freeCellCountLabel, averageEnergyLabel, averageDeadAgeLabel, stopButton, resumeButton, genomeLabel, energyLabel, plantsEatenLabel, childrenLabel, ageLabel);
        VBox leftStats = new VBox(10, plantCountLabel, animalCountLabel, freeCellCountLabel, averageEnergyLabel, averageDeadAgeLabel);
        leftStats.setAlignment(Pos.CENTER_LEFT);

        VBox rightStats = new VBox(10, genomeLabel, energyLabel, plantsEatenLabel, childrenLabel, descendantsLabel, ageLabel, deathDayLabel, stopButton, resumeButton);
        rightStats.setAlignment(Pos.CENTER_LEFT);

        // Create an HBox to hold the two VBox layouts
        HBox statsLayout = new HBox(20, leftStats, rightStats);
        statsLayout.setAlignment(Pos.CENTER);

        // Create a VBox to hold the map and the statistics
        VBox mainLayout = new VBox(20, mapGrid, statsLayout);
        mainLayout.setAlignment(Pos.CENTER);
//        layout.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(mainLayout);
        Scene mapScene = new Scene(root, 20* parameters.getMapWidth()*1.5, 20* parameters.getMapHeight()*1.5);
        primaryStage.setScene(mapScene);
    }

    private Rectangle getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);

            // Domyślne przypisanie wartości, jeśli indeksy są null
            if (rowIndex == null) rowIndex = 0;
            if (columnIndex == null) columnIndex = 0;

            // Sprawdzamy, czy węzeł znajduje się na danej pozycji
            if (rowIndex == row && columnIndex == column) {
                // Sprawdzamy, czy węzeł jest typu Rectangle
                if (node instanceof Rectangle) {
                    return (Rectangle) node;
                } else {
                    // Zgłaszamy błąd, jeśli węzeł nie jest Rectangle
                    System.err.println("Node at row " + row + " and column " + column + " is not a Rectangle.");
                }
            }
        }
        return null;  // Zwracamy null, jeśli węzeł nie został znaleziony
    }

    public static void main(String[] args) {
        launch(args);
    }
}