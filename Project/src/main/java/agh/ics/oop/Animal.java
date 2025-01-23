package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation;
    private int energy;
    private ArrayList<Integer> genType;
    private int genNumber;
    private int children = 0;
    private int age = 0;
    private SimulationParameters parameters;



    public Animal() {
        this.position = new Vector2d(2,2);
        this.orientation = MapDirection.NORTH;
        this.energy = 100;
        GenTypeStartGeneration genGenerator = new GenTypeStartGeneration();
        this.genType = genGenerator.generateGenType(10);
        this.genNumber = 0;
    }

    public Animal(Vector2d position, int energy, ArrayList<Integer> genType) {
        this.position = position;
        this.orientation = MapDirection.NORTH;
        this.energy = energy;
        this.genType = genType;
        this.genNumber = 0;
    }

    public Animal(Vector2d position, MapDirection orientation, int energy, ArrayList<Integer> genType, int genNumber, SimulationParameters parameters){ //dodatkowy konstruktor specjalnie pod nowonarodzone dziecko (według opisu projektu dziecko ma losową orientację)
        this.position = position;
        this.orientation = orientation;
        this.energy = energy;
        this.genType = genType;
        this.genNumber = genNumber;
        this.parameters = parameters;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public MapDirection getOrientation() {
        return this.orientation;
    }

    public int getEnergy(){
        return this.energy;
    }

    public void removeEnergy(int amount){
        this.energy = this.energy - amount;
    }

    public void addEnergy(int amount){
        this.energy = this.energy + amount;
    }

    public ArrayList<Integer> getGenType() {
        return genType;
    }

    public int getGenNumber(){
        return this.genNumber;
    }

    public void nextGen() {
        if (this.genNumber + 1 == this.genType.size()){
            this.genNumber = 0;
        } else {
            this.genNumber += 1;
        }
    }

    @Override
    public String toString() {
        return switch (this.orientation) {
            case SOUTH -> "S";
            case SOUTHEAST -> "SE";
            case EAST -> "E";
            case NORTHEAST -> "NE";
            case NORTH -> "N";
            case NORTHWEST -> "NW";
            case WEST -> "W";
            case SOUTHWEST -> "SW";
        };
    }


    public void move(int direction, MoveValidator moveValidator){
        this.orientation = this.orientation.new_direction(direction);
        Vector2d tmp = this.position.add(this.orientation.toUnitVector());
        if (moveValidator.canMoveTo(tmp)) {
            if (tmp.getX() > parameters.getMapWidth()) {
                this.position = new Vector2d(0, tmp.getY());
            } else if (tmp.getX() < 0) {
                this.position = new Vector2d(parameters.getMapWidth()-1, tmp.getY());
            } else {
                this.position = tmp;
            }
        }
        else {
            this.orientation=this.orientation.back();
        }

    }

    public int getAge(){
        return this.age;
    }

    public void addAge(){
        this.age += 1;
    }

    public void addChild(){
        this.children += 1;
    }

    public int getChildren(){
        return this.children;
    }

    public void removeChild(){
        this.children -= 1;
    }

    public Animal reproduce(Animal parent2){ //parent2 jest zawsze słabszy od this TRZEBA DODAĆ ILE ENERGII DOSTAJE DZIECKO I TRACA RODZICE
        ArrayList<Integer> genType = new ArrayList<>();
        Random random = new Random();
        int sumOfEnergy = this.energy + parent2.energy;

        int ratioParent1= this.energy/sumOfEnergy;
        int ratioParent2= parent2.energy/sumOfEnergy;

        int choice = (int) (random.nextInt(2)); // 0 to lewa strona, 1 to prawa strona

        int lengthOfParent1=(int) this.genType.size()*ratioParent1;
        int lengthOfParent2=parent2.genType.size()-lengthOfParent1;

        if (choice==0){
            for (int i = 0; i < lengthOfParent1; i++){
                genType.add(this.genType.get(i));
            }
            for (int i = lengthOfParent1; i < parent2.genType.size(); i++){
                genType.add(parent2.genType.get(i));
            }
        }
        else if (choice==1){
            for (int i = 0; i < lengthOfParent2; i++){
                genType.add(parent2.genType.get(i));
            }
            for (int i = lengthOfParent2; i < this.genType.size(); i++){
                genType.add(this.genType.get(i));
            }
        }
        ArrayList<Integer> newGenType = new ArrayList<>();
        if (parameters.getMutationVariant()=="Pełna losowość"){
            newGenType = mutationFullRandomness(genType);
        }
        else if (parameters.getMutationVariant()=="Podmianka"){
            newGenType = mutationSwap(genType);
        }
        this.removeEnergy(this.parameters.getEnergyUsedByParents());
        parent2.removeEnergy(this.parameters.getEnergyUsedByParents());

        return new Animal(this.position, this.getRandomDirection(), 2*this.parameters.getEnergyUsedByParents(), newGenType, random.nextInt(8), parameters); //tutaj trzeba poprawić potem jeszcze tą ilość energii
    }


    public ArrayList<Integer> mutationFullRandomness(ArrayList<Integer> genType){
        ArrayList<Integer> newGenType = new ArrayList<>();
        ArrayList<Integer> randomIndexes = new ArrayList<>();
        Random random = new Random();
        int amount = (random.nextInt(parameters.getMinMutations(), parameters.getMaxMutations()+1));
        int randomIndex;

        for (int i=0; i<amount;i++) {
            randomIndex = (random.nextInt(genType.size()));
            if (!randomIndexes.contains(randomIndex)) {
                randomIndexes.add(randomIndex);
            }
        }

        for (int i=0; i<genType.size();i++){
            if (randomIndexes.contains(i)){
                newGenType.add(random.nextInt(8));
            }
            else{
                newGenType.add(genType.get(i));
            }
        }
        return newGenType;
    }

    public ArrayList<Integer> mutationSwap(ArrayList<Integer> genType){
        ArrayList<Integer> newGenType = new ArrayList<>(genType); //kopiowanie genotypu
        Random random = new Random();
        int amount = random.nextInt(this.parameters.getMinMutations(), this.parameters.getMaxMutations()+1); //losowanie ilości podmianek
        for (int i=0; i<amount;i++){
            int randomIndex1 = random.nextInt(genType.size());
            int randomIndex2 = random.nextInt(genType.size());

            while (randomIndex1 == randomIndex2){ //upewnienie się, że indeksy są różne
                randomIndex2 = random.nextInt(genType.size());
            }

            int tmp = newGenType.get(randomIndex1);
            newGenType.set(randomIndex1, newGenType.get(randomIndex2));
            newGenType.set(randomIndex2, tmp);
        }
        return newGenType;
    }

    private MapDirection getRandomDirection() {  // ewentualne błędy mogą wyjść z tego (potrzebuję tego do losowej orientacji zwierzaka przy narodzeniu)
        MapDirection[] directions = MapDirection.values();
        return directions[new Random().nextInt(directions.length)];
    }
}


