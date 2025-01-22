package agh.ics.oop;

import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private List<Animal> animals;
    private WorldMap map;
    private int plantEnergy;
    private int growNumber;
    private SimulationPresenter presenter;

    public Simulation(List<Vector2d> positions, WorldMap map, int energy, int genTypeSize, int plantEnergy, int growNumber, SimulationPresenter presenter) {
        this.plantEnergy = plantEnergy;
        this.animals = new ArrayList<>();
        this.growNumber = growNumber;
        this.presenter = presenter;
        GenTypeStartGeneration generator = new GenTypeStartGeneration();
        for (Vector2d position : positions) {
            Animal animal = new Animal(position, energy, generator.generateGenType(genTypeSize));
            map.placeAnimal(animal);
            this.animals.add(animal);
        }
        this.map = map;
    }

    public WorldMap getMap() {
        return this.map;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public void run() {
        while (!this.animals.isEmpty()) {
            List<Animal> deadAnimals = new ArrayList<>();
            for (Animal animal : this.animals) {
                if (animal.getEnergy() <= 0) {
                    deadAnimals.add(animal);
                    this.map.removeDeadAnimal(animal);
                }
            }
            this.animals.removeAll(deadAnimals);

            for (Animal animal : this.animals) {
                ArrayList<Integer> gens = animal.getGenType();
                this.map.move(animal, gens.get(animal.getGenNumber()));
                animal.nextGen();
            }

            this.map.consume(this.plantEnergy);
            List<Animal> children = this.map.reproduction();
            this.animals.addAll(children);
            this.map.growPlant(this.growNumber);

            // Update the map after each step on the JavaFX Application Thread
            Platform.runLater(() -> presenter.drawMap());

            // Add a delay to visualize the steps
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}