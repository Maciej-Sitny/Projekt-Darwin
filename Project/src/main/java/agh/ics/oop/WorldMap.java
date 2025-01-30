package agh.ics.oop;

import java.util.List;
import java.util.Set;

public interface WorldMap extends MoveValidator {
    void placeAnimal(Animal animal);

    void move(Animal animal, int direction);

    List<WorldElement> getElements();

    Boundary getCurrentBounds();

    void consume();

    boolean isOccupied(Vector2d currentPosition);

    void removeDeadAnimal(Animal animal);

    List<Animal> reproduction();

    void growPlant(int amount);

    Set<Vector2d> getPlantsPositions();
    Set<Vector2d> findPlaceForPlants();
    int getPlantCount();
    java.util.Map<Vector2d, List<Animal>> getAnimals();
}
