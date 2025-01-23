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
    private SimulationParameters parameters;

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
        }
        this.map = map;
        this.parameters = parameters;
    }

    public WorldMap getMap() {
        return this.map;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public void deadRemover(){
        List<Animal> deadAnimals = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (animal.getEnergy() <= 0) {
                deadAnimals.add(animal);
                this.map.removeDeadAnimal(animal);
            }
        }
        this.animals.removeAll(deadAnimals);
    }

    public void moveOnMap(){
        for (Animal animal : this.animals) {
            ArrayList<Integer> gens = animal.getGenType();
            this.map.move(animal, gens.get(animal.getGenNumber()));
            animal.nextGen();
        }
    }

    public void consumeOnMap(){
        this.map.consume();
    }

    public void reproduceOnMap(){
        List<Animal> children = this.map.reproduction();
        this.animals.addAll(children);
    }

    public void plantsGrowOnMap(){
        this.map.growPlant(this.growNumber);
    }

    public void run() {
        while (!this.animals.isEmpty()) {

            this.deadRemover();

            this.moveOnMap();

            this.consumeOnMap();

            this.reproduceOnMap();

            this.plantsGrowOnMap();


            Platform.runLater(() -> {
                if (this.presenter != null) {
                    presenter.drawMap(); // Rysowanie mapy
                }
            });


            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}