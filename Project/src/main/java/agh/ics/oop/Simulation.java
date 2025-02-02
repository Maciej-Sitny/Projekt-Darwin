package agh.ics.oop;

import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class
Simulation implements Runnable {
    private List<Animal> animals;
    private WorldMap map;
    private int plantEnergy;
    private int growNumber;
    private SimulationPresenter presenter;
    private SimulationParameters parameters;
    public List<Animal> allanimals= new ArrayList<>();
    private boolean isRunning = true;
    private CSVWriter csvWriter;
    private int day = 0;
    private List<Animal> allDeadAnimals = new ArrayList<>();

    public boolean running() {
        return this.isRunning;
    }

    public Simulation(List<Vector2d> positions, WorldMap map, int energy, int genTypeSize, int plantEnergy, int growNumber, SimulationPresenter presenter,SimulationParameters parameters) {
        this.plantEnergy = plantEnergy;
        this.animals = new ArrayList<>();
        this.growNumber = growNumber;
        this.presenter = presenter;
        GenTypeStartGeneration generator = new GenTypeStartGeneration();
        for (Vector2d position : positions) {
            Animal animal = new Animal(position, MapDirection.NORTH, energy, generator.generateGenType(genTypeSize),0,parameters);
            map.placeAnimal(animal);
            this.animals.add(animal);
            this.allanimals.add(animal);
        }
        this.map = map;
        this.parameters = parameters;

        if (parameters.isSaveDataToFile()) {
            List<String> headers = List.of("Day", "Total Animals", "Total Plants", "Average Energy", "Number of Free Cells", "Average Age of Dead Animals","Most Popular GenType");
            try {
                csvWriter = new CSVWriter("simulation_data.csv", headers);
            } catch (IOException e) {
                System.err.println("Failed to initialize CSV writer: " + e.getMessage());
            }
        }

    }
    public int getDay(){
        return this.day;
    }

    public WorldMap getMap() {
        return this.map;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public List<Animal> getAllAnimals() {
        return this.allanimals;
    }

    public List<Animal> getAllDeadAnimals() {
        return this.allDeadAnimals;
    }

    public void deadRemover(){
        List<Animal> deadAnimals = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (animal.getEnergy() <= 0) {
                deadAnimals.add(animal);
                allDeadAnimals.add(animal);
                this.map.removeDeadAnimal(animal);
            }
        }
        this.animals.removeAll(deadAnimals);
    }

    public void moveOnMap(){
        for (Animal animal : this.animals) {
            ArrayList<Integer> gens = animal.getGenType();
            this.map.move(animal, gens.get(animal.getGenNumber()%animal.getGenType().size()));
            animal.nextGen();
        }
    }

    public void consumeOnMap(){
        this.map.consume();
    }

    public void reproduceOnMap(){
        List<Animal> children = this.map.reproduction();
        this.animals.addAll(children);
        this.allanimals.addAll(children);
    }

    public void energyDepletion() {
        if (parameters.getMapType().equals("Poles")) {
            for (Animal animal : animals) {
                animal.removeEnergy(parameters.getEnergyLost() + Math.abs(parameters.getMapHeight() / 2 - animal.getPosition().getY()));
            }
        } else {
            for (Animal animal : animals) {
                animal.removeEnergy(parameters.getEnergyLost());
            }
        }
    }

    public void plantsGrowOnMap(){
        this.map.growPlant(this.growNumber);
    }

    public void ageAnimal(){
        for (Animal animal : animals) {
            animal.addAge();
        }
    }

    public List<String> collectDailyData(int day) {
        int totalAnimals = map.getElements().stream().filter(e -> e instanceof Animal).toList().size();
        int totalPlants = map.getPlantCount();
        int totalEnergy = map.getElements().stream()
                .filter(e -> e instanceof Animal)
                .mapToInt(e -> ((Animal) e).getEnergy())
                .sum();
        int averageEnergy = totalAnimals > 0 ? totalEnergy / totalAnimals : 0;
        int numberOfFreeCells = parameters.getMapHeight() * parameters.getMapWidth() - totalAnimals - totalPlants;
        int totalDeadAge = allDeadAnimals.stream().mapToInt(Animal::getAge).sum();
        int averageDeadAge = allDeadAnimals.size() > 0 ? (int) totalDeadAge / allDeadAnimals.size() : 0;
        ArrayList<Integer> mostPopularGen = mostPopularGen();
        return List.of(
                String.valueOf(day),
                String.valueOf(totalAnimals),
                String.valueOf(totalPlants),
                String.valueOf(averageEnergy),
                String.valueOf(numberOfFreeCells),
                String.valueOf(averageDeadAge),
                String.valueOf(mostPopularGen)
        );
    }

    public void stop() {
        isRunning = false;
        if (csvWriter != null) {
            try {
                csvWriter.close();
            } catch (IOException e) {
                System.err.println("Failed to close CSV writer: " + e.getMessage());
            }
        }

    }

    public void resume() {
        isRunning = true;
        if (csvWriter != null) {
            try {
                csvWriter.open();
            } catch (IOException e) {
                System.err.println("Failed to open CSV writer: " + e.getMessage());
            }
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

            this.energyDepletion();

            this.plantsGrowOnMap();

            this.ageAnimal();


            Platform.runLater(() -> {
                if (this.presenter != null) {
                    presenter.drawMap();
                }
            });

            if (parameters.isSaveDataToFile()) {
                try {
                    List<String> dailyData = collectDailyData(day);
                    csvWriter.writeLine(dailyData);
                } catch (IOException e) {
                    System.err.println("Failed to write to CSV: " + e.getMessage());
                }
            }

            day++;

            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    public ArrayList<Integer> mostPopularGen(){
        Map<ArrayList<Integer>, Integer> genTypeCounter = new HashMap<>();
        for (Animal animal : animals) {
            ArrayList<Integer> genType = animal.getGenType();
            genTypeCounter.put(genType, genTypeCounter.getOrDefault(genType, 0) + 1);
        }
        return genTypeCounter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

    }
}