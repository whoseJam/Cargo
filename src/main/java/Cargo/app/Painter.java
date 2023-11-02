package Cargo.app;

import Cargo.domain.Car;
import Cargo.domain.Location;
import Cargo.domain.Schedule;
import Cargo.domain.Standstill;
import Cargo.domain.Visit;
import com.sun.marlin.DCollinearSimplifier;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import javax.swing.plaf.IconUIResource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Painter {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    private Location realLocation(Canvas canvas, Location location) {
        double margin = 25;
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
        for (Visit visit : solution.getVisits()) {
            Standstill previous = visit.getPreviousStandstill();
            if (previous == null) continue;
            Location previousLocation = previous.getLocation();
            if (previousLocation == null) continue;
            Location currentLocation = visit.getLocation();
            if (currentLocation == null) continue;
            edges.put(previous, visit);
        }
        for (Car car : solution.getCars()) {
            Standstill current = car;
            gc.setStroke(car.getColor());
            while (edges.containsKey(current)) {
                Standstill next = edges.get(current);
                paintPath(canvas, gc, current.getLocation(), next.getLocation());
                current = next;
            }
        }
    }

    public void paint(Canvas canvas, Schedule solution) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        HashMap<Location, Integer> locations = new HashMap<>();
        for (Car car : solution.getCars()) {
            if (car.isDummy()) continue;
            Location current = car.getLocation();
            if (!locations.containsKey(current))
                locations.put(current, 0);
            locations.put(current,
                locations.get(current) | 1);
        }
        for (Visit visit: solution.getVisits()) {
            Location current = visit.getLocation();
            if (!locations.containsKey(current))
                locations.put(current, 0);
            int k = (visit.getSize() > 0) ? 1 : 2;
            locations.put(current,
                locations.get(current) | (1<<k));
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
