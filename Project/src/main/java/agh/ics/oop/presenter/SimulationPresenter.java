package agh.ics.oop.presenter;

import agh.ics.oop.SimulationParameters;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SimulationPresenter extends Application {

    private SimulationParameters parameters = new SimulationParameters();

    @Override
    public void start(Stage primaryStage) {
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
            parameters.setMapHeight(Integer.parseInt(mapHeight.getText()));
            parameters.setMapWidth(Integer.parseInt(mapWidth.getText()));
            parameters.setMapVariant(mapVariant.getValue());
            parameters.setInitialPlants(Integer.parseInt(initialPlants.getText()));
            parameters.setEnergyPerPlant(Integer.parseInt(energyPerPlant.getText()));
            parameters.setPlantsPerDay(Integer.parseInt(plantsPerDay.getText()));
            parameters.setInitialAnimals(Integer.parseInt(initialAnimals.getText()));
            parameters.setInitialEnergy(Integer.parseInt(initialEnergy.getText()));
            parameters.setEnergyToBeFed(Integer.parseInt(energyToBeFed.getText()));
            parameters.setEnergyUsedByParents(Integer.parseInt(energyUsedByParents.getText()));
            parameters.setMinMutations(Integer.parseInt(minMutations.getText()));
            parameters.setMaxMutations(Integer.parseInt(maxMutations.getText()));
            parameters.setMutationVariant(mutationVariant.getValue());
            parameters.setGenomeLength(Integer.parseInt(genomeLength.getText()));
            // Use parameters in other classes as needed
        });
        grid.add(submitButton, 1, 16);

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}