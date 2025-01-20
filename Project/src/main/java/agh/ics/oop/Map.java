package agh.ics.oop;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Map implements WorldMap{
    protected java.util.Map<Vector2d, List<Animal> > animals = new HashMap<>();
    protected java.util.Map<Vector2d, Plant> plants = new HashMap<>();
    private int width;
    private int height;
    private int equatorWidth;

    public Map(int width, int heigth) {
        this.width = width;
        this.height = heigth;
        this.equatorWidth = (int) (heigth * 20 / 100);
    }

    public boolean canMoveTo(Vector2d position){
        if (position.getY() < 0 || position.getY() > this.height){
            return false;
        }
        else{
            return true;
        }
    }

    public void placeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        animals.computeIfAbsent(position, k -> new ArrayList<>()).add(animal);
    }

    public void growPlant(int amount) {
        Random random = new Random();
        int cnt = 0;
        while (cnt != amount) {
            int tmp = random.nextInt(5) + 1;
            if (tmp % 5 < 4){
                Vector2d TMP = new Vector2d(random.nextInt(this.width), ThreadLocalRandom.current().nextInt((int) this.height/2 - (int) this.equatorWidth/2, (int) this.height/2 + (int) this.equatorWidth/2));
                if (!plants.containsKey(TMP)){
                    plants.put(TMP, new Plant(TMP));
                    cnt += 1;
                }
            }
            else{
                int tmp1 = random.nextInt(2);
                if (tmp1 % 2 == 1) {
                    Vector2d TMP = new Vector2d(random.nextInt(this.width), random.nextInt((int)this.height/2 - (int)this.equatorWidth/2 ));
                    if (!plants.containsKey(TMP)){
                        plants.put(TMP, new Plant(TMP));
                        cnt += 1;
                    }
                } else {
                    Vector2d TMP = new Vector2d(random.nextInt(this.width),ThreadLocalRandom.current().nextInt((int)this.height/2 + (int)this.equatorWidth/2, this.height + 1));
                    if (!plants.containsKey(TMP)){
                        plants.put(TMP, new Plant(TMP));
                        cnt += 1;
                    }
                }
            }
        }
    }

    @Override
    public List<WorldElement> getElements(){
        List<WorldElement> elements = new ArrayList<>();
        animals.values().forEach(list -> elements.addAll(list));
        return elements;
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary (new Vector2d(0,0), new Vector2d(this.width,this.height));
    }

    public boolean isOccupied(Vector2d position){
        return animals.containsKey(position);
    }
    public boolean areThereAnimals(Vector2d position){
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

        animal.move(direction,this);
        Vector2d newPosition = animal.getPosition();
        animals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
    }

    public void reproduction() {
        for (List<Animal> animalList : animals.values()) {
            if (animalList.size() >= 2) {
                List<Animal> winners = decideWhoWins(animalList);
                Animal parent1 = winners.get(0);
                Animal parent2 = winners.get(1);
                Animal child;
                if (parent1.getEnergy()>=parent2.getEnergy()){
                    child = parent1.reproduce(parent2);
                }
                else{
                    child = parent2.reproduce(parent1);
                }

                placeAnimal(child);
            }
        }
    }

    public Animal resolveConflict(List<Animal> animals) { //wyznacza zwycięzcę konfliktu wedlug zasad
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

    public List<Animal> decideWhoWins(List<Animal> animals){ //daje liste zwyciezcow konfliktu, mozna wykorzystac przy jedzeniu też (ten najlepszy to zawsze zerowy indeks)
        Animal winner1 = resolveConflict(animals);
        animals.remove(winner1);
        Animal winner2 = resolveConflict(animals);
        return List.of(winner1, winner2);
    }

    public void consume(int energy) {
        for (Vector2d position : plants.keySet()) {
            Plant plant = plants.get(position);
            List<Animal> animalList = animals.get(position);

            if (animalList != null && !animalList.isEmpty()) {
                Animal chosenAnimal = resolveConflict(animalList);
                if (chosenAnimal != null) {
                    chosenAnimal.addEnergy(energy);
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

}
