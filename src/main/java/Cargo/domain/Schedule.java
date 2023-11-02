package Cargo.domain;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PlanningSolution
public class Schedule {

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Car> cars;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    private List<Visit> visits;

    @PlanningScore
    private HardMediumSoftLongScore score;

    public Schedule() {
    }

    public Schedule(
        List<Car> cars,
        List<Visit> visits) {
        cars.add(new Car());
        this.cars = cars;
        this.visits = visits;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Map<Standstill, Standstill> edges = new HashMap<>();
        for (Visit visit : visits) {
            Standstill previous = visit.getPreviousStandstill();
            if (previous != null) edges.put(previous, visit);
        }
        for (Car car : cars) {
            Standstill cur = car;
            if (car.isDummy()) continue;
            List<Standstill> path = new ArrayList<>();
            path.add(cur);
            double dist = 0;
            while (edges.containsKey(cur)) {
                Standstill next = edges.get(cur);
                path.add(next);
                dist += cur.getLocation().getDistanceTo(next.getLocation());
                cur = next;
            }
            builder.append(car.toString());
            for (int l = 1, r; l < path.size(); l = r + 1) {
                Standstill now = path.get(r = l);
                while (r + 1 < path.size()) {
                    Standstill next = path.get(r + 1);
                    if (now.getLocation() == next.getLocation()) r++;
                    else break;
                }
                int size = 0;
                for (int i = l; i <= r; i++) size += ((Visit) path.get(i)).getSize();
                builder.append(now.getLocation().toString() + "(" + size + ")");
                if (r + 1 < path.size()) builder.append(" -> ");
            }
            builder.append(" dist = " + ((int) dist));
            builder.append("\n");
        }
        return new String(builder);
    }
}
