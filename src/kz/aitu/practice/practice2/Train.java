package kz.aitu.practice.practice2;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private List<Locomotive> locomotives;
    private List<Car> cars;

    public Train() {
        this.locomotives = new ArrayList<>();
        this.cars = new ArrayList<>();
    }

    public void addLocomotive(Locomotive locomotive) {
        locomotives.add(locomotive);
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Locomotive> getLocomotives() {
        return locomotives;
    }

    public List<Car> getCars() {
        return cars;
    }

    public int getTotalCapacity() {
        int totalCapacity = 0;

        for (Locomotive locomotive : locomotives) {
            totalCapacity += locomotive.getCapacity();
        }

        for (Car car : cars) {
            totalCapacity += car.getCapacity();
        }

        return totalCapacity;
    }

    public int getNumberOfPassengers(){
        int totalNumberOfPassengers = 0;


        for (Car car : cars) {
            totalNumberOfPassengers += car.getNumberOfPassengers();
        }

        return totalNumberOfPassengers;
    }


}
