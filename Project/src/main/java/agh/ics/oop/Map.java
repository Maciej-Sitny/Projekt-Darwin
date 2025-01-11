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
        animals.get(animal.getPosition()).add(animal);
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

    public void move(Animal animal, int direction){

    }
}
