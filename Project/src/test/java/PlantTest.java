package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlantTest {

    @Test
    void testConstructor() {
        Vector2d position = new Vector2d(2, 3);
        Plant plant = new Plant(position);

        assertEquals(2, plant.getPosition().getX());
        assertEquals(3, plant.getPosition().getY());
    }

    @Test
    void testToString() {
        Vector2d position = new Vector2d(2, 3);
        Plant plant = new Plant(position);

        assertEquals("P", plant.toString());
    }

    @Test
    void testGetPosition() {
        Vector2d position = new Vector2d(4, 5);
        Plant plant = new Plant(position);

        assertEquals(position, plant.getPosition());
    }

    @Test
    void testEqualityWithSamePosition() {
        Vector2d position1 = new Vector2d(1, 1);
        Vector2d position2 = new Vector2d(1, 1);
        Plant plant1 = new Plant(position1);
        Plant plant2 = new Plant(position2);

        assertTrue(plant1.getPosition().equals(plant2.getPosition()));
    }

    @Test
    void testEqualityWithDifferentPosition() {
        Vector2d position1 = new Vector2d(2, 3);
        Vector2d position2 = new Vector2d(3, 4);
        Plant plant1 = new Plant(position1);
        Plant plant2 = new Plant(position2);

        assertFalse(plant1.getPosition().equals(plant2.getPosition()));
    }
}