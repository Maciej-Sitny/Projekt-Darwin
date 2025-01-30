package agh.ics.oop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {

    private Animal animal;
    private SimulationParameters parameters;
    private MoveValidator moveValidator;

    @BeforeEach
    void setUp() {
        parameters = new SimulationParameters(10, 10, 20, 20, 3, 5, 100, 50, 25, 1, 2 , "Podmianka", 4, 3,"Round Globe");
        animal = new Animal(new Vector2d(2, 2), parameters);
        moveValidator = new Map(parameters);
    }

    @Test
    void testDefaultConstructor() {
        Animal animal = new Animal();

        assertEquals(new Vector2d(2, 2), animal.getPosition());
        assertEquals(MapDirection.NORTH, animal.getOrientation());
        assertEquals(100, animal.getEnergy());
        assertNotNull(animal.getGenType());
        assertEquals(10, animal.getGenType().size());
        assertEquals(0, animal.getGenNumber());
        assertNotNull(animal.getObserver());
    }

    @Test
    void testFullParameterConstructor() {
        Vector2d position = new Vector2d(3, 3);
        MapDirection orientation = MapDirection.EAST;
        int energy = 50;
        ArrayList<Integer> genType = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        int genNumber = 2;

        Animal animal = new Animal(position, orientation, energy, genType, genNumber, parameters);

        assertEquals(position, animal.getPosition());
        assertEquals(orientation, animal.getOrientation());
        assertEquals(energy, animal.getEnergy());
        assertEquals(genType, animal.getGenType());
        assertEquals(genNumber, animal.getGenNumber());
        assertNotNull(animal.getObserver());
    }

    @Test
    void testInitialValues() {
        assertEquals(new Vector2d(2, 2), animal.getPosition());
        assertEquals(MapDirection.NORTH, animal.getOrientation());
        assertEquals(100, animal.getEnergy());
        assertEquals(0, animal.getGenNumber());
        assertEquals(0, animal.getAge());
        assertEquals(0, animal.getChildren());
    }

    @Test
    void testRemoveEnergy() {
        animal.removeEnergy(30);
        assertEquals(70, animal.getEnergy());
    }

    @Test
    void testAddEnergy() {
        animal.addEnergy(50);
        assertEquals(150, animal.getEnergy());
    }

    @Test
    void testEatPlant() {
        assertEquals(0, animal.getPlantsEaten());
        animal.eatPlant();
        assertEquals(1, animal.getPlantsEaten());
    }

    @Test
    void testNextGen() {
        ArrayList<Integer> genType = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        animal = new Animal(new Vector2d(2, 2), 100, genType);

        assertEquals(0, animal.getGenNumber());
        animal.nextGen();
        assertEquals(1, animal.getGenNumber());
        animal.nextGen();
        animal.nextGen();
        animal.nextGen();
        assertEquals(4, animal.getGenNumber());
        animal.nextGen();
        assertEquals(0, animal.getGenNumber()); // Powinien wrócić do początku
    }

    @Test
    void testMoveValidPosition() {
        Vector2d oldPosition = animal.getPosition();
        animal.move(1, moveValidator);
        assertNotEquals(oldPosition, animal.getPosition());
    }

    @Test
    void testMoveInvalidPosition() {
        animal = new Animal(new Vector2d(4, 2), parameters);
        Vector2d oldPosition = animal.getPosition();
        animal.move(1, moveValidator);
        assertNotEquals(oldPosition, animal.getPosition()); // Sprawdzamy, czy została poprawnie zmieniona
    }

    @Test
    void testReproduce() {
        Animal parent2 = new Animal(new Vector2d(2, 2), parameters);
        Animal child = animal.reproduce(parent2);

        assertNotNull(child);
        assertEquals(new Vector2d(2, 2), child.getPosition());
        assertEquals(75, animal.getEnergy());
        assertEquals(75, parent2.getEnergy());
        assertEquals(50, child.getEnergy());
        assertEquals(4,child.getGenType().size());
        assertTrue(child.getGenNumber() < animal.getGenType().size());
    }

    @Test
    void testMutationFullRandomness() {
        ArrayList<Integer> genotype = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        ArrayList<Integer> mutated = animal.mutationFullRandomness(genotype);

        assertNotNull(mutated);
        assertEquals(5, mutated.size());
    }

    @Test
    void testMutationSwap() {
        ArrayList<Integer> genotype = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        ArrayList<Integer> mutated = animal.mutationSwap(genotype);

        assertNotNull(mutated);
        assertEquals(5, mutated.size());
    }
}