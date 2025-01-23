package agh.ics.oop;

import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private List<Animal> animals;
    private WorldMap map;
    private int plantEnergy;
    private int growNumber;
    private SimulationPresenter presenter;
    private SimulationParameters parameters;
    private CSVWriter csvWriter;
    private int day = 0;
    private boolean isRunning = true;

    public Simulation(List<Vector2d> positions, WorldMap map, int energy, int genTypeSize, int plantEnergy, int growNumber, SimulationPresenter presenter, SimulationParameters parameters) {
        this.plantEnergy = plantEnergy;
        this.animals = new ArrayList<>();
        this.growNumber = growNumber;
        this.presenter = presenter;
        GenTypeStartGeneration generator = new GenTypeStartGeneration();
        for (Vector2d position : positions) {
            Animal animal = new Animal(position, MapDirection.NORTH, energy, generator.generateGenType(genTypeSize), 0, parameters);
            map.placeAnimal(animal);
            this.animals.add(animal);
        }
        this.map = map;
        this.parameters = parameters;

        List<String> headers = List.of("Day", "Total Animals", "Total Plants", "Average Energy");
        try {
            csvWriter = new CSVWriter("simulation_data.csv", headers);
        } catch (IOException e) {
            System.err.println("Failed to initialize CSV writer: " + e.getMessage());
        }
    }

    public WorldMap getMap() {
        return this.map;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public void deadRemover() {
        List<Animal> deadAnimals = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (animal.getEnergy() <= 0) {
                deadAnimals.add(animal);
                this.map.removeDeadAnimal(animal);
            }
        }
        this.animals.removeAll(deadAnimals);
    }

    public void moveOnMap() {
        for (Animal animal : this.animals) {
            ArrayList<Integer> gens = animal.getGenType();
            this.map.move(animal, gens.get(animal.getGenNumber()));
            animal.nextGen();
        }
    }

    public void consumeOnMap() {
        this.map.consume();
    }

    public void reproduceOnMap() {
        List<Animal> children = this.map.reproduction();
        this.animals.addAll(children);
    }

    public void plantsGrowOnMap() {
        this.map.growPlant(this.growNumber);
    }

    public List<String> collectDailyData(int day) {
        int totalAnimals = map.getElements().stream().filter(e -> e instanceof Animal).toList().size();
        int totalPlants = map.getPlantCount();
        int totalEnergy = map.getElements().stream()
                .filter(e -> e instanceof Animal)
                .mapToInt(e -> ((Animal) e).getEnergy())
                .sum();
        int averageEnergy = totalAnimals > 0 ? totalEnergy / totalAnimals : 0;

        return List.of(
                String.valueOf(day),          // Day of simulation
                String.valueOf(totalAnimals), // Number of animals
                String.valueOf(totalPlants),  // Number of plants
                String.valueOf(averageEnergy) // Average energy
        );
    }

    public void stop() {
        isRunning = false;
        try {
            csvWriter.close();
        } catch (IOException e) {
            System.err.println("Failed to close CSV writer: " + e.getMessage());
        }
    }

    public void resume() {
        isRunning = true;
        try {
            csvWriter.open();
        } catch (IOException e) {
            System.err.println("Failed to open CSV writer: " + e.getMessage());
        }
    }

    public void run() {
        while (!this.animals.isEmpty()) {
            synchronized (this) {
                while (!isRunning) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            this.deadRemover();
            this.moveOnMap();
            this.consumeOnMap();
            this.reproduceOnMap();
            this.plantsGrowOnMap();

            Platform.runLater(() -> {
                if (this.presenter != null) {
                    presenter.drawMap();
                }
            });

            try {
                List<String> dailyData = collectDailyData(day);
                csvWriter.writeLine(dailyData);
            } catch (IOException e) {
                System.err.println("Failed to write to CSV: " + e.getMessage());
            }

            day++;

            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}