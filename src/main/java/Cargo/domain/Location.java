package Cargo.domain;

import org.springframework.stereotype.Component;

@Component
public class Location {
    private String name;
    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getDistanceTo(Location other) {
        double latitudeDiff = this.latitude - other.latitude;
        double longtitudeDiff = this.longitude - other.longitude;
//        return Math.abs(latitudeDiff) + Math.abs(longtitudeDiff);
        return Math.sqrt(latitudeDiff * latitudeDiff + longtitudeDiff * longtitudeDiff);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "(" + latitude + ", " + longitude + ")";
    }
}
