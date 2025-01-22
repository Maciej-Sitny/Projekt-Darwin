package agh.ics.oop;

import java.util.List;

public interface WorldMap extends MoveValidator {
    void placeAnimal(Animal animal);

    void move(Animal animal, int direction);

    List<WorldElement> getElements();

    Boundary getCurrentBounds();

    void consume(int energy);

    boolean isOccupied(Vector2d currentPosition);

    void removeDeadAnimal(Animal animal);

    void reproduction();

    void growPlant(int amount);
}
