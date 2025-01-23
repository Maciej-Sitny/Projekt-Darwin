package agh.ics.oop.observers;

import agh.ics.oop.Animal;
import agh.ics.oop.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Observer {
    private Animal animal;

    public Observer(Animal animal) {
        this.animal = animal;
    }

    // Metoda aktualizująca stan obserwatora
    public void update(String genType, int genNumber, int children, int age, int energy, Vector2d position) {
        // Przykład logowania
        System.out.println("Zwierzak zaktualizowany:");
        System.out.println("Pozycja: " + position);
        System.out.println("Genotyp: " + genType);
        System.out.println("Numer genu: " + genNumber);
        System.out.println("Liczba dzieci: " + children);
        System.out.println("Wiek: " + age);
        if (energy > 0) {
            System.out.println("Energia: " + energy);
        } else {
            System.out.println("Zwierzak zmarł.");
        }
    }
    public ArrayList<Integer> getGenType(){
        return animal.getGenType();
    }

    public int getGenNumber(){
        return animal.getGenNumber();
    }

    public int getChildren(){
        return animal.getChildren();
    }

    public int getAge(){
        return animal.getAge();
    }

    public int getEnergy(){
        return animal.getEnergy();
    }

    public Vector2d getPosition(){
        return animal.getPosition();
    }



}