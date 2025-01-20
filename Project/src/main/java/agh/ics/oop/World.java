package agh.ics.oop;


import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Application;

public class World {
    public static void main(String[] args) {
        Application.launch(SimulationPresenter.class, args); // Tylko uruchamia UI
    }
}