package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Recipient;
import Cargo.domain.Schedule;
import Cargo.domain.Standstill;
import Cargo.domain.Storage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.HashMap;
import java.util.Map;

public class Painter {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    private Location realLocation(Canvas canvas, Location location) {
        double margin = 75;
        double dx = location.getLatitude() - minLatitude;
        double tx = maxLatitude - minLatitude;
        double nx = dx / tx * (canvas.getWidth() - margin * 2) + margin;
        double dy = location.getLongitude() - minLongitude;
        double ty = maxLongitude - minLongitude;
        double ny = dy / ty * (canvas.getHeight() - margin * 2) + margin;
        return new Location(location.getName(), nx, ny);
    }

    private void paintLocation(Canvas canvas, GraphicsContext gc, Location location, int type) {
        location = realLocation(canvas, location);
        if ((type & (1 << 2)) != 0) {
            double r = 10;
            gc.setFill(Color.AQUAMARINE);
            gc.fillArc(
                location.getLatitude() - r / 2,
                location.getLongitude() - r / 2,
                r, r, 360, 360, ArcType.CHORD);
        }
        if ((type & (1 << 1)) != 0) {
            double r = 6;
            gc.setFill(Color.GAINSBORO);
            gc.fillArc(
                location.getLatitude() - r / 2,
                location.getLongitude() - r / 2,
                r, r, 360, 360, ArcType.CHORD);
        }
        if ((type & 1) != 0) {
            double r = 3;
            gc.setFill(Color.BLACK);
            gc.fillArc(
                location.getLatitude() - r / 2,
                location.getLongitude() - r / 2,
                r, r, 360, 360, ArcType.CHORD);
        }
        gc.setFill(Color.BLACK);
        if ((type & (1 << 2)) == 0)
            gc.fillText(location.getName(), location.getLatitude(), location.getLongitude());
    }

    private void paintPath(Canvas canvas, GraphicsContext gc, Location from, Location to) {
        from = realLocation(canvas, from);
        to = realLocation(canvas, to);
        gc.strokeLine(
            from.getLatitude(), from.getLongitude(),
            to.getLatitude(), to.getLongitude());
    }

    private void paintPaths(Canvas canvas, GraphicsContext gc, Schedule solution) {
        HashMap<Standstill, Standstill> edges = new HashMap<>();
        for (Recipient recipient : solution.getRecipients()) {
            Standstill previous = recipient.getPreviousStandstill();
            if (previous == null) continue;
            Location previousLocation = previous.getLocation();
            if (previousLocation == null) continue;
            Location currentLocation = recipient.getLocation();
            if (currentLocation == null) continue;
            edges.put(previous, recipient);
        }
        for (Car car : solution.getCars()) {
            int pathLength = 0;
            Standstill current = car;
            gc.setStroke(car.getColor());
            while (edges.containsKey(current)) {
                Recipient next = (Recipient) edges.get(current);
                if (current instanceof Car || ((Recipient) current).getFrom() != next.getFrom()) {
                    Storage storage = next.getFrom();
                    paintPath(canvas, gc, current.getLocation(), storage.getLocation());
                    paintPath(canvas, gc, storage.getLocation(), next.getLocation());
                } else {
                    paintPath(canvas, gc, current.getLocation(), next.getLocation());
                }
                current = next;
                pathLength++;
            }
            System.out.println("path length=" + pathLength);
        }
    }

    public void paint(Canvas canvas, Schedule solution) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        HashMap<Location, Integer> locations = new HashMap<>();
        for (Car car : solution.getCars()) {
            Location current = car.getLocation();
            if (!locations.containsKey(current))
                locations.put(current, 0);
            locations.put(current,
                locations.get(current) | 1);
        }
        for (Storage storage : solution.getStorages()) {
            Location current = storage.getLocation();
            if (!locations.containsKey(current))
                locations.put(current, 0);
            locations.put(current,
                locations.get(current) | (1<<1));
        }
        for (Recipient recipient : solution.getRecipients()) {
            Location current = recipient.getLocation();
            if (!locations.containsKey(current))
                locations.put(current, 0);
            locations.put(current,
                locations.get(current) | (1<<2));
        }

        int i = 0;
        for (Location location : locations.keySet()) {
            if (i == 0) {
                minLatitude = maxLatitude = location.getLatitude();
                minLongitude = maxLongitude = location.getLongitude();
                i++;
            } else {
                minLatitude = Math.min(minLatitude, location.getLatitude());
                maxLatitude = Math.max(maxLatitude, location.getLatitude());
                minLongitude = Math.min(minLongitude, location.getLongitude());
                maxLongitude = Math.max(maxLongitude, location.getLongitude());
            }
        }
        System.out.println("Longitude=" + minLongitude+ " , " + maxLongitude);
        for (Map.Entry<Location, Integer> entry : locations.entrySet()) {
            paintLocation(canvas, gc, entry.getKey(), entry.getValue());
        }
        paintPaths(canvas, gc, solution);
    }
}
