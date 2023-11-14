package Cargo.domain;

import Cargo.score.ScoreCalculator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import net.minidev.json.annotate.JsonIgnore;
import org.json.JSONObject;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
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

    public UUID id;

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
    }

    private JSONObject toJson(Standstill next, int load) {
        JSONObject ans = new JSONObject();
        ans.put("location", next.getLocation().getName());
        ans.put("latitude", next.getLocation().getLatitude());
        ans.put("longitude", next.getLocation().getLongitude());
        ans.put("load", load);
        return ans;
    }

    private JSONObject toJson(Standstill next) {
        JSONObject ans = new JSONObject();
        ans.put("location", next.getLocation().getName());
        ans.put("latitude", next.getLocation().getLatitude());
        ans.put("longitude", next.getLocation().getLongitude());
        if (next instanceof Recipient) {
            Recipient rep = (Recipient) next;
            ans.put("drop", rep.getSize());
            ans.put("rep_id", rep.getId());
        }
        return ans;
    }

    private JSONObject toJson(List<Standstill> path) {
        Car car = (Car) path.get(0);
        List<JSONObject> points = new ArrayList<>();
        points.add(toJson(car));
        for (int i = 1; i < path.size(); i++) {
            Recipient now = (Recipient) path.get(i);
            if (i == 1 || ((Recipient) path.get(i - 1)).getFrom() != now.getFrom()) {
                int j = i;
                while (j + 1 < path.size()) {
                    Recipient next = (Recipient) path.get(j + 1);
                    if (now.getFrom() == next.getFrom()) j++;
                    else break;

                }
                int load = 0;
                for (int k = i; k <= j; k++) load += ((Recipient) path.get(k)).getSize();
                points.add(toJson(now.getFrom(), load));
                points.add(toJson(now));
            } else {
                points.add(toJson(now));
            }
        }
        JSONObject ans = new JSONObject();
        ans.put("car_id", car.getId());
        ans.put("path", points.toArray());
        return ans;
    }

    public JSONObject toJson() {
        HashMap<Standstill, Standstill> edges = new HashMap<>();
        for (Recipient recipient : recipients) {
            Standstill previous = recipient.getPreviousStandstill();
            if (previous == null) continue;
            Location previousLocation = previous.getLocation();
            if (previousLocation == null) continue;
            Location currentLocation = recipient.getLocation();
            if (currentLocation == null) continue;
            edges.put(previous, recipient);
        }

        List<JSONObject> carList = new ArrayList<>();
        for (Car car : cars) {
            Standstill cur = car;
            List<Standstill> path = new ArrayList<>();
            path.add(cur);
            while (edges.containsKey(cur)) {
                Standstill next = edges.get(cur);
                path.add(next);
                cur = next;
            }

            JSONObject ans = toJson(path);
            carList.add(ans);
        }

        JSONObject ans = new JSONObject();
        ans.put("cars", carList.toArray());
        HardMediumSoftLongScore score =  new ScoreCalculator().calculateScore(this);
        ans.put("hard_score", score.hardScore());
        ans.put("medium_score", score.mediumScore());
        ans.put("soft_score", score.softScore());
        return ans;
    }
}
