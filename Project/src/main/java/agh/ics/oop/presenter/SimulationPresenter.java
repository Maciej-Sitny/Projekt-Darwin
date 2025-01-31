package  agh.ics.oop.presenter;
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


        grid.add(new Label("Mutation Variant:"), 0, 14); // Przenosimy do innego wiersza
        ComboBox<String> mutationVariant = new ComboBox<>();
        mutationVariant.getItems().addAll("Pełna losowość", "Podmianka");
        mutationVariant.setValue("Pełna losowość");
        grid.add(mutationVariant, 1, 14);

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
                        mapType.getValue()
                );

                initializeSimulation();
                new Thread(() -> {
                    simulation.run();
                    Platform.runLater(this::drawMap);
                }).start();
            }
        });

        grid.add(submitButton, 1, 15);
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

            // Sprawdzamy czy wszystkie wartości (oprócz Energy lost per tile away from the equator) są > 0
            for (int i = 0; i < fields.length - 1; i++) {
                int value = Integer.parseInt(fields[i].getText());
                if (value <= 0) {
                    String fieldName = getFieldName(i);
                    showAlert(fieldName + " must be greater than 0.");
                    return false;
                }
            }

            // Sprawdzamy warunki dla mutacji
            if (minMut > maxMut) {
                showAlert("Minimal number of mutations cannot be greater than maximal number of mutations.");
                return false;
            }

            if (maxMut > genomeLen) {
                showAlert("Maximal number of mutations cannot be greater than Genome Length.");
                return false;
            }

            // Sprawdzamy, czy energia wymagana do rozmnażania jest większa niż energia przekazywana potomstwu
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

    // Pomocnicza metoda do wyświetlania odpowiednich nazw pól
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
        Animal currentAnimal;
        int mapHeight = parameters.getMapHeight();
        int mapWidth = parameters.getMapWidth();


        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Rectangle cell = new Rectangle(20, 20);
                cell.setStroke(Color.BLACK); // Ustawiamy obramowanie
                cell.setFill(Color.GREEN); // Ustawiamy kolor tła na zielony


                cell.setOnMouseEntered(event -> {
                    cell.setFill(Color.LIGHTBLUE); // Zmieniamy kolor na podświetlenie
                });


                cell.setOnMouseExited(event -> {
                    cell.setFill(Color.GREEN); // Przywracamy oryginalny kolor
                });

                mapGrid.add(cell, j, i); // Dodajemy prostokąt do siatki
            }
        }

        for (Vector2d plantPosition : simulation.getMap().getPlantsPositions()) {
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(plantPosition.getY(), plantPosition.getX(), mapGrid);
            if (cell != null) {
                cell.setFill(Color.YELLOW);
                cell.setOnMouseEntered(event -> {
                    cell.setFill(Color.LIGHTYELLOW); // Zmieniamy kolor na podświetlenie
                });


                cell.setOnMouseExited(event -> {
                    cell.setFill(Color.YELLOW); // Przywracamy oryginalny kolor
                });
            }
        }


        Label genomeLabel = new Label("Genome: ");
        Label energyLabel = new Label("Energy: ");
        Label plantsEatenLabel = new Label("Plants eaten: ");
        Label childrenLabel = new Label("Children: ");
        Label ageLabel = new Label("Age: ");
        Label deathDayLabel = new Label("Death day: ");

        for (Animal animal : simulation.getAnimals()) {
            Vector2d position = animal.getPosition();
            Rectangle cell = (Rectangle) getNodeByRowColumnIndex(position.getY(), position.getX(), mapGrid);
            if (cell != null) {
                if (animal.getAge() == 1) {
                    cell.setFill(Color.BLUE);
                    cell.setOnMouseEntered(event -> {
                        cell.setFill(Color.LIGHTBLUE); // Zmieniamy kolor na podświetlenie
                    });


                    cell.setOnMouseExited(event -> {
                        cell.setFill(Color.BLUE); // Przywracamy oryginalny kolor
                    });
                } else {
                    int maxEnergy = parameters.getInitialEnergy();
                    int energy = Math.max(0, Math.min(animal.getEnergy(), maxEnergy));
                    double intensity = (double) energy / maxEnergy;
                    int redValue = (int) (255 * intensity);


                    Color dynamicColor = Color.rgb(redValue, 0, 0);
                    cell.setFill(dynamicColor);
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


        int numberOfPlants = simulation.getMap().getPlantsPositions().size();
        Label plantCountLabel = new Label("Number of plants: " + numberOfPlants);


        int numberOfAnimals = simulation.getAnimals().size();
        Label animalCountLabel = new Label("Number of animals: " + numberOfAnimals);


        Set<Vector2d> free = new HashSet<>();
        free.addAll(simulation.getMap().getPlantsPositions());
        free.addAll(simulation.getMap().getAnimals().keySet());
        int numberOfFreeCells = Math.max(0, free.size());
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



        VBox leftStats = new VBox(10, plantCountLabel, animalCountLabel, freeCellCountLabel, averageEnergyLabel, averageDeadAgeLabel);
        leftStats.setAlignment(Pos.CENTER_LEFT);

        VBox rightStats = new VBox(10, genomeLabel, energyLabel, plantsEatenLabel, childrenLabel, ageLabel, deathDayLabel, stopButton, resumeButton);
        rightStats.setAlignment(Pos.CENTER_LEFT);

        HBox statsLayout = new HBox(20, leftStats, rightStats);
        statsLayout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(20, mapGrid, statsLayout);
        mainLayout.setAlignment(Pos.CENTER);


        StackPane root = new StackPane(mainLayout);
        Scene mapScene = new Scene(root, 1000, 1000);
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
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}