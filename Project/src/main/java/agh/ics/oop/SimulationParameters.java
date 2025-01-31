package agh.ics.oop;
import java.io.*;
import java.util.Properties;
public class SimulationParameters {
    private int mapHeight;
    private int mapWidth;
    private int initialPlants;
    private int energyPerPlant;
    private int plantsPerDay;
    private int initialAnimals;
    private int initialEnergy;
    private int energyToBeFed;
    private int energyUsedByParents;
    private int minMutations;
    private int maxMutations;
    private String mutationVariant;
    private int genomeLength;
    private int energyLost;
    private String mapType;
    private boolean saveDataToFile;
    public SimulationParameters(int mapHeight, int mapWidth, int initialPlants, int energyPerPlant, int plantsPerDay, int initialAnimals, int initialEnergy, int energyToBeFed, int energyUsedByParents, int minMutations, int maxMutations, String mutationVariant, int genomeLength, int energyLost, String mapType, Boolean saveDataToFile) {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.initialPlants = initialPlants;
        this.energyPerPlant = energyPerPlant;
        this.plantsPerDay = plantsPerDay;
        this.initialAnimals = initialAnimals;
        this.initialEnergy = initialEnergy;
        this.energyToBeFed = energyToBeFed;
        this.energyUsedByParents = energyUsedByParents;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.mutationVariant = mutationVariant;
        this.genomeLength = genomeLength;
        this.energyLost = energyLost;
        this.mapType = mapType;
        this.saveDataToFile = saveDataToFile;
    }


    public int getMapHeight() {
        return mapHeight;
    }
    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
    public int getMapWidth() {
        return mapWidth;
    }
    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }
    public int getInitialPlants() {
        return initialPlants;
    }
    public void setInitialPlants(int initialPlants) {
        this.initialPlants = initialPlants;
    }
    public int getEnergyPerPlant() {
        return energyPerPlant;
    }
    public void setEnergyPerPlant(int energyPerPlant) {
        this.energyPerPlant = energyPerPlant;
    }
    public int getPlantsPerDay() {
        return plantsPerDay;
    }
    public void setPlantsPerDay(int plantsPerDay) {
        this.plantsPerDay = plantsPerDay;
    }
    public int getInitialAnimals() {
        return initialAnimals;
    }
    public void setInitialAnimals(int initialAnimals) {
        this.initialAnimals = initialAnimals;
    }
    public int getInitialEnergy() {
        return initialEnergy;
    }
    public void setInitialEnergy(int initialEnergy) {
        this.initialEnergy = initialEnergy;
    }
    public int getEnergyToBeFed() {
        return energyToBeFed;
    }
    public void setEnergyToBeFed(int energyToBeFed) {
        this.energyToBeFed = energyToBeFed;
    }
    public int getEnergyUsedByParents() {
        return energyUsedByParents;
    }
    public void setEnergyUsedByParents(int energyUsedByParents) {
        this.energyUsedByParents = energyUsedByParents;
    }
    public int getMinMutations() {
        return minMutations;
    }
    public void setMinMutations(int minMutations) {
        this.minMutations = minMutations;
    }
    public int getMaxMutations() {
        return maxMutations;
    }
    public void setMaxMutations(int maxMutations) {
        this.maxMutations = maxMutations;
    }
    public String getMutationVariant() {
        return mutationVariant;
    }
    public void setMutationVariant(String mutationVariant) {
        this.mutationVariant = mutationVariant;
    }
    public int getGenomeLength() {
        return genomeLength;
    }
    public void setGenomeLength(int genomeLength) {
        this.genomeLength = genomeLength;
    }
    public int getEnergyLost() {
        return energyLost;
    }
    public void setEnergyLost(int energyLost) {
        this.energyLost = energyLost;
    }
    public String getMapType() {
        return mapType;
    }
    public void setMapType(String mapType) {
        this.mapType = mapType;
    }
    public boolean isSaveDataToFile() {
        return saveDataToFile;
    }

    public void saveToFile(String fileName) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("mapHeight", String.valueOf(mapHeight));
        properties.setProperty("mapWidth", String.valueOf(mapWidth));
        properties.setProperty("initialPlants", String.valueOf(initialPlants));
        properties.setProperty("energyPerPlant", String.valueOf(energyPerPlant));
        properties.setProperty("plantsPerDay", String.valueOf(plantsPerDay));
        properties.setProperty("initialAnimals", String.valueOf(initialAnimals));
        properties.setProperty("initialEnergy", String.valueOf(initialEnergy));
        properties.setProperty("energyToBeFed", String.valueOf(energyToBeFed));
        properties.setProperty("energyUsedByParents", String.valueOf(energyUsedByParents));
        properties.setProperty("minMutations", String.valueOf(minMutations));
        properties.setProperty("maxMutations", String.valueOf(maxMutations));
        properties.setProperty("mutationVariant", mutationVariant);
        properties.setProperty("genomeLength", String.valueOf(genomeLength));
        properties.setProperty("energyLost", String.valueOf(energyLost));
        properties.setProperty("mapType", mapType);
        properties.setProperty("saveDataToFile", String.valueOf(saveDataToFile));

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            properties.store(fos, "Simulation Parameters");
        }
    }

    public static SimulationParameters loadFromFile(String fileName) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileName)) {
            properties.load(fis);
        }

        return new SimulationParameters(
                Integer.parseInt(properties.getProperty("mapHeight")),
                Integer.parseInt(properties.getProperty("mapWidth")),
                Integer.parseInt(properties.getProperty("initialPlants")),
                Integer.parseInt(properties.getProperty("energyPerPlant")),
                Integer.parseInt(properties.getProperty("plantsPerDay")),
                Integer.parseInt(properties.getProperty("initialAnimals")),
                Integer.parseInt(properties.getProperty("initialEnergy")),
                Integer.parseInt(properties.getProperty("energyToBeFed")),
                Integer.parseInt(properties.getProperty("energyUsedByParents")),
                Integer.parseInt(properties.getProperty("minMutations")),
                Integer.parseInt(properties.getProperty("maxMutations")),
                properties.getProperty("mutationVariant"),
                Integer.parseInt(properties.getProperty("genomeLength")),
                Integer.parseInt(properties.getProperty("energyLost")),
                properties.getProperty("mapType"),
                Boolean.parseBoolean(properties.getProperty("saveDataToFile"))
        );
    }
}