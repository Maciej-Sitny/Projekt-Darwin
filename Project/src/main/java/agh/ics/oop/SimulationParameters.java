package agh.ics.oop;

public class SimulationParameters {
    private int mapHeight;
    private int mapWidth;
    private String mapVariant;
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
    public String getMapVariant() {
        return mapVariant;
    }
    public void setMapVariant(String mapVariant) {
        this.mapVariant = mapVariant;
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
}