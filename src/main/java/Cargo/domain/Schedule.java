package Cargo.domain;

import Cargo.score.ScoreCalculator;
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
    @ValueRangeProvider(id = "carProvider")
    private List<Car> cars;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "recipientProvider")
    private List<Recipient> recipients;

    @ProblemFactProperty
    @ValueRangeProvider
    private List<Storage> storages;

    @PlanningScore
    private HardMediumSoftLongScore score;

    public Schedule() {
    }

    public Schedule(
        List<Car> cars,
        List<Recipient> recipients,
        List<Storage> storages) {
        this.cars = cars;
        this.recipients = recipients;
        this.storages = storages;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public List<Storage> getStorages() {
        return storages;
    }

    @Override
    public String toString() {
        return new ScoreCalculator().calculateScore(this).toString();
//        StringBuilder builder = new StringBuilder();
//        Map<Standstill, Standstill> edges = new HashMap<>();
//        for (Recipient recipient : recipients) {
//            Standstill previous = recipient.getPreviousStandstill();
//            if (previous != null) edges.put(previous, recipient);
//            System.out.println("current=" + recipient.toString() + " previous=" + previous.toString());
//        }
//        for (Car car : cars) {
//            Standstill cur = car;
//            if (car.isDummy()) continue;
//            List<Standstill> path = new ArrayList<>();
//            path.add(cur);
//            double dist = 0;
//            while (edges.containsKey(cur)) {
//                Standstill next = edges.get(cur);
//                path.add(next);
//                dist += cur.getLocation().getDistanceTo(next.getLocation());
//                cur = next;
//            }
//            builder.append(car.toString());
//            for (int l = 1, r; l < path.size(); l = r + 1) {
//                Standstill now = path.get(r = l);
//                builder.append(now.getLocation().toString());
//                if (r + 1 < path.size()) builder.append(" -> ");
//            }
//            builder.append(" dist = " + ((int) dist));
//            builder.append("\n");
//        }
//        return new String(builder);
    }
}
