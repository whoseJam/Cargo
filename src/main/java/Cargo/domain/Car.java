package Cargo.domain;

import javafx.scene.paint.Color;

public class Car implements Standstill {
    private int id;
    private double capacity;
    private Location startLocation;
    private Color color = Color.BLACK;

    @Override
    public Location getLocation() {
        return startLocation;
    }

    public Car() {
    }

    public Car(int id, int capacity, Location startLocation) {
        this.id = id;
        this.capacity = capacity;
        this.startLocation = startLocation;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "car " + id + ": ";
    }
}
