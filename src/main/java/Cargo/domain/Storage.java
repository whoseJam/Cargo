package Cargo.domain;

public class Storage implements Standstill {
    private double size;
    private Location location;

    public Storage() {
    }

    public Storage(double size, Location location) {
        this.size = size;
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public double getSize() {
        return size;
    }
}
