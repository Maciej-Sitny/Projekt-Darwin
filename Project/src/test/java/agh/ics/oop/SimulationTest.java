package  agh.ics.oop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {
    private Simulation simulation;
    private WorldMap map;
    private SimulationParameters parameters;
    private List<Vector2d> startPositions;

    @BeforeEach
    void setUp() {
        parameters = new SimulationParameters(10, 10, 20, 20, 3, 5, 100, 50, 25, 1, 2 , "Full randomness", 4, 3,"Round Globe",Boolean.FALSE);
        map = new Map(parameters);
        map.growPlant(20);
        startPositions = List.of(new Vector2d(2, 2), new Vector2d(3, 3), new Vector2d(2, 2));
        simulation = new Simulation(startPositions, map, 100, 4, 20, 15, null, parameters);
    }

    @Test
    void testSimulationInitialization() {
        assertNotNull(simulation.getMap());
        assertEquals(3, simulation.getAnimals().size());
        assertEquals(3, simulation.getAllAnimals().size());
        assertEquals(0, simulation.getAllDeadAnimals().size());
    }

    @Test
    void testDeadRemover() {
        for (Animal animal : simulation.getAnimals()) {
            animal.removeEnergy(1000);
        }

        simulation.deadRemover();

        assertEquals(0, simulation.getAnimals().size()); // Wszystkie zwierzęta powinny być usunięte
        assertEquals(3, simulation.getAllDeadAnimals().size());
    }

    @Test
    void testMoveOnMap() {
        Vector2d initialPosition = simulation.getAnimals().get(0).getPosition();
        simulation.moveOnMap();
        assertNotEquals(initialPosition, simulation.getAnimals().get(0).getPosition());
    }

    @Test
    void testConsumeOnMap() {
        Vector2d plantPosition = new Vector2d(2, 2);
        assertEquals(20, map.getPlantCount());
        map.growPlant(400);
        assertEquals(100, map.getPlantCount());

        simulation.consumeOnMap();

        assertEquals(98, map.getPlantCount());
    }

    @Test
    void testReproduceOnMap() {

        int initialSize = simulation.getAnimals().size();
        simulation.reproduceOnMap();

        assertEquals(4,simulation.getAnimals().size()); // Powinno pojawić się nowe zwierzę
    }

    @Test
    void testEnergyDepletion() {
        int initialEnergy = simulation.getAnimals().get(0).getEnergy();
        simulation.energyDepletion();
        assertTrue(simulation.getAnimals().get(0).getEnergy() < initialEnergy);
    }

    @Test
    void testAgeAnimal() {
        int initialAge = simulation.getAnimals().get(0).getAge();
        simulation.ageAnimal();
        assertEquals(initialAge + 1, simulation.getAnimals().get(0).getAge());
    }

    @Test
    void testPlantsGrowOnMap() {
        int initialPlantCount = map.getPlantCount();
        simulation.plantsGrowOnMap();
        assertTrue(map.getPlantCount() > initialPlantCount);
    }

    @Test
    void testCollectDailyData() {
        List<String> dailyData = simulation.collectDailyData(1);

        assertEquals(7, dailyData.size()); // Powinno być 7 danych (dzień, liczba zwierząt, liczba roślin itd.)
        assertEquals("1", dailyData.get(0)); // Dzień symulacji
    }

    @Test
    void testStopAndResumeSimulation() {
        simulation.stop();
        assertFalse(simulation.running());

        simulation.resume();
        assertTrue(simulation.running());
    }

    @Test
    void testOneDaySimulationCycle() {
        int initialAnimalCount = simulation.getAnimals().size();
        int initialPlantCount = map.getPlantCount();

        simulation.deadRemover();
        simulation.moveOnMap();
        simulation.consumeOnMap();
        simulation.reproduceOnMap();
        simulation.energyDepletion();
        simulation.plantsGrowOnMap();
        simulation.ageAnimal();

        assertTrue(simulation.getAnimals().size() <= initialAnimalCount + 1); // Może ubyć (śmierć) lub przybyć (rozmnażanie)
        assertTrue(map.getPlantCount() >= initialPlantCount); // Rośliny rosną
    }
}