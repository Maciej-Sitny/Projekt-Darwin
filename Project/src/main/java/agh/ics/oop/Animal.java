package agh.ics.oop;

import java.util.ArrayList;
import java.util.Random;

public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation;
    private int energy;
    private ArrayList<Integer> genType;
    private int genNumber;


    public Animal() {
        this.position = new Vector2d(2,2);
        this.orientation = MapDirection.NORTH;
        this.energy = 100;
        GenTypeStartGeneration genGenerator = new GenTypeStartGeneration();
        this.genType = genGenerator.generateGenType(10);
        this.genNumber = 0;
    }

    public Animal(Vector2d position, MapDirection direction, int energy, ArrayList<Integer> genType) {
        this.position = position;
        this.orientation = MapDirection.NORTH;
        this.energy = energy;
        this.genType = genType;
        this.genNumber = 0;
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
            if (tmp.getX() > 4) {
                this.position = new Vector2d(0, tmp.getY());
            } else if (tmp.getX() < 0) {
                this.position = new Vector2d(4, tmp.getY());
            } else {
                this.position = tmp;
            }
        }
        else {
            this.orientation=this.orientation.back();
        }

    }
}


