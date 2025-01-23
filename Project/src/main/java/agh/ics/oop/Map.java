package agh.ics.oop;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Map implements WorldMap {
    protected java.util.Map<Vector2d, List<Animal>> animals = new HashMap<>();
    protected java.util.Map<Vector2d, Plant> plants = new HashMap<>();
    private int width;
    private int height;
    private int equatorWidth;
    private int energyPerPlant;
    private int plantsPerDay;
    private int energyToBeFed;
    private int energyUsedByParents;

    public Map(SimulationParameters parameters) {

        this.width = parameters.getMapWidth();
        this.height = parameters.getMapHeight();
        this.equatorWidth = (int) (parameters.getMapHeight() * 20 / 100);
        this.energyPerPlant = parameters.getEnergyPerPlant(); // użyte
        this.plantsPerDay = parameters.getPlantsPerDay(); //użyte
        this.energyToBeFed = parameters.getEnergyToBeFed(); //użyte
        this.energyUsedByParents = parameters.getEnergyUsedByParents(); //użyte
    }

    public boolean canMoveTo(Vector2d position) {
        if (position.getY() < 0 || position.getY() > this.height) {
            return false;
        } else {
            return true;
        }
    }

    public void placeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
    }

    public void growPlant(int amount) {
        Random random = new Random();

        if (amount >= this.height * this.width - this.getPlantCount()) {
            Set<Vector2d> freePlaces = this.findPlaceForPlants();
            for (Vector2d position : freePlaces) {
                this.plants.put(position, new Plant(position));
            }
        } else {
            int cnt = 0;


            Set<Vector2d> freeEq = this.findPlaceForPlantsOnEquator();
            Set<Vector2d> notEq = this.findPlaceForPlantsNotOnEquator();

            while (cnt < amount) {



                if (freeEq.isEmpty()) {
                    if (!notEq.isEmpty()) {
                        Vector2d position = notEq.iterator().next();
                        this.plants.put(position, new Plant(position));
                        notEq.remove(position);
                        cnt++;
                    }
                }

                else if (notEq.isEmpty()) {
                    if (!freeEq.isEmpty()) {
                        Vector2d position = freeEq.iterator().next();
                        this.plants.put(position, new Plant(position));
                        freeEq.remove(position);
                        cnt++;
                    }
                }

                else {
                    int tmp = random.nextInt(5);
                    if (tmp % 5 < 4) {

                        if (!freeEq.isEmpty()) {
                            Vector2d position = freeEq.iterator().next();
                            this.plants.put(position, new Plant(position));
                            freeEq.remove(position);
                            cnt++;
                        }
                    } else {
                        if (!notEq.isEmpty()) {
                            Vector2d position = notEq.iterator().next();
                            this.plants.put(position, new Plant(position));
                            notEq.remove(position);
                            cnt++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> elements = new ArrayList<>();
        animals.values().forEach(list -> elements.addAll(list));
        return elements;
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(this.width, this.height));
    }

    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }

    public boolean areThereAnimals(Vector2d position) {
        return animals.containsKey(position);
    }

    public void move(Animal animal, int direction) {

        Vector2d currentPosition = animal.getPosition();

        List<Animal> animalList = animals.get(currentPosition);
        if (animalList != null) {
            animalList.remove(animal);

            if (animalList.isEmpty()) {
                animals.remove(currentPosition);
            }
        }

        animal.move(direction, this);
        Vector2d newPosition = animal.getPosition();
        animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
    }

    public List<Animal> reproduction() {
        List<Animal> newAnimalsList = new ArrayList<>();
        for (List<Animal> animalList : animals.values()) {
            if (animalList.size() >= 2) {
                List<Animal> winners = decideWhoWins(animalList);
                Animal parent1 = winners.get(0);
                Animal parent2 = winners.get(1);
                Animal child;
                if (parent1.getEnergy() >= parent2.getEnergy()) {
                    child = parent1.reproduce(parent2);

                } else {
                    child = parent2.reproduce(parent1);
                }


                this.placeAnimal(child);
                newAnimalsList.add(child);
            }
        }
        return newAnimalsList;
    }

    public Animal resolveConflict(List<Animal> contenders) { //wyznacza zwycięzcę konfliktu wedlug zasad
        List<Animal> animals = new ArrayList<>();
        for (Animal animal : contenders) {
            if (animal.getEnergy()>=this.energyToBeFed){
                animals.add(animal);
            }
        }

        if (animals.isEmpty()) {
            return null;
        }

        animals.sort(Comparator.comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getAge).reversed()
                .thenComparingInt(Animal::getChildren).reversed());

        List<Animal> topAnimals = new ArrayList<>();
        int maxEnergy = animals.getFirst().getEnergy();
        int maxAge = animals.getFirst().getAge();
        int maxChildren = animals.getFirst().getChildren();

        for (Animal animal : animals) {
            if (animal.getEnergy() == maxEnergy && animal.getAge() == maxAge && animal.getChildren() == maxChildren) {
                topAnimals.add(animal);
            } else {
                break;
            }
        }

        if (topAnimals.size() == 1) {
            return topAnimals.getFirst();
        } else {
            Random random = new Random();
            return topAnimals.get(random.nextInt(topAnimals.size()));
        }
    }

    public List<Animal> decideWhoWins(List<Animal> animals) { //daje liste zwyciezcow konfliktu, mozna wykorzystac przy jedzeniu też (ten najlepszy to zawsze zerowy indeks)
        Animal winner1 = resolveConflict(animals);
        animals.remove(winner1);
        Animal winner2 = resolveConflict(animals);
        return List.of(winner1, winner2);
    }

    public void consume() {
        for (Vector2d position : plants.keySet()) {
            Plant plant = plants.get(position);
            List<Animal> animalList = animals.get(position);

            if (animalList != null && !animalList.isEmpty()) {
                Animal chosenAnimal = resolveConflict(animalList);
                if (chosenAnimal != null) {
                    chosenAnimal.addEnergy(this.energyPerPlant);
                    plants.remove(position);
                }
            }
        }
    }

    public void removeDeadAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        List<Animal> animalList = animals.get(position);

        if (animalList != null) {
            animalList.remove(animal);

            if (animalList.isEmpty()) {
                animals.remove(position);
            }
        }
    }

    public Set<Vector2d> getPlantsPositions() {
        return plants.keySet();
    }

    public int getPlantCount() {
        return this.plants.size();
    }

    public Set<Vector2d> findPlaceForPlants() {
        Set<Vector2d> freePlaces = new HashSet<>();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                Vector2d position = new Vector2d(x, y);

                if (!this.plants.containsKey(position)) {
                    freePlaces.add(position);
                }
            }
        }

        return freePlaces;
    }

    public Set<Vector2d> findPlaceForPlantsOnEquator(){
        Set<Vector2d> freePlaces = new HashSet<>();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                Vector2d position = new Vector2d(x, y);

                if (!this.plants.containsKey(position) && y >= (int)this.height/2 - (int)this.equatorWidth/2 && y <= (int)this.height/2 + (int)this.equatorWidth/2 ) {
                    freePlaces.add(position);
                }
            }
        }

        return freePlaces;
    }

    public Set<Vector2d> findPlaceForPlantsNotOnEquator(){
        Set<agh.ics.oop.Vector2d> freePlaces = new HashSet<>();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                agh.ics.oop.Vector2d position = new agh.ics.oop.Vector2d(x, y);

                if (!this.plants.containsKey(position) && (y < (int)this.height/2 - (int)this.equatorWidth/2 || y > (int)this.height/2 + (int)this.equatorWidth/2 )) {
                    freePlaces.add(position);
                }
            }
        }

        return freePlaces;
    }
}