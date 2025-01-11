package agh.ics.oop;

import java.util.List;

public interface WorldMap extends MoveValidator {
    void placeAnimal(Animal animal);

    void move(Animal animal, int direction);

    List<WorldElement> getElements();

    Boundary getCurrentBounds();
}
