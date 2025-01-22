package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Simulation implements Runnable {
    private List<Animal> animals;
    private WorldMap map;
    private int plantEnergy;
    private int growNumber;

    public Simulation(List<Vector2d> positions, WorldMap map, int energy, int genTypeSize, int plantEnergy, int growNumber) {
        this.plantEnergy = plantEnergy;
        this.animals = new ArrayList<>();
        this.growNumber = growNumber;
        GenTypeStartGeneration generator = new GenTypeStartGeneration();
        for (Vector2d position : positions) {
            Animal animal = new Animal(position, energy, generator.generateGenType(genTypeSize));
            map.placeAnimal(animal);
            this.animals.add(animal);
        }
        this.map = map;
    }

    public List<Animal> getAnimals() {
        return this.animals;
    }

    public void run() {
        while (!this.animals.isEmpty()) {

            List<Animal> deadAnimals = new ArrayList<>();

            for (Animal animal : this.animals) {
                if (animal.getEnergy() <= 0) {
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

            //this.map.reproduction();
            // tutaj musisz jakoś to zrobić by to się wykonało dla każdego pola na mapie i dodało
            //wszystkie zwierzaki tutaj do animals TUTAJ, bo inaczej nigdy nie wejdą do symulacji

            this.map.growPlant(this.growNumber);

        }
    }
}


