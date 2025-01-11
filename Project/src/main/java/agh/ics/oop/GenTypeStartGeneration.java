package agh.ics.oop;

import java.util.ArrayList;
import java.util.Random;

public class GenTypeStartGeneration {

    public ArrayList<Integer> generateGenType(int size) {
        ArrayList<Integer> numbers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            numbers.add(random.nextInt(8));
        }
        return numbers;
    }
}
