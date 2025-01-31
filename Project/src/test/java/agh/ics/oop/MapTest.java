package agh.ics.oop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;

class MapTest {

    private Map map;
    private SimulationParameters parameters;

    @BeforeEach
    void setUp() {
        parameters = new SimulationParameters(10, 10, 20, 10, 5, 2, 3, 1000, 10, 1, 3, "Full randomness", 7, 2, "Round Globe",Boolean.FALSE);
        map = new Map(parameters);
    }



    @Test
    void testPlaceAnimal() {
        Animal animal = new Animal(new Vector2d(2, 2), parameters);
        map.placeAnimal(animal);
        assertTrue(map.isOccupied(new Vector2d(2, 2)));
    }

    @Test
    void testGrowPlant() {
        map.growPlant(3);
        assertEquals(3, map.getPlantCount());
    }

    @Test
    void testGetElements() {
        Animal animal = new Animal(new Vector2d(2, 2), parameters);
        map.placeAnimal(animal);
        List<WorldElement> elements = map.getElements();
        assertEquals(1, elements.size());
        assertTrue(elements.contains(animal));
    }

    @Test
    void testResolveConflict() {
        Animal animal1 = new Animal(new Vector2d(2, 2), parameters);
        Animal animal2 = new Animal(new Vector2d(2, 2), parameters);
        map.placeAnimal(animal1);
        map.placeAnimal(animal2);
        List<Animal> contenders = List.of(animal1, animal2);
        Animal winner = map.resolveConflict(contenders);
        assertTrue(contenders.contains(winner));
    }




    @Test
    void testFindPlaceForPlants() {
        Set<Vector2d> freePlaces = map.findPlaceForPlants();
        assertTrue(freePlaces.size() > 0);
    }

    @Test
    void testFindPlaceForPlantsOnEquator() {
        Set<Vector2d> freePlaces = map.findPlaceForPlantsOnEquator();
        assertTrue(freePlaces.size() > 0);
    }

    @Test
    void testFindPlaceForPlantsNotOnEquator() {
        Set<Vector2d> freePlaces = map.findPlaceForPlantsNotOnEquator();
        assertTrue(freePlaces.size() > 0);
    }

    @Test
    void testIsOccupied() {
        Animal animal = new Animal(new Vector2d(2, 2), parameters);
        map.placeAnimal(animal);
        assertTrue(map.isOccupied(new Vector2d(2, 2)));
        assertFalse(map.isOccupied(new Vector2d(5, 5)));
    }

    @Test
    void testAreThereAnimals() {
        Animal animal = new Animal(new Vector2d(2, 2), parameters);
        map.placeAnimal(animal);
        assertTrue(map.areThereAnimals(new Vector2d(2, 2)));
        assertFalse(map.areThereAnimals(new Vector2d(7, 7)));
    }

    @Test
    void testCanMoveToGlobe(){
        assertFalse( map.canMoveTo(new Vector2d(10,10)));
        assertTrue(map.canMoveTo(new Vector2d(9,9)));
        assertTrue(map.canMoveTo(new Vector2d(10,9)));
        assertTrue(map.canMoveTo(new Vector2d(-1,9)));
        assertFalse(map.canMoveTo(new Vector2d(8,-1)));
    }

    @Test
    void testCanMoveToPoles(){
        parameters = new SimulationParameters(10, 10, 20, 10, 5, 2, 3, 1000, 10, 1, 3, "Full randomness", 7, 2, "Poles",Boolean.FALSE);
        map = new Map(parameters);
        assertFalse(map.canMoveTo(new Vector2d(10,10)));
        assertTrue(map.canMoveTo(new Vector2d(9,9)));
        assertFalse(map.canMoveTo(new Vector2d(10,9)));
        assertFalse(map.canMoveTo(new Vector2d(-1,9)));
        assertFalse(map.canMoveTo(new Vector2d(8,-1)));
    }


}