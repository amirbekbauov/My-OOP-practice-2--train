package kz.aitu.practice.practice2;

public class Car {
    private int numberOfPassengers;
    private int capacity;
    public Car(int cap, int numPass){
        this.numberOfPassengers = numPass;
        this.capacity = cap;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public Car(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
